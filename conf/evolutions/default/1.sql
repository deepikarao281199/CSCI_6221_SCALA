# --- !Ups

CREATE TABLE weather_records (
    id SERIAL PRIMARY KEY,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    temperature DOUBLE PRECISION NOT NULL,
    min_temp DOUBLE PRECISION NOT NULL,
    max_temp DOUBLE PRECISION NOT NULL,
    feels_like DOUBLE PRECISION NOT NULL,
    humidity INTEGER NOT NULL,
    pressure INTEGER NOT NULL,
    main VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    icon VARCHAR(50) NOT NULL,
    wind_speed DOUBLE PRECISION NOT NULL,
    wind_deg INTEGER NOT NULL,
    timestamp BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_weather_city ON weather_records(city);
CREATE INDEX idx_weather_timestamp ON weather_records(timestamp);

# --- !Downs

DROP TABLE IF EXISTS weather_records;