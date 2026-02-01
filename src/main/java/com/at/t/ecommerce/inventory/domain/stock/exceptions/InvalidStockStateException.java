package com.at.t.ecommerce.inventory.domain.stock.exceptions;

public final class InvalidStockStateException extends StockException{

	public InvalidStockStateException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
