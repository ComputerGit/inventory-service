package com.at.t.ecommerce.inventory.infrastructure.mappers;

import org.springframework.stereotype.Component;

import com.at.t.ecommerce.inventory.domain.stock.entities.Stock;
import com.at.t.ecommerce.inventory.domain.stock.enums.*;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;
import com.at.t.ecommerce.inventory.infrastructure.persistence.jpa.entities.StockJpaEntity;

@Component
public class StockMapper {

    /**
     * DB -> Domain
     * Taking the flat rows and building the Rich Object.
     */
    public Stock toDomain(StockJpaEntity entity) {
        if (entity == null) return null;

        UnitOfMeasure unit = UnitOfMeasure.valueOf(entity.getUnitOfMeasure());

        return Stock.reconstitute(
            new StockId(entity.getId()),
            new ProductId(entity.getProductId()),
            new WarehouseId(entity.getWarehouseId()),
            new Owner(entity.getOwnerId()),
            unit,
            
            // Quantities (Wrap primitives back into Value Objects)
            Quantity.of(entity.getQuantityOnHand(), unit),
            Quantity.of(entity.getQuantityReserved(), unit),
            Quantity.of(entity.getQuantityAllocated(), unit),
            Quantity.of(entity.getQuantityInTransit(), unit),
            Quantity.of(entity.getSafetyStock(), unit),
            Quantity.of(entity.getDiscrepancyGap(), unit),

            // Policies
            Quantity.of(entity.getLowStockThreshold(), unit),
            Quantity.of(entity.getMaxStockThreshold(), unit),
            
            VelocityCode.valueOf(entity.getVelocityCode()),
            RotationPolicy.valueOf(entity.getRotationPolicy()),
            LifeCycleStatus.valueOf(entity.getLifecycleStatus()),
            
            entity.getExpiryDate(),
            entity.getLastUpdated(),
            entity.getVersion()
        );
    }

    /**
     * Domain -> DB
     * Flattening the Rich Object into simple rows.
     */
    public StockJpaEntity toEntity(Stock domain) {
        if (domain == null) return null;

        StockJpaEntity entity = new StockJpaEntity();

        // IDs
        entity.setId(domain.getId().value());
        entity.setProductId(domain.getProductId().value());
        entity.setWarehouseId(domain.getWarehouseId().value());
        entity.setOwnerId(domain.getOwnerId().value());
        entity.setUnitOfMeasure(domain.getUnitOfMeasure().name());

        // Quantities (Extract raw long values)
        entity.setQuantityOnHand(domain.getQuantityOnHand().value());
        entity.setQuantityReserved(domain.getQuantityReserved().value());
        entity.setQuantityAllocated(domain.getQuantityAllocated().value());
        entity.setQuantityInTransit(domain.getQuantityInTransit().value());
        entity.setSafetyStock(domain.getSafetyStock().value());
        entity.setDiscrepancyGap(domain.getDiscrepancyGap().value());

        // Policies
        entity.setLowStockThreshold(domain.getLowStockThreshold().value());
        entity.setMaxStockThreshold(domain.getMaxStockThreshold().value());
        entity.setVelocityCode(domain.getVelocityCode().name());
        entity.setRotationPolicy(domain.getRotationPolicy().name());
        entity.setLifecycleStatus(domain.getLifecycleStatus().name());
        
        entity.setExpiryDate(domain.getExpiryDate());
        entity.setLastUpdated(java.time.Instant.now()); // Always refresh timestamp on save
        entity.setVersion(domain.getVersion()); // Hibernate handles incrementing this

        return entity;
    }
}