package com.at.t.ecommerce.inventory.domain.stock.events;

import java.time.Instant;

import com.at.t.ecommerce.inventory.domain.stock.events.StockEvent;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;

public record StockReceived(
    StockId stockId,
    ProductId productId,
    Quantity amount,
    Instant occurredOn
) implements StockEvent {
    
    public static StockReceived now(StockId id, ProductId pId, Quantity amount) {
        return new StockReceived(id, pId, amount, Instant.now());
    }
}