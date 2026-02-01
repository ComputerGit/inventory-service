package com.at.t.ecommerce.inventory.domain.stock.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.at.t.ecommerce.inventory.domain.stock.enums.*;
import com.at.t.ecommerce.inventory.domain.stock.events.StockEvent;
import com.at.t.ecommerce.inventory.domain.stock.exceptions.InsufficientStockException;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;
import com.at.t.ecommerce.inventory.domain.stock.events.StockReserved;

/**
 * The Stock Aggregate Root. This class represents a specific 'bucket' of
 * inventory defined by its Product, Warehouse, and Owner. It manages the
 * lifecycle of stock from reservation to shipment.
 */
public class Stock {

	// --- 1. IDENTITY ---
	private final StockId id;
	private final ProductId productId;
	private final WarehouseId warehouseId;
	private final Owner ownerId;
	private final UnitOfMeasure unitOfMeasure;

	// --- 2. QUANTITY BUCKETS (STATE) ---
	private StockCondition stockCondition;
	private Quantity quantityOnHand; // Physical stock currently sitting on shelves.
	private Quantity quantityReserved; // Stock "locked" by customer orders/carts.
	private Quantity quantityAllocated; // Stock currently being picked by warehouse staff.
	private Quantity quantityInTransit; // Stock confirmed as shipped from supplier but not yet received.
	private Quantity discrepancyGap; // Difference between system count and physical count.
	private Quantity safetyStock; // Buffer to prevent overselling due to theft/damage.

	// --- 3. POLICIES & THRESHOLDS ---
	private final Quantity lowStockThreshold;
	private final Quantity maxStockThreshold;
	private VelocityCode velocityCode;
	private final RotationPolicy rotationPolicy;

	// --- 4. LIFECYCLE & AUDIT ---
	private LocalDate expiryDate;
	private LifeCycleStatus lifecycleStatus;
	private Instant lastUpdated;
	private Long version;
	private LocalDate lastAuditDate;

	// --- CONSTRUCTOR ---
	public Stock(StockId stockId, ProductId productId, WarehouseId warehouseId, Owner ownerId, RotationPolicy rotationPolicy,
			Quantity lowStockThreshold, Quantity maxStockThreshold , UnitOfMeasure unitOfMeasure) {

		this.productId = Objects.requireNonNull(productId);
		this.warehouseId = Objects.requireNonNull(warehouseId);
		this.ownerId = Objects.requireNonNull(ownerId);
		this.rotationPolicy = Objects.requireNonNull(rotationPolicy);
		this.lowStockThreshold = Objects.requireNonNull(lowStockThreshold);
		this.maxStockThreshold = Objects.requireNonNull(maxStockThreshold);
		this.unitOfMeasure = Objects.requireNonNull(unitOfMeasure);

		if (lowStockThreshold.unit() != unitOfMeasure || maxStockThreshold.isLessThan(lowStockThreshold)) {
			throw new IllegalArgumentException("Maximum limit cannot be lower than re-order point (Low Threshold).");
		}

		this.id = StockId.newId();
		this.stockCondition = StockCondition.NEW;
		this.velocityCode = VelocityCode.B_STANDARD;
		this.lifecycleStatus = LifeCycleStatus.ACTIVE;

		this.quantityOnHand = Quantity.zero(unitOfMeasure);
		this.quantityReserved = Quantity.zero(unitOfMeasure);
		this.quantityAllocated = Quantity.zero(unitOfMeasure);
		this.quantityInTransit = Quantity.zero(unitOfMeasure);
		this.discrepancyGap = Quantity.zero(unitOfMeasure);
		this.safetyStock = Quantity.zero(unitOfMeasure);

		this.version = 0L;
		this.lastUpdated = Instant.now();
		this.lastAuditDate = LocalDate.now();

		validateInvariants();
	}
	
	private final List<StockEvent> domainEvents = new ArrayList<>();
	
