CREATE TABLE horse_images (
    id BIGSERIAL PRIMARY KEY,
    public_id VARCHAR(255) NOT NULL,
    main_image BOOLEAN DEFAULT false,
    horse_id BIGINT NOT NULL,
    CONSTRAINT fk_horse_image FOREIGN KEY (horse_id) REFERENCES horses(id) ON DELETE CASCADE
);