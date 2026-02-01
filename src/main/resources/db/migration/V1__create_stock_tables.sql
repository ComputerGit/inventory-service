CREATE TABLE stock (
  id UUID PRIMARY KEY,
  product_id VARCHAR(50) UNIQUE NOT NULL,
  total_qty INT NOT NULL,
  reserved_qty INT NOT NULL
);

CREATE TABLE stock_reservation (
  id UUID PRIMARY KEY,
  product_id VARCHAR(50),
  qty INT,
  order_id VARCHAR(50),
  status VARCHAR(20),
  created_at TIMESTAMP DEFAULT now()
);
