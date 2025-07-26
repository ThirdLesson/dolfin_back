CREATE TABLE exchange_monthly (
                                  exchange_id VARCHAR(255) NOT NULL,
                                  target_exchange CHAR(3) NOT NULL,
                                  exchange_value DECIMAL(38, 2),
                                  exchange_date DATE,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (exchange_id)
);
