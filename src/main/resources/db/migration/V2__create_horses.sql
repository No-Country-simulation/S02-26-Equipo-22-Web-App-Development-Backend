CREATE TABLE horses (
    id BIGSERIAL PRIMARY KEY,
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
    video_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT chk_discount CHECK (discount_price IS NULL OR discount_price < price)
);

CREATE INDEX idx_horses_price ON horses(price);
CREATE INDEX idx_horses_age ON horses(age);

-- Para el filtro principal del catalogo
CREATE INDEX idx_horses_main_filter ON horses(verification_status, sold, deleted);