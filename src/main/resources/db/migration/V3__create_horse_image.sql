CREATE TABLE horse_images (
    id BIGSERIAL PRIMARY KEY,
    public_id VARCHAR(255) NOT NULL,
    main_image BOOLEAN DEFAULT false,
    horse_post_id BIGINT NOT NULL,
    CONSTRAINT fk_horse_image FOREIGN KEY (horse_post_id) REFERENCES horse_posts(id) ON DELETE CASCADE
);

CREATE INDEX idx_horse_images_horse_post_id ON horse_images(horse_post_id);