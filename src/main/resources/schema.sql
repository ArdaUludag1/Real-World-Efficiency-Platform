-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    trim VARCHAR(100),
    fuel_consumption_city DOUBLE,
    fuel_consumption_highway DOUBLE,
    fuel_consumption_mixed DOUBLE,
    model_id VARCHAR(100),
    model_engine_position VARCHAR(100),
    model_engine_cc INT,
    model_engine_cyl INT,
    model_engine_type VARCHAR(100),
    model_engine_valves_per_cyl INT,
    model_engine_power_ps INT,
    model_engine_power_rpm INT,
    model_engine_torque_nm INT,
    model_engine_torque_rpm INT,
    model_engine_bore_mm DOUBLE,
    model_engine_stroke_mm DOUBLE,
    model_engine_compression VARCHAR(100),
    model_engine_fuel VARCHAR(100),
    model_top_speed_kph INT,
    model_0_to_100_kph DOUBLE,
    model_drive VARCHAR(100),
    model_transmission_type VARCHAR(100),
    model_seats INT,
    model_doors INT,
    model_weight_kg INT,
    model_length_mm INT,
    model_width_mm INT,
    model_height_mm INT,
    model_wheelbase_mm INT,
    model_lkm_hwy DOUBLE,
    model_lkm_mixed DOUBLE,
    model_lkm_city DOUBLE,
    model_fuel_cap_l DOUBLE,
    model_sold_in_us BOOLEAN,
    model_co2 DOUBLE,
    model_make_display VARCHAR(100),
    make_display VARCHAR(100),
    make_country VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_vehicle (make, model, year, trim)
);

-- Create trips table
CREATE TABLE IF NOT EXISTS trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT,
    start_location VARCHAR(255) NOT NULL,
    end_location VARCHAR(255) NOT NULL,
    distance_km DOUBLE NOT NULL,
    fuel_consumed_liters DOUBLE,
    fuel_efficiency DOUBLE,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    weather_condition VARCHAR(100),
    traffic_condition VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE SET NULL
);

-- Create history table
CREATE TABLE IF NOT EXISTS history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    description TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create contact_messages table
CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    subject VARCHAR(200),
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'NEW'
);
