package com.at.t.ecommerce.inventory.infrastructure.persistence.jpa.adaptors;

import com.at.t.ecommerce.inventory.domain.stock.entities.Stock;
import com.at.t.ecommerce.inventory.domain.stock.enums.LifeCycleStatus;
import com.at.t.ecommerce.inventory.domain.stock.enums.RotationPolicy;
import com.at.t.ecommerce.inventory.domain.stock.enums.UnitOfMeasure;
import com.at.t.ecommerce.inventory.domain.stock.enums.VelocityCode;
import com.at.t.ecommerce.inventory.domain.stock.repositories.StockRepository;
import com.at.t.ecommerce.inventory.domain.stock.vo.Owner;
import com.at.t.ecommerce.inventory.domain.stock.vo.ProductId;
import com.at.t.ecommerce.inventory.domain.stock.vo.Quantity;
import com.at.t.ecommerce.inventory.domain.stock.vo.StockId;
import com.at.t.ecommerce.inventory.domain.stock.vo.WarehouseId;
import com.at.t.ecommerce.inventory.infrastructure.persistence.jpa.entities.StockJpaEntity;
import com.at.t.ecommerce.inventory.infrastructure.persistence.jpa.repositories.SpringDataStockRepository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Primary
public class StockRepositoryAdapter implements StockRepository {

    private final SpringDataStockRepository jpaRepository;

    public StockRepositoryAdapter(SpringDataStockRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Stock save(Stock stock) {
        StockJpaEntity entity = new StockJpaEntity();
        
        entity.setId(stock.getId().value()); 
        entity.setProductId(stock.getProductId().value());
        entity.setWarehouseId(stock.getWarehouseId().value());
        entity.setOwnerId(stock.getOwnerId().value());
        entity.setUnitOfMeasure(stock.getUnitOfMeasure().name());

        entity.setQuantityOnHand(stock.getQuantityOnHand().value());
        entity.setQuantityReserved(stock.getQuantityReserved().value());
        entity.setQuantityAllocated(stock.getQuantityAllocated().value());
        entity.setQuantityInTransit(stock.getQuantityInTransit().value());
        entity.setSafetyStock(stock.getSafetyStock().value());
        entity.setDiscrepancyGap(stock.getDiscrepancyGap().value());

        entity.setLowStockThreshold(stock.getLowStockThreshold().value());
        entity.setMaxStockThreshold(stock.getMaxStockThreshold().value());
        entity.setVelocityCode(stock.getVelocityCode().name());
        entity.setRotationPolicy(stock.getRotationPolicy().name());

        entity.setLifecycleStatus(stock.getLifecycleStatus().name());
        entity.setExpiryDate(stock.getExpiryDate());
        entity.setVersion(stock.getVersion());

        jpaRepository.save(entity);
        return stock; 
    }

    @Override
    public Optional<Stock> findById(StockId id) {
        Optional<StockJpaEntity> entityOptional = jpaRepository.findById(id.value());
        // Use the helper method!
        return entityOptional.map(this::mapToDomain); 
    }

    @Override
    public Optional<Stock> findByProductAndWarehouse(ProductId productId, WarehouseId warehouseId) {
        Optional<StockJpaEntity> entityOpt = jpaRepository.findByProductIdAndWarehouseId(
                productId.value(), 
                warehouseId.value()
        );
        // Use the helper method!
        return entityOpt.map(this::mapToDomain);
    }

    @Override
    public Optional<Stock> findByIdForUpdate(StockId id) {
        // We will build this out later when we tackle pessimistic locking!
        return Optional.empty();
    }

    // --- THE MAGIC HELPER METHOD ---
    private Stock mapToDomain(StockJpaEntity entity) {
        return Stock.reconstitute(
                new StockId(entity.getId()),
                new ProductId(entity.getProductId()),
                new WarehouseId(entity.getWarehouseId()),
                new Owner(entity.getOwnerId()),
                UnitOfMeasure.valueOf(entity.getUnitOfMeasure()),
                
                Quantity.of(entity.getQuantityOnHand(), UnitOfMeasure.valueOf(entity.getUnitOfMeasure())),
                Quantity.of(entity.getQuantityReserved(), UnitOfMeasure.valueOf(entity.getUnitOfMeasure())),
                Quantity.of(entity.getQuantityAllocated(), UnitOfMeasure.valueOf(entity.getUnitOfMeasure())),
                Quantity.of(entity.getQuantityInTransit(), UnitOfMeasure.valueOf(entity.getUnitOfMeasure())),
                Quantity.of(entity.getSafetyStock(), UnitOfMeasure.valueOf(entity.getUnitOfMeasure())),
                Quantity.of(entity.getDiscrepancyGap(), UnitOfMeasure.valueOf(entity.getUnitOfMeasure())),
                Quantity.of(entity.getLowStockThreshold(), UnitOfMeasure.valueOf(entity.getUnitOfMeasure())),
                Quantity.of(entity.getMaxStockThreshold(), UnitOfMeasure.valueOf(entity.getUnitOfMeasure())),
                
                VelocityCode.valueOf(entity.getVelocityCode()),
                RotationPolicy.valueOf(entity.getRotationPolicy()),
                LifeCycleStatus.valueOf(entity.getLifecycleStatus()),
                
                entity.getExpiryDate(),
                entity.getLastUpdated(),
                entity.getVersion()
        );
    }
}