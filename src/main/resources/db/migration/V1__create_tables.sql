CREATE TABLE item (
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      name VARCHAR(100) NOT NULL,
      price INT NOT NULL
);

CREATE INDEX idx_item_name ON item(name);

CREATE TABLE inventory (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       item_id BIGINT NOT NULL,
       qty INT NOT NULL,
       type VARCHAR(1) NOT NULL CHECK (type IN ('T', 'W')),
       CONSTRAINT fk_inventory_item FOREIGN KEY (item_id) REFERENCES item(id)
);

CREATE INDEX idx_inventory_item_id ON inventory(item_id);
CREATE INDEX idx_inventory_type ON inventory(type);

CREATE TABLE "order" (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     order_no VARCHAR(10) NOT NULL UNIQUE,
     item_id BIGINT NOT NULL,
     qty INT NOT NULL,
     price INT NOT NULL,
     CONSTRAINT fk_order_item FOREIGN KEY (item_id) REFERENCES item(id)
);

CREATE INDEX idx_order_item_id ON "order"(item_id);
CREATE INDEX idx_order_order_no ON "order"(order_no);
