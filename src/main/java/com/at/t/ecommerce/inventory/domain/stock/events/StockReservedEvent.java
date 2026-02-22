package com.at.t.ecommerce.inventory.domain.stock.events;

import java.time.Instant;
import java.util.UUID;

public record StockReservedEvent(
        String eventId,
        String productId,
        String warehouseId,
        long quantityReserved,
        Instant timestamp
) {
    public static StockReservedEvent create(String productId, String warehouseId, long quantity) {
        return new StockReservedEvent(
                UUID.randomUUID().toString(),
                productId,
                warehouseId,
                quantity,
                Instant.now()
        );
    }
}