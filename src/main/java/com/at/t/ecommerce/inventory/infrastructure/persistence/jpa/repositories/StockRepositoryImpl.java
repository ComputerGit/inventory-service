package com.at.t.ecommerce.inventory.infrastructure.persistence.jpa.repositories;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.at.t.ecommerce.inventory.domain.stock.entities.Stock;
import com.at.t.ecommerce.inventory.domain.stock.repositories.StockRepository;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;
import com.at.t.ecommerce.inventory.infrastructure.mappers.StockMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final SpringDataStockRepository jpaRepository; // The Spring Magic
    private final StockMapper mapper;                      // The Translator

    @Override
    @Transactional
    public Stock save(Stock stock) {
        // 1. Convert Domain -> DB Entity
        var entity = mapper.toEntity(stock);
        
        // 2. Save using Spring Data
        var savedEntity = jpaRepository.save(entity);
        
        // 3. Convert back DB -> Domain (to return fresh state/IDs)
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Stock> findById(StockId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Stock> findByProductAndWarehouse(ProductId productId, WarehouseId warehouseId) {
        return jpaRepository.findByProductIdAndWarehouseId(productId.value(), warehouseId.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Optional<Stock> findByIdForUpdate(StockId id) {
        // Calls the pessimistic lock query we defined above
        return jpaRepository.findByIdLocked(id.value())
                .map(mapper::toDomain);
    }
}