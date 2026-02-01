package com.at.t.ecommerce.inventory.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

// Docker imports are GONE. We just use standard JPA classes.
import com.at.t.ecommerce.inventory.domain.stock.entities.Stock;
import com.at.t.ecommerce.inventory.domain.stock.enums.UnitOfMeasure;
import com.at.t.ecommerce.inventory.domain.stock.factory.StockFactory;
import com.at.t.ecommerce.inventory.domain.stock.repositories.StockRepository;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;

@DataJpaTest // This automatically configures H2 for us!
@Import({
    com.at.t.ecommerce.inventory.infrastructure.mappers.StockMapper.class, 
    com.at.t.ecommerce.inventory.infrastructure.persistence.jpa.repositories.StockRepositoryImpl.class
})
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    // We can use the factory safely
    private final StockFactory factory = new StockFactory();

    @Test
    @DisplayName("Should Save and Retrieve Stock using H2 In-Memory DB")
    void testSaveAndFind() {
        // 1. Create Stock
        Stock newStock = factory.createNewStock(
            new ProductId("prod-h2-test"),
            new WarehouseId("wh-h2-test"),
            new Owner("owner-1"),
            UnitOfMeasure.EACH
        );

        // 2. Save
        stockRepository.save(newStock);

        // 3. Retrieve
        Optional<Stock> found = stockRepository.findByProductAndWarehouse(
            new ProductId("prod-h2-test"), 
            new WarehouseId("wh-h2-test")
        );

        // 4. Verify
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isNotNull();
        assertThat(found.get().getWarehouseId().value()).isEqualTo("wh-h2-test");
    }
}