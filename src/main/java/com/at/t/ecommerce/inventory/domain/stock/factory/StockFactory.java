package com.at.t.ecommerce.inventory.domain.stock.factory;

import org.springframework.stereotype.Component;

import com.at.t.ecommerce.inventory.domain.stock.entities.Stock;
import com.at.t.ecommerce.inventory.domain.stock.enums.RotationPolicy;
import com.at.t.ecommerce.inventory.domain.stock.enums.UnitOfMeasure;
import com.at.t.ecommerce.inventory.domain.stock.vo.Owner;
import com.at.t.ecommerce.inventory.domain.stock.vo.ProductId;
import com.at.t.ecommerce.inventory.domain.stock.vo.Quantity;
import com.at.t.ecommerce.inventory.domain.stock.vo.StockId; // ✅ Make sure this is imported
import com.at.t.ecommerce.inventory.domain.stock.vo.WarehouseId;

@Component
public class StockFactory {

    public Stock createNewStock(ProductId productId, WarehouseId warehouseId, Owner ownerId, UnitOfMeasure unit) {
        
        // Defaults for new items
        RotationPolicy defaultPolicy = RotationPolicy.NEAREST;
        Quantity defaultLowThreshold = Quantity.of(10, unit);
        Quantity defaultMaxThreshold = Quantity.of(1000, unit);

        // ✅ FIX: Added 'StockId.newId()' as the first argument
        return new Stock(
            StockId.newId(),    // <--- THIS WAS LIKELY MISSING
            productId, 
            warehouseId, 
            ownerId, 
            defaultPolicy, 
            defaultLowThreshold, 
            defaultMaxThreshold,
            unit
        );
    }
}