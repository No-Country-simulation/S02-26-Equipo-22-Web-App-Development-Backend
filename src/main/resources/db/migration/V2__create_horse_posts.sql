CREATE TABLE horse_posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    breed VARCHAR(255) NOT NULL,
    age INTEGER NOT NULL,
    gender VARCHAR(50) NOT NULL,
    temperament VARCHAR(50) NOT NULL,
    discipline VARCHAR(50) NOT NULL,
    price NUMERIC(15,2) NOT NULL,
    discount_price NUMERIC(15,2),
    sold BOOLEAN NOT NULL DEFAULT false,
    location VARCHAR(255) NOT NULL,
    verification_status VARCHAR(50) NOT NULL DEFAULT 'PENDING_DATA',
    description TEXT,
    video_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT chk_discount CHECK (discount_price IS NULL OR discount_price < price)
);

CREATE INDEX idx_horse_posts_price ON horse_posts(price);
CREATE INDEX idx_horse_posts_age ON horse_posts(age);
CREATE INDEX idx_horse_posts_title_lower ON horse_posts(lower(title));

-- Para el filtro principal del catalogo
CREATE INDEX idx_horse_posts_main_filter ON horse_posts(verification_status, sold, deleted);