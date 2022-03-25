CREATE TABLE car (
    id INT NOT NULL PRIMARY KEY,
    license_plate VARCHAR(100),
    color VARCHAR(100)
);

ALTER TABLE car ADD driver_license_id VARCHAR(100);
ALTER TABLE car ADD car_model VARCHAR(100);

INSERT INTO car (id, car_model) VALUES (1,'LandRover');