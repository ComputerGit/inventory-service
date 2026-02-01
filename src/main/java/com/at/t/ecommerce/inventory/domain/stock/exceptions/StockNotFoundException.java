package com.at.t.ecommerce.inventory.domain.stock.exceptions;

import com.at.t.ecommerce.inventory.domain.stock.vo.ProductId;
import com.at.t.ecommerce.inventory.domain.stock.vo.WarehouseId;

public class StockNotFoundException extends RuntimeException {
	public StockNotFoundException(ProductId pid, WarehouseId wid) {
		super(String.format("Stock not found for Product %s in Warehouse %s", pid.value(), wid.value()));
	}
}