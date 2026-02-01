package com.at.t.ecommerce.inventory.interfaces.grpc.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

import com.at.t.ecommerce.inventory.domain.stock.exceptions.InsufficientStockException;
import com.at.t.ecommerce.inventory.domain.stock.exceptions.StockNotFoundException;

@GrpcAdvice // The gRPC equivalent of @ControllerAdvice
public class GlobalGrpcExceptionHandler {

    @GrpcExceptionHandler(StockNotFoundException.class)
    public StatusRuntimeException handleNotFound(StockNotFoundException e) {
        // Maps Java Exception -> gRPC Status.NOT_FOUND (Code 5)
        return Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    @GrpcExceptionHandler(InsufficientStockException.class)
    public StatusRuntimeException handleInsufficientStock(InsufficientStockException e) {
        // Maps to FAILED_PRECONDITION (Code 9) - Standard for business rule violations
        return Status.FAILED_PRECONDITION
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException handleGeneric(Exception e) {
        // Maps to INTERNAL (Code 13) - The "500 Server Error" of gRPC
        return Status.INTERNAL
                .withDescription("An unexpected error occurred: " + e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }
}