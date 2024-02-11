
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS product_definition (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  sku VARCHAR(8) UNIQUE NOT NULL,
  name VARCHAR(60)
);