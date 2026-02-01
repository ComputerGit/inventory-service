package com.at.t.ecommerce.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.at.t.ecommerce.inventory.domain.stock.entities.Stock;
import com.at.t.ecommerce.inventory.domain.stock.enums.LifeCycleStatus;
import com.at.t.ecommerce.inventory.domain.stock.enums.RotationPolicy;
import com.at.t.ecommerce.inventory.domain.stock.enums.UnitOfMeasure;
import com.at.t.ecommerce.inventory.domain.stock.enums.VelocityCode;
import com.at.t.ecommerce.inventory.domain.stock.exceptions.InsufficientStockException;
import com.at.t.ecommerce.inventory.domain.stock.factory.StockFactory;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;

class StockTest {

    private final ProductId productId = new ProductId("prod-123");
    private final WarehouseId warehouseId = new WarehouseId("wh-texas");
    private final Owner ownerId = new Owner("owner-1");
    private final UnitOfMeasure unit = UnitOfMeasure.EACH;

    // We keep the factory initialized to avoid NullPointerExceptions
    private final StockFactory factory = new StockFactory();

    @Test
    @DisplayName("Should successfully reserve stock when availability is sufficient")
    void testReserveSuccess() {
        // 1. GIVEN: Stock with 100 items On Hand
        Stock stock = Stock.reconstitute(
            StockId.newId(), 
            productId, 
            warehouseId, 
            ownerId, 
            unit,
            Quantity.of(100, unit), // On Hand
            Quantity.of(0, unit),   // Reserved
            Quantity.of(0, unit), Quantity.of(0, unit), Quantity.of(0, unit), Quantity.of(0, unit),
            Quantity.of(10, unit), Quantity.of(1000, unit), 
            
            // ✅ FIX: Order matches Error Message exactly:
            // VelocityCode -> RotationPolicy -> LifeCycleStatus
            VelocityCode.A_FAST_MOVER,              
            RotationPolicy.FIFO,         
            LifeCycleStatus.ACTIVE,      
            
            null, // Expected: LocalDate (Expiry)
            null, // Expected: Instant (Last Updated)
            1L    // Expected: Long (Version)
        );

        // 2. WHEN: We reserve 50 items
        stock.reserveStock(Quantity.of(50, unit));

        // 3. THEN: 
        assertThat(stock.getQuantityReserved().value()).isEqualTo(50);
        assertThat(stock.getAvailableToPromise().value()).isEqualTo(50);
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when requesting too much")
    void testReserveFail() {
        // 1. GIVEN: Stock with only 10 items On Hand
        Stock stock = Stock.reconstitute(
            StockId.newId(), 
            productId, 
            warehouseId, 
            ownerId, 
            unit,
            Quantity.of(10, unit), // On Hand
            Quantity.of(0, unit),  
            Quantity.of(0, unit), Quantity.of(0, unit), Quantity.of(0, unit), Quantity.of(0, unit),
            Quantity.of(5, unit), Quantity.of(100, unit), 
            
            // ✅ FIX: Correct Order
            VelocityCode.A_FAST_MOVER,              
            RotationPolicy.FIFO,         
            LifeCycleStatus.ACTIVE,      
            
            null, // Expiry
            null, // Last Updated
            1L
        );

        // 2. WHEN & THEN: We try to reserve 15
        assertThatThrownBy(() -> stock.reserveStock(Quantity.of(15, unit)))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining("Insufficient stock");
    }
}