package com.at.t.ecommerce.inventory.domain.stock.repositories;

import java.util.Optional;

import com.at.t.ecommerce.inventory.domain.stock.entities.Stock;
import com.at.t.ecommerce.inventory.domain.stock.vo.ProductId;
import com.at.t.ecommerce.inventory.domain.stock.vo.StockId;
import com.at.t.ecommerce.inventory.domain.stock.vo.WarehouseId;

public interface StockRepository {

    /**
     * Saves the Stock state AND persists the pending Domain Events.
     * Returns the saved instance (standard practice for eventual consistency/IDs).
     */
    Stock save(Stock stock);

    /**
     * Finds stock by its technical ID.
     */
    Optional<Stock> findById(StockId id);

    /**
     * Finds stock by its Business Keys. 
     * The Service layer needs this to find "The iPhone 15 at Texas Warehouse".
     */
    Optional<Stock> findByProductAndWarehouse(ProductId productId, WarehouseId warehouseId);

    /**
     * PRO FEATURE: Pessimistic Lock Fetch.
     * Use this when you are about to reserve stock. It tells the DB to "Lock" the row
     * so no other transaction can read/write it until we are done.
     * Prevents the "Double Booking" race condition.
     */
    Optional<Stock> findByIdForUpdate(StockId id);
}