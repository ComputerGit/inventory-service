package com.at.t.ecommerce.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.at.t.ecommerce.inventory.domain.stock.entities.Stock;
import com.at.t.ecommerce.inventory.domain.stock.exceptions.InsufficientStockException;
import com.at.t.ecommerce.inventory.domain.stock.factory.StockFactory;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;
import com.at.t.ecommerce.inventory.domain.stock.enums.UnitOfMeasure;

class StockTest {

    // Helper to generate IDs
    private final StockFactory factory = new StockFactory();
    private final ProductId productId = new ProductId("prod-123");
    private final WarehouseId warehouseId = new WarehouseId("wh-texas");
    private final Owner ownerId = new Owner("owner-1");
    private final UnitOfMeasure unit = UnitOfMeasure.EACH;

    @Test
    @DisplayName("Should successfully reserve stock when availability is sufficient")
    void testReserveSuccess() {
        // 1. GIVEN: A fresh stock item
        Stock stock = factory.createNewStock(productId, warehouseId, ownerId, unit);
        
        // We manually inject some inventory (simulating a "Receive Stock" event for the test)
        // Since we don't have receive logic yet, we can use reflection or a helper, 
        // BUT for now, let's assume 'createNewStock' initializes with 0. 
        // Wait, we need stock to reserve it!
        
        // TRICK: We can use the 'reconstitute' method to create a Stock with 100 items on hand
        // This simulates loading a "Full" stock from DB without needing complex setup.
        Stock loadedStock = Stock.reconstitute(
            StockId.newId(), productId, warehouseId, ownerId, unit,
            Quantity.of(100, unit), // On Hand
            Quantity.of(0, unit),   // Reserved
            Quantity.of(0, unit), Quantity.of(0, unit), Quantity.of(0, unit), Quantity.of(0, unit),
            Quantity.of(10, unit), Quantity.of(1000, unit), 
            null, null, null, null, null, 1L
        );

        // 2. WHEN: We reserve 50
        loadedStock.reserveStock(Quantity.of(50, unit));

        // 3. THEN: 
        // Reserved should be 50
        assertThat(stockMatches(loadedStock.getQuantityReserved(), 50)).isTrue();
        // Available to Promise should be 50 (100 - 50)
        assertThat(stockMatches(loadedStock.getAvailableToPromise(), 50)).isTrue();
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when requesting too much")
    void testReserveFail() {
        // 1. GIVEN: Stock with 10 items
        Stock stock = Stock.reconstitute(
            StockId.newId(), productId, warehouseId, ownerId, unit,
            Quantity.of(10, unit), // On Hand
            Quantity.of(0, unit),  
            Quantity.of(0, unit), Quantity.of(0, unit), Quantity.of(0, unit), Quantity.of(0, unit),
            Quantity.of(5, unit), Quantity.of(100, unit), 
            null, null, null, null, null, 1L
        );

        // 2. WHEN & THEN: We try to reserve 15
        assertThatThrownBy(() -> stock.reserveStock(Quantity.of(15, unit)))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining("Insufficient Stock");
    }
    
    // Helper to compare Quantity with integer
    private boolean stockMatches(Quantity q, long val) {
        return q.value() == val;
    }
}