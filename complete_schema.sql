-- Complete database schema for Route & Weather Dashboard
-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS history;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create vehicles table
CREATE TABLE vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create history table
CREATE TABLE history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    from_city VARCHAR(100) NOT NULL,
    to_city VARCHAR(100) NOT NULL,
    distance_km DOUBLE NOT NULL,
    base_consumption_l DOUBLE NOT NULL,
    adjusted_consumption_l DOUBLE NOT NULL,
    weather_from VARCHAR(255),
    weather_to VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- Insert sample vehicle data
INSERT INTO vehicles (make, model, year) VALUES
('Toyota', 'Camry', 2020),
('Honda', 'Civic', 2019),
('Ford', 'Focus', 2021),
('BMW', '3 Series', 2022),
('Mercedes', 'C-Class', 2021),
('Audi', 'A4', 2020),
('Volkswagen', 'Golf', 2019),
('Nissan', 'Altima', 2021); 