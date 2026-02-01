package com.at.t.ecommerce.inventory.domain.stock.events;

import com.at.t.ecommerce.inventory.domain.shared.DomainEvent;

/**
 * Java 17 Sealed Interface.
 * We explicitly 'permit' only the specific event records to implement this.
 * This guarantees type safety at compile time.
 */

public sealed interface StockEvent extends DomainEvent 
    permits StockReserved, StockReleased, StockShipped, StockReceived, StockLevelLow {
}