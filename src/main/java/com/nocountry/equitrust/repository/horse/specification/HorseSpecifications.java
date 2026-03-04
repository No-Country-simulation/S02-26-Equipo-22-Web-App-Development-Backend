package com.nocountry.equitrust.repository.horse.specification;

import com.nocountry.equitrust.controller.dto.horse.HorseFilterRequest;
import com.nocountry.equitrust.model.horse.*;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HorseSpecifications {

    public static Specification<HorsePost> withFilters(HorseFilterRequest filter) {
        if (filter == null) {
            return Specification.where(null);
        }

        return Specification
                .where(search(filter.getSearch()))
                .and(breedsIn(filter.getBreeds()))
                .and(locationsIn(filter.getLocations()))
                .and(disciplinesIn(filter.getDisciplines()))
                .and(gender(filter.getGender()))
                .and(temperament(filter.getTemperament()))
                .and(priceBetween(filter.getMinPrice(), filter.getMaxPrice()))
                .and(ageBetween(filter.getMinAge(), filter.getMaxAge()))
                .and(isVerified(filter.getIsVerified()))
                .and(notSold(filter.getIncludeSold()));
    }

    public static Specification<HorsePost> notSold(Boolean includeSold) {
        return (root, query, cb) -> {
            if (Boolean.TRUE.equals(includeSold)) {
                return cb.conjunction();
            }
            return cb.isFalse(root.get("sold"));
        };
    }

    public static Specification<HorsePost> isVerified(Boolean isVerified) {
        return (root, query, cb) -> {
            if (isVerified == null) return cb.conjunction();

            if (isVerified) {
                return cb.equal(root.get("status"), VerificationStatus.VERIFIED);
            } else {
                return cb.notEqual(root.get("status"), VerificationStatus.VERIFIED);
            }
        };
    }

    // Multi-selection filters (checkboxes)
    public static Specification<HorsePost> breedsIn(List<String> breeds) {
        return (root, query, cb) -> {
            if (breeds == null || breeds.isEmpty()) return cb.conjunction();

            // Normalize: trim, toLowerCase, remove blanks and nulls
            List<String> cleaned = breeds.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toLowerCase)
                    .distinct()
                    .collect(Collectors.toList());

            if (cleaned.isEmpty()) return cb.conjunction();

            Expression<String> breedExpr = cb.lower(root.get("breed"));
            return breedExpr.in(cleaned);
        };
    }

    public static Specification<HorsePost> locationsIn(List<String> locations) {
        return (root, query, cb) -> {
            if (locations == null || locations.isEmpty()) return cb.conjunction();

            // Normalize: trim, toLowerCase, remove blanks and nulls
            List<String> cleaned = locations.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toLowerCase)
                    .distinct()
                    .collect(Collectors.toList());

            if (cleaned.isEmpty()) return cb.conjunction();

            Expression<String> locExpr = cb.lower(root.get("location"));
            return locExpr.in(cleaned);
        };
    }

    public static Specification<HorsePost> disciplinesIn(List<Discipline> disciplines) {
        return (root, query, cb) -> {
            if (disciplines == null || disciplines.isEmpty()) return cb.conjunction();

            // Filter nulls and deduplicate
            List<Discipline> cleaned = disciplines.stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            if (cleaned.isEmpty()) return cb.conjunction();
            return root.get("discipline").in(cleaned);
        };
    }

    public static Specification<HorsePost> temperament(Temperament temperament) {
        return (root, query, cb) -> {
            if (temperament == null) return cb.conjunction();
            return cb.equal(root.get("temperament"), temperament);
        };
    }

    public static Specification<HorsePost> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return cb.conjunction();

            if (min != null && max != null && min.compareTo(max) > 0) {
                return cb.conjunction();
            }

            if (min != null && max != null)
                return cb.between(root.get("price"), min, max);

            if (min != null)
                return cb.greaterThanOrEqualTo(root.get("price"), min);

            return cb.lessThanOrEqualTo(root.get("price"), max);
        };
    }

    public static Specification<HorsePost> ageBetween(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return cb.conjunction();

            if (min != null && max != null && min > max) {
                return cb.conjunction();
            }

            if (min != null && max != null)
                return cb.between(root.get("age"), min, max);

            if (min != null)
                return cb.greaterThanOrEqualTo(root.get("age"), min);

            return cb.lessThanOrEqualTo(root.get("age"), max);
        };
    }

    public static Specification<HorsePost> gender(Gender gender) {
        return (root, query, cb) -> {
            if (gender == null) return cb.conjunction();
            return cb.equal(root.get("gender"), gender);
        };
    }

    public static Specification<HorsePost> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return cb.conjunction();

            String pattern = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("breed")), pattern),
                    cb.like(cb.lower(root.get("location")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}