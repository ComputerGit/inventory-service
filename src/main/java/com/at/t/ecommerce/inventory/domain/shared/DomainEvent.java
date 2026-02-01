package com.at.t.ecommerce.inventory.domain.shared;

import java.time.Instant;

// This is the generic parent for ALL events in your microservice (Stock, Order, Payment)
public interface DomainEvent {
    Instant occurredOn();
}