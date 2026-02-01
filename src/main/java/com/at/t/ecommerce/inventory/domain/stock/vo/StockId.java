package com.at.t.ecommerce.inventory.domain.stock.vo;

import java.util.Objects;
import java.util.UUID;

public record StockId(String value) {
    
    public StockId {
        Objects.requireNonNull(value, "StockId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("StockId cannot be empty");
        }
    }

    // Factory method to generate a new random ID
    public static StockId newId() {
        return new StockId(UUID.randomUUID().toString());
    }
    
    // Factory method to create from existing String (for Mapper)
    public static StockId of(String value) {
        return new StockId(value);
    }
}