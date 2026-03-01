CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE INDEX idx_horses_breed_trgm ON horses USING GIN (lower(breed) gin_trgm_ops);
CREATE INDEX idx_horses_location_trgm ON horses USING GIN (lower(location) gin_trgm_ops);
CREATE INDEX idx_horses_description_trgm ON horses USING GIN (lower(description) gin_trgm_ops);