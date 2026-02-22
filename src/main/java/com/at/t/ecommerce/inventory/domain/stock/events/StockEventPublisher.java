package com.at.t.ecommerce.inventory.domain.stock.events;

public interface StockEventPublisher {
    void publishStockReservedEvent(StockReservedEvent event);
}