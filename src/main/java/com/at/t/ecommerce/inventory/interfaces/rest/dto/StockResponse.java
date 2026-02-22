package com.at.t.ecommerce.inventory.interfaces.rest.dto;

/**
 * A simple DTO to safely send data out as JSON without exposing our Domain logic.
 */
public record StockResponse(
        String stockId,
        String productId,
        String warehouseId,
        long quantityOnHand,
        long quantityReserved,
        long availableToPromise
) {}