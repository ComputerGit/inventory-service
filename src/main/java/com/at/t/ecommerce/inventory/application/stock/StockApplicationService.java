package com.at.t.ecommerce.inventory.application.stock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at.t.ecommerce.inventory.domain.stock.entities.Stock;
import com.at.t.ecommerce.inventory.domain.stock.exceptions.StockNotFoundException;
import com.at.t.ecommerce.inventory.domain.stock.repositories.StockRepository;
import com.at.t.ecommerce.inventory.domain.stock.events.StockEventPublisher; // <-- Add this import
import com.at.t.ecommerce.inventory.domain.stock.events.StockReservedEvent;  // <-- Add this import
import com.at.t.ecommerce.inventory.domain.stock.vo.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j 
public class StockApplicationService {

    private final StockRepository repository;
    private final StockEventPublisher eventPublisher; // <-- 1. Inject the Publisher here!

    @Transactional
    public void reserveStock(ProductId productId, WarehouseId warehouseId, Quantity amount) {
        log.info("Attempting to reserve {} items for Product: {}", amount.value(), productId.value());

        Stock stock = repository.findByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> new StockNotFoundException(productId, warehouseId));

        stock.reserveStock(amount);
        repository.save(stock);
        
        // --- 2. THE MISSING KAFKA TRIGGER ---
        StockReservedEvent event = StockReservedEvent.create(
                productId.value(),
                warehouseId.value(),
                amount.value()
        );
        eventPublisher.publishStockReservedEvent(event);
        // ------------------------------------
        
        log.info("Reservation successful. New Available Qty: {}", stock.getAvailableToPromise().value());
    }
}