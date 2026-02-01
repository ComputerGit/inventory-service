package com.at.t.ecommerce.inventory.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stock_inventory") // Custom DB table name
@Getter
@Setter // Lombok is fine here in Infrastructure (Data structure only)
public class StockJpaEntity {

    // --- KEYS ---
    @Id
    @Column(name = "stock_id")
    private String id; // We store StockId as String (UUID/ULID)

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "warehouse_id", nullable = false)
    private String warehouseId;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;
    
    @Column(name = "unit_of_measure", nullable = false)
    private String unitOfMeasure; // Stores "EACH", "METER"

    // --- QUANTITIES (Flattened) ---
    // We unwrap the 'Quantity' object into simple 'Long' values for SQL
    @Column(name = "qty_on_hand")
    private long quantityOnHand;

    @Column(name = "qty_reserved")
    private long quantityReserved;

    @Column(name = "qty_allocated")
    private long quantityAllocated;

    @Column(name = "qty_in_transit")
    private long quantityInTransit;

    @Column(name = "qty_safety_stock")
    private long safetyStock;
    
    @Column(name = "qty_discrepancy")
    private long discrepancyGap;

    // --- POLICIES ---
    @Column(name = "threshold_low")
    private long lowStockThreshold;

    @Column(name = "threshold_max")
    private long maxStockThreshold;

    @Column(name = "velocity_code")
    private String velocityCode; // "A", "B", "C"

    @Column(name = "rotation_policy")
    private String rotationPolicy; // "FIFO", "LIFO"

    // --- AUDIT & CONCURRENCY ---
    @Column(name = "status")
    private String lifecycleStatus; // "ACTIVE", "DISCONTINUED"
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @Column(name = "last_audit_date")
    private LocalDate lastAuditDate;

    // OPTIMISTIC LOCKING: Crucial for high-concurrency inventory
    @Version
    private Long version; 
}