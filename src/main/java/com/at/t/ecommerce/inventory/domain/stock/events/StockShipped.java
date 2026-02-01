package com.at.t.ecommerce.inventory.domain.stock.events;

import java.time.Instant;

import com.at.t.ecommerce.inventory.domain.stock.events.StockEvent;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;

public record StockShipped(
    StockId stockId,
    ProductId productId,
    Quantity amount,
    Instant occurredOn
) implements StockEvent {
    
    public static StockShipped now(StockId id, ProductId pId, Quantity amount) {
        return new StockShipped(id, pId, amount, Instant.now());
    }
}