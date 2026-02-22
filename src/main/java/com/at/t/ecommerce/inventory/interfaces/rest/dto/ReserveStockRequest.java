package com.at.t.ecommerce.inventory.interfaces.rest.dto;

public record ReserveStockRequest(
        String productId,
        String warehouseId,
        long quantity,
        String unitOfMeasure
) {}