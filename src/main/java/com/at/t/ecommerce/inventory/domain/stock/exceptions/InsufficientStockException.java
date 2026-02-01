package com.at.t.ecommerce.inventory.domain.stock.exceptions;

import com.at.t.ecommerce.inventory.domain.stock.vo.ProductId;
import com.at.t.ecommerce.inventory.domain.stock.vo.Quantity;

public final class InsufficientStockException extends StockException{
	
	public InsufficientStockException(ProductId productId , Quantity requested , Quantity available) {
		super("Insufficient stock for product " + productId +
	              ", requested=" + requested +
	              ", available=" + available);
	}

}
