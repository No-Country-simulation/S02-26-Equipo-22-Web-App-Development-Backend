CREATE TABLE veterinary_records (
    id BIGSERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    pdf_link VARCHAR(255) NOT NULL,
    date_record DATE NOT NULL,
    clinic_address VARCHAR(255) NOT NULL,
    veterinarian_license VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    horse_post_id BIGINT NOT NULL,
    CONSTRAINT fk_vet_horse_post FOREIGN KEY (horse_post_id) REFERENCES horse_posts(id) ON DELETE CASCADE
);

CREATE INDEX idx_vet_records_horse_post_id ON veterinary_records(horse_post_id);