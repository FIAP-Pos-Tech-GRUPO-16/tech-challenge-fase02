CREATE TABLE IF NOT EXISTS menu_items (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    available_only_on_site BOOLEAN NOT NULL,
    photo_path VARCHAR(500),
    restaurant_id UUID NOT NULL,
    CONSTRAINT chk_menu_items_price_positive CHECK (price > 0),
    CONSTRAINT fk_menu_items_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);
