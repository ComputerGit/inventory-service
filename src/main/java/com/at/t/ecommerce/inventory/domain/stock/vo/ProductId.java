package com.at.t.ecommerce.inventory.domain.stock.vo;

import java.util.Objects;

public record ProductId(String value) {
    public ProductId {
        Objects.requireNonNull(value, "ProductId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ProductId cannot be empty");
        }
    }
    
    public static ProductId of(String value) {
        return new ProductId(value);
    }
}