package com.nocountry.equitrust.repository.horse.specification;

import com.nocountry.equitrust.controller.dto.horse.HorseFilterRequest;
import com.nocountry.equitrust.model.horse.*;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class HorseSpecifications {

    public static Specification<Horse> withFilters(HorseFilterRequest filter) {
        if (filter == null) {
            return Specification.where(null);
        }

        return Specification
                .where(search(filter.getSearch()))
                .and(breed(filter.getBreed()))
                .and(gender(filter.getGender()))
                .and(temperament(filter.getTemperament()))
                .and(discipline(filter.getDiscipline()))
                .and(priceBetween(filter.getMinPrice(), filter.getMaxPrice()))
                .and(ageBetween(filter.getMinAge(), filter.getMaxAge()))
                .and(location(filter.getLocation()))
                .and(isVerified(filter.getIsVerified()))
                .and(notSold(filter.getIncludeSold()));
    }

    public static Specification<Horse> notSold(Boolean includeSold) {
        return (root, query, cb) -> {
            if (Boolean.TRUE.equals(includeSold)) {
                return cb.conjunction();
            }
            return cb.isFalse(root.get("sold"));
        };
    }

    public static Specification<Horse> isVerified(Boolean isVerified) {
        return (root, query, cb) -> {
            if (isVerified == null) return cb.conjunction();

            if (isVerified) {
                return cb.equal(root.get("status"), VerificationStatus.VERIFIED);
            } else {
                return cb.notEqual(root.get("status"), VerificationStatus.VERIFIED);
            }
        };
    }

    public static Specification<Horse> breed(String breed) {
        return (root, query, cb) -> {
            if (breed == null || breed.isBlank()) return cb.conjunction();

            String value = breed.trim().toLowerCase();
            return cb.equal(cb.lower(root.get("breed")), value);
        };
    }

    public static Specification<Horse> temperament(Temperament temperament) {
        return (root, query, cb) -> {
            if (temperament == null) return cb.conjunction();
            return cb.equal(root.get("temperament"), temperament);
        };
    }

    public static Specification<Horse> priceBetween(BigDecimal min, BigDecimal max) {
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

    public static Specification<Horse> ageBetween(Integer min, Integer max) {
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

    public static Specification<Horse> location(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isBlank()) return cb.conjunction();

            String value = location.trim().toLowerCase();
            return cb.like(cb.lower(root.get("location")), "%" + value + "%");
        };
    }

    public static Specification<Horse> gender(Gender gender) {
        return (root, query, cb) -> {
            if (gender == null) return cb.conjunction();
            return cb.equal(root.get("gender"), gender);
        };
    }

    private static Specification<Horse> discipline(Discipline discipline) {
        return (root, query, cb) -> {
            if(discipline == null) return cb.conjunction();
            return cb.equal(root.get("discipline"), discipline);
        };
    }

    public static Specification<Horse> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return cb.conjunction();

            String pattern = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("breed")), pattern),
                    cb.like(cb.lower(root.get("location")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}