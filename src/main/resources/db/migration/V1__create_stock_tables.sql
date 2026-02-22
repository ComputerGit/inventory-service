-- 1. The Main Inventory Table (Matches StockJpaEntity)
CREATE TABLE stock_inventory (
  stock_id VARCHAR(255) PRIMARY KEY,  -- Renamed from 'id' to match Java if needed, or keep 'id'
  product_id VARCHAR(50) NOT NULL,
  warehouse_id VARCHAR(50) NOT NULL,
  owner_id VARCHAR(50) NOT NULL,
  
  -- Quantities
  qty_on_hand INT DEFAULT 0,
  qty_reserved INT DEFAULT 0,
  qty_allocated INT DEFAULT 0,
  qty_in_transit INT DEFAULT 0,
  qty_safety_stock INT DEFAULT 0,
  
  -- Audit
  version BIGINT,
  last_updated TIMESTAMP
);

-- 2. The Reservation Table (Matches StockReservationEntity - if you create one later)
CREATE TABLE stock_reservations (
  id UUID PRIMARY KEY,
  stock_id VARCHAR(255),
  order_id VARCHAR(50),
  qty INT,
  status VARCHAR(20),
  created_at TIMESTAMP DEFAULT now()
);