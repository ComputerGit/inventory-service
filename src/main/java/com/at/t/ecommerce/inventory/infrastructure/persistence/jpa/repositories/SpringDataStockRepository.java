package com.at.t.ecommerce.inventory.infrastructure.persistence.jpa.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;

import com.at.t.ecommerce.inventory.infrastructure.persistence.jpa.entities.StockJpaEntity;

// NOTE: This is internal to Infrastructure. The Domain DOES NOT see this.
public interface SpringDataStockRepository extends JpaRepository<StockJpaEntity, String> {

    // Supports findByProductAndWarehouse
    Optional<StockJpaEntity> findByProductIdAndWarehouseId(String productId, String warehouseId);

    // The "Pro" Feature: Pessimistic Locking
    // This generates: SELECT * FROM stock WHERE id = ? FOR UPDATE
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockJpaEntity s WHERE s.id = :id")
    Optional<StockJpaEntity> findByIdLocked(String id);
}