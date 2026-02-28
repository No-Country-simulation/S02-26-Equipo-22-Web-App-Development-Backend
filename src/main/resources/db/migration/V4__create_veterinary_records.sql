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
    horse_id BIGINT NOT NULL,
    CONSTRAINT fk_vet_horse FOREIGN KEY (horse_id) REFERENCES horses(id) ON DELETE CASCADE
);