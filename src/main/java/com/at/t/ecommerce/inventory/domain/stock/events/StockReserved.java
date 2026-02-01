package com.at.t.ecommerce.inventory.domain.stock.events;

import java.time.Instant;
import java.util.Objects;

import com.at.t.ecommerce.inventory.domain.stock.vo.*;

public record StockReserved(
    StockId stockId,
    ProductId productId,
    Quantity amount,
    Instant occurredOn
) implements StockEvent {
    
    // Compact Constructor for Validation
    public StockReserved {
        Objects.requireNonNull(stockId);
        Objects.requireNonNull(productId);
    }

    public static StockReserved now(StockId id, ProductId pId, Quantity amount) {
        return new StockReserved(id, pId, amount, Instant.now());
    }
}