	// New Helper Method
    public List<StockEvent> pullDomainEvents() {
        List<StockEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    private void registerEvent(StockEvent event) {
        this.domainEvents.add(event);
    }

	// --- DOMAIN BEHAVIORS ---

	/**
	 * Calculates stock "Available To Promise" (ATP). This is the amount we can
	 * safely tell a customer is available to buy.
	 */
	public Quantity getAvailableToPromise() {
		return quantityOnHand.add(quantityInTransit).subtract(quantityReserved).subtract(safetyStock);
	}

	/**
	 * STEP 1: RESERVE (Order Created) Locks the item so no other customer can buy
	 * it. Stock stays 'On Hand' physically but is virtually marked as 'Reserved'.
	 */
	public void reserveStock(Quantity amount) {
		Quantity available = getAvailableToPromise();
		if (amount.isGreaterThan(available)) {
			throw new InsufficientStockException(productId, amount, available);
		}
		this.quantityReserved = this.quantityReserved.add(amount);
        this.lastUpdated = Instant.now();
        validateInvariants();

        // ✅ The Voice: Register the event
        registerEvent(StockReserved.now(this.id, this.productId, amount));
	}

	/**
	 * STEP 2: ALLOCATE (Picking Started) Moves stock from 'Reserved' to 'Allocated'
	 * when the warehouse starts picking the order. This prevents the stock from
	 * being released back to inventory accidentally.
	 */
	public void allocateStock(Quantity amount) {
		if (amount.isGreaterThan(quantityReserved)) {
			throw new IllegalStateException("Cannot allocate: Requested amount exceeds current reservations.");
		}
		this.quantityReserved = this.quantityReserved.subtract(amount);
		this.quantityAllocated = this.quantityAllocated.add(amount);
		this.lastUpdated = Instant.now();
		validateInvariants();
	}

	/**
	 * STEP 3: SHIP (Order Dispatched) Physically removes the item from the
	 * warehouse. OnHand and Allocated both decrease.
	 */
	public void confirmShipment(Quantity amount) {
		if (amount.isGreaterThan(quantityAllocated)) {
			throw new IllegalStateException("Cannot ship: Requested amount was never allocated.");
		}
		this.quantityAllocated = this.quantityAllocated.subtract(amount);
		this.quantityOnHand = this.quantityOnHand.subtract(amount);
		this.lastUpdated = Instant.now();
		validateInvariants();
	}

	/**
	 * RELEASE (Order Cancelled) Removes the reservation and makes the stock
	 * available for other customers again.
	 */
	public void releaseStock(Quantity amount) {
		if (amount.isGreaterThan(quantityReserved)) {
			throw new IllegalStateException("Cannot release: Requested amount exceeds reserved quantity.");
		}
		this.quantityReserved = this.quantityReserved.subtract(amount);
		this.lastUpdated = Instant.now();
		validateInvariants();
	}

	/**
	 * RESTORE (Returns / Failed Delivery) Adds stock back to the physical 'On Hand'
	 * bucket (e.g., customer returned a phone). This increases the actual inventory
	 * count.
	 */
	public void restoreStock(Quantity amount) {
		this.quantityOnHand = this.quantityOnHand.add(amount);
		this.lastUpdated = Instant.now();
		validateInvariants();
	}

	/**
	 * RECEIVE (New Shipment from Supplier) Moves stock from 'In Transit' to 'On
	 * Hand' when a truck arrives at the warehouse.
	 */
	public void receiveStock(Quantity amount) {
		// Handle over-delivery logic safely
        if (amount.isGreaterThan(quantityInTransit)) {
            this.quantityInTransit = Quantity.zero(unitOfMeasure); 
        } else {
            this.quantityInTransit = this.quantityInTransit.subtract(amount);
        }
        this.quantityOnHand = this.quantityOnHand.add(amount);
        this.lastUpdated = Instant.now();
        validateInvariants();
	}

	// --- GUARDS ---

	/**
	 * Ensures the internal state of the Stock aggregate is always logically valid.
	 */
	private void validateInvariants() {
		// Final check: You can't have more reserved than you have total items (Physical
		// + Incoming)
		Quantity totalPossible = quantityOnHand.add(quantityInTransit);
		if (quantityReserved.isGreaterThan(totalPossible)) {
			throw new IllegalStateException("System Integrity Error: Reserved stock exceeds total possible inventory.");
		}
	}
	
	// --- 1. GETTERS (Necessary for the Mapper to read state) ---
    // We keep setters private/absent to enforce immutability
    public StockId getId() { return id; }
    public ProductId getProductId() { return productId; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public Owner getOwnerId() { return ownerId; }
    public UnitOfMeasure getUnitOfMeasure() { return unitOfMeasure; }
    
    // We expose the raw value or the object? Object is safer.
    public Quantity getQuantityOnHand() { return quantityOnHand; }
    public Quantity getQuantityReserved() { return quantityReserved; }
    public Quantity getQuantityAllocated() { return quantityAllocated; }
    public Quantity getQuantityInTransit() { return quantityInTransit; }
    public Quantity getSafetyStock() { return safetyStock; }
    public Quantity getDiscrepancyGap() { return discrepancyGap; }
    
    public Quantity getLowStockThreshold() { return lowStockThreshold; }
    public Quantity getMaxStockThreshold() { return maxStockThreshold; }
    public VelocityCode getVelocityCode() { return velocityCode; }
    public RotationPolicy getRotationPolicy() { return rotationPolicy; }
    public LifeCycleStatus getLifecycleStatus() { return lifecycleStatus; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public Long getVersion() { return version; }
    
 // --- 2. THE RECONSTITUTION FACTORY (For the Mapper) ---
    /**
     * DANGEROUS: This method is ONLY for the Infrastructure Layer (Mapper).
     * It bypasses the 'New Stock' logic to restore an existing object from the DB.
     */
    public static Stock reconstitute(
            StockId id, ProductId productId, WarehouseId warehouseId, Owner ownerId, UnitOfMeasure unit,
            Quantity onHand, Quantity reserved, Quantity allocated, Quantity inTransit, Quantity safety, Quantity discrepancy,
            Quantity lowThresh, Quantity maxThresh, 
            VelocityCode velocity, RotationPolicy rotation, LifeCycleStatus status,
            LocalDate expiry, Instant lastUpdated, Long version) {
        
        // We use a private constructor or just create it here. 
        // For simplicity in this example, we use the main constructor but then OVERWRITE the internal state.
        // A cleaner way is a private "All Args" constructor.
        
        Stock stock = new Stock(id, productId, warehouseId, ownerId, rotation, lowThresh, maxThresh, unit);
        
        // Force the ID to match the Database ID (not a new random one)
        setPrivateField(stock, "id", id); 
        
        // Restore Buckets
        stock.quantityOnHand = onHand;
        stock.quantityReserved = reserved;
        stock.quantityAllocated = allocated;
        stock.quantityInTransit = inTransit;
        stock.safetyStock = safety;
        stock.discrepancyGap = discrepancy;
        
        // Restore Metadata
        stock.velocityCode = velocity;
        stock.lifecycleStatus = status;
        stock.expiryDate = expiry;
        stock.lastUpdated = lastUpdated;
        stock.version = version;
        
        return stock;
    }
    
 // Helper to set the final ID field (Java reflection or protected access is usually needed here)
    // In a real project, we often add a private constructor that takes ALL fields to avoid reflection.
    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reconstitute Stock", e);
        }
    }
	
}