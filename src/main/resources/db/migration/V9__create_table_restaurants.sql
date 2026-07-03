CREATE TABLE IF NOT EXISTS restaurants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address_street VARCHAR(255) NOT NULL,
    address_number VARCHAR(20) NOT NULL,
    address_city VARCHAR(255) NOT NULL,
    address_zip_code VARCHAR(8),
    cuisine_type VARCHAR(255) NOT NULL,
    opening_hours VARCHAR(255) NOT NULL,
    owner_id UUID NOT NULL,
    CONSTRAINT fk_restaurants_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);
