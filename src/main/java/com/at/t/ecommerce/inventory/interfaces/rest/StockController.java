package com.at.t.ecommerce.inventory.interfaces.rest;

import com.at.t.ecommerce.inventory.application.stock.StockApplicationService;
import com.at.t.ecommerce.inventory.domain.stock.entities.Stock;
import com.at.t.ecommerce.inventory.domain.stock.repositories.StockRepository;
import com.at.t.ecommerce.inventory.domain.stock.vo.ProductId;
import com.at.t.ecommerce.inventory.domain.stock.vo.Quantity;
import com.at.t.ecommerce.inventory.domain.stock.vo.StockId;
import com.at.t.ecommerce.inventory.domain.stock.vo.WarehouseId;
import com.at.t.ecommerce.inventory.domain.stock.enums.UnitOfMeasure;
import com.at.t.ecommerce.inventory.interfaces.rest.dto.ReserveStockRequest;
import com.at.t.ecommerce.inventory.interfaces.rest.dto.StockResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventory")
public class StockController {

    private final StockRepository stockRepository;
    private final StockApplicationService stockApplicationService; // <-- Added this!

    public StockController(StockRepository stockRepository, StockApplicationService stockApplicationService) {
        this.stockRepository = stockRepository;
        this.stockApplicationService = stockApplicationService;
    }

    // --- 1. THE READ PATH (Your excellent GET method) ---
    @GetMapping("/{stockId}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable String stockId) {
        Optional<Stock> stockOpt = stockRepository.findById(new StockId(stockId));

        if (stockOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Stock stock = stockOpt.get();
        StockResponse response = new StockResponse(
                stock.getId().value(),
                stock.getProductId().value(),
                stock.getWarehouseId().value(),
                stock.getQuantityOnHand().value(),
                stock.getQuantityReserved().value(),
                stock.getAvailableToPromise().value() 
        );

        return ResponseEntity.ok(response);
    }

    // --- 2. THE WRITE PATH (The new POST method to reserve stock!) ---
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveStock(@RequestBody ReserveStockRequest request) {
        try {
            // Convert Strings from the JSON request into pure Domain Value Objects
            ProductId productId = new ProductId(request.productId());
            WarehouseId warehouseId = new WarehouseId(request.warehouseId());
            Quantity amount = Quantity.of(request.quantity(), UnitOfMeasure.valueOf(request.unitOfMeasure()));

            // Call your Orchestrator!
            stockApplicationService.reserveStock(productId, warehouseId, amount);
            
            return ResponseEntity.ok("Successfully reserved " + request.quantity() + " telecom assets.");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}