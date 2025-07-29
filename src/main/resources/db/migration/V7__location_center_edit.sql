ALTER TABLE location
    ADD COLUMN homepage_url VARCHAR(255),
    CHANGE COLUMN location_number tel VARCHAR(255);
