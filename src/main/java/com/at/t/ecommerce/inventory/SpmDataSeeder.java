package com.at.t.ecommerce.inventory; // <-- Make sure this matches your root package!

import com.at.t.ecommerce.inventory.domain.stock.entities.Stock;
import com.at.t.ecommerce.inventory.domain.stock.enums.RotationPolicy;
import com.at.t.ecommerce.inventory.domain.stock.enums.UnitOfMeasure;
import com.at.t.ecommerce.inventory.domain.stock.repositories.StockRepository;
import com.at.t.ecommerce.inventory.domain.stock.vo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component // <--- THIS IS CRITICAL. Without this, it won't run.
public class SpmDataSeeder implements CommandLineRunner {

	private final StockRepository stockRepository;

	public SpmDataSeeder(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		ProductId productId = new ProductId("ERICSSON-5G-RAD-001");
		WarehouseId warehouseId = new WarehouseId("TX-HUB-DALLAS");

		// Only insert if it doesn't already exist!
		if (stockRepository.findByProductAndWarehouse(productId, warehouseId).isEmpty()) {
			System.out.println("🏗️ SPM System Starting: Provisioning Initial Telecom Assets...");

			Stock ericssonRadio = new Stock(StockId.newId(), productId, warehouseId, new Owner("ATT-NETWORK-OPS"),
					RotationPolicy.FIFO, Quantity.of(5, UnitOfMeasure.EACH), Quantity.of(100, UnitOfMeasure.EACH),
					UnitOfMeasure.EACH);

			ericssonRadio.restoreStock(Quantity.of(50, UnitOfMeasure.EACH));
			ericssonRadio.reserveStock(Quantity.of(2, UnitOfMeasure.EACH));

			stockRepository.save(ericssonRadio);
			System.out.println("✅ Asset Successfully Saved to PostgreSQL!");
		} else {
			System.out.println("⚡ Telecom Assets already provisioned. Skipping seeder.");
		}
	}
}
	