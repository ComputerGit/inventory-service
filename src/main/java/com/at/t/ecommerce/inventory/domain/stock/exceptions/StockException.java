package com.at.t.ecommerce.inventory.domain.stock.exceptions;

public sealed class StockException extends RuntimeException permits InsufficientStockException , InvalidStockStateException{
	
	public StockException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
