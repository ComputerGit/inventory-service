package com.at.t.ecommerce.inventory.domain.stock.vo;

import java.util.Objects;

public record Owner(String value) {
    public Owner {
        Objects.requireNonNull(value, "Owner ID value cannot be null");
        if (value.isBlank()) {
             throw new IllegalArgumentException("Owner ID cannot be empty");
        }
    }

    public static Owner of(String value) {
        return new Owner(value);
    }
}