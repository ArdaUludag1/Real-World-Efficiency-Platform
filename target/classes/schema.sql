-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicles table (cached CarQuery data)
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    UNIQUE KEY uq_vehicle (make, model, year)
);

-- History table (stores backup/audit trail of all trips)
CREATE TABLE IF NOT EXISTS history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_id BIGINT,
    user_id BIGINT NOT NULL,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year SMALLINT NOT NULL,
    from_city VARCHAR(100) NOT NULL,
    to_city VARCHAR(100) NOT NULL,
    distance_km DOUBLE NOT NULL,
    fuel_consumption_actual DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (trip_id) REFERENCES trip(id) ON DELETE SET NULL
);

-- Trip table (stores user trip data)
CREATE TABLE IF NOT EXISTS trip (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year SMALLINT NOT NULL,
    from_city VARCHAR(100) NOT NULL,
    to_city VARCHAR(100) NOT NULL,
    distance_km DOUBLE NOT NULL,
    fuel_consumption_actual DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
); 

-- Insert sample vehicle data (you can expand this with more realistic data)
INSERT INTO vehicles (make, model, year) VALUES
('Toyota', 'Corolla', 2010),
('Toyota', 'Corolla', 2011),
('Toyota', 'Corolla', 2012),
('Toyota', 'Camry', 2010),
('Toyota', 'Camry', 2011),
('Toyota', 'Camry', 2012),
('Honda', 'Civic', 2010),
('Honda', 'Civic', 2011),
('Honda', 'Civic', 2012),
('Honda', 'Accord', 2010),
('Honda', 'Accord', 2011),
('Honda', 'Accord', 2012),
('Ford', 'Focus', 2010),
('Ford', 'Focus', 2011),
('Ford', 'Focus', 2012),
('Ford', 'Fusion', 2010),
('Ford', 'Fusion', 2011),
('Ford', 'Fusion', 2012)
ON DUPLICATE KEY UPDATE make=make; 