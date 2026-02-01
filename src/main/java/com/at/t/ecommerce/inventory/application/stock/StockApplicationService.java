package com.at.t.ecommerce.inventory.application.stock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.at.t.ecommerce.inventory.domain.stock.entities.Stock;
import com.at.t.ecommerce.inventory.domain.stock.exceptions.StockNotFoundException;
import com.at.t.ecommerce.inventory.domain.stock.repositories.StockRepository;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j // Logs are crucial for debugging production issues
public class StockApplicationService {

    private final StockRepository repository;

    /**
     * The Main Orchestrator for Reservations.
     * 1. Loads the Aggregate (Stock)
     * 2. Executes Business Logic (Reserve)
     * 3. Persists State
     */
    @Transactional
    public void reserveStock(ProductId productId, WarehouseId warehouseId, Quantity amount) {
        log.info("Attempting to reserve {} items for Product: {}", amount.value(), productId.value());

        // 1. Fetch the Aggregate (Use the Lock if high concurrency is expected)
        Stock stock = repository.findByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> new StockNotFoundException(productId, warehouseId));

        // 2. Execute Domain Logic (The Domain guarantees the rules)
        stock.reserveStock(amount);

        // 3. Save (This flushes the changes to DB and dispatches Events)
        repository.save(stock);
        
        log.info("Reservation successful. New Available Qty: {}", stock.getAvailableToPromise().value());
    }
}