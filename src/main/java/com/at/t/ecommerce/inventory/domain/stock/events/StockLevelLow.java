package com.at.t.ecommerce.inventory.domain.stock.events;

import java.time.Instant;

import com.at.t.ecommerce.inventory.domain.stock.events.StockEvent;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;

public record StockLevelLow(
    StockId stockId,
    ProductId productId,
    Quantity currentLevel,
    Instant occurredOn
) implements StockEvent {
    
    public static StockLevelLow now(StockId id, ProductId pId, Quantity level) {
        return new StockLevelLow(id, pId, level, Instant.now());
    }
}