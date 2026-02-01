package com.at.t.ecommerce.inventory.domain.stock.vo;

import java.util.Objects;

public record WarehouseId(String value) {
    public WarehouseId {
        Objects.requireNonNull(value, "WarehouseId value cannot be null");
        if (value.isBlank()) {
             throw new IllegalArgumentException("WarehouseId cannot be empty");
        }
    }

    public static WarehouseId of(String value) {
        return new WarehouseId(value);
    }
}