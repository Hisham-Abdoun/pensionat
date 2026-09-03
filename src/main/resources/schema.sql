CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_number INT NOT NULL UNIQUE,
    room_type VARCHAR(50) NOT NULL,
    extra_beds INT NOT NULL,
    price_per_night DOUBLE NOT NULL
    );

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    number_of_guests INT NOT NULL,
    customer_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id)
    );