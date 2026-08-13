CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    venue_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    total_quantity INT NOT NULL,
    available_quantity INT NOT NULL,

    CONSTRAINT fk_inventory_venue FOREIGN KEY (venue_id) REFERENCES venue(id) ON DELETE CASCADE,
    CONSTRAINT chk_inventory_total_quantity_positive CHECK (total_quantity >= 0),
    CONSTRAINT chk_inventory_available_quantity_positive CHECK (available_quantity >= 0),
    CONSTRAINT chk_inventory_total_and_available_quantity_relation CHECK (total_quantity >= available_quantity)
);