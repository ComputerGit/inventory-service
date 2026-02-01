package com.at.t.ecommerce.inventory.domain.stock.events;

import java.time.Instant;

import com.at.t.ecommerce.inventory.domain.shared.DomainEvent;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;

public record StockReservedEvent(
    StockId stockId,
    ProductId productId,
    Quantity amountReserved,
    Instant occurredOn
) implements DomainEvent {

    public static StockReservedEvent from(StockId stockId, ProductId productId, Quantity amount) {
        return new StockReservedEvent(stockId, productId, amount, Instant.now());
    }
}