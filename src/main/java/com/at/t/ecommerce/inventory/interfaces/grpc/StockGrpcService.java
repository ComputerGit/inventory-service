package com.at.t.ecommerce.inventory.interfaces.grpc; 

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import lombok.RequiredArgsConstructor;

import com.at.t.ecommerce.inventory.grpc.generated.StockServiceGrpc;
import com.at.t.ecommerce.inventory.grpc.generated.ReserveStockRequest;
import com.at.t.ecommerce.inventory.grpc.generated.StockResponse;

import com.at.t.ecommerce.inventory.application.stock.StockApplicationService;
import com.at.t.ecommerce.inventory.domain.stock.enums.UnitOfMeasure;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;

@GrpcService
@RequiredArgsConstructor
public class StockGrpcService extends StockServiceGrpc.StockServiceImplBase {

    private final StockApplicationService applicationService;

    @Override
    public void reserveStock(ReserveStockRequest request, StreamObserver<StockResponse> responseObserver) {
        
        // 1. Convert DTO -> Domain
        ProductId productId = new ProductId(request.getProductId());
        WarehouseId warehouseId = new WarehouseId(request.getWarehouseId());
        UnitOfMeasure unit = UnitOfMeasure.valueOf(request.getUnitOfMeasure());
        Quantity amount = Quantity.of(request.getQuantity(), unit);

        // 2. Call Service (Exceptions handled by @GrpcAdvice)
        applicationService.reserveStock(productId, warehouseId, amount);

        // 3. Success Response
        StockResponse response = StockResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Reservation Successful")
                .setStockId("HIDDEN")
                .setTimestamp(java.time.Instant.now().toString())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}