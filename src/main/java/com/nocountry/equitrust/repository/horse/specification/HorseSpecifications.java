package com.nocountry.equitrust.repository.horse.specification;

import com.nocountry.equitrust.model.horse.Horse;
import com.nocountry.equitrust.model.horse.Temperament;
import com.nocountry.equitrust.model.horse.VerificationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class HorseSpecifications {
    public static Specification<Horse> notSold(Boolean includeSold) {
        return (root, query, cb) -> {
            if (Boolean.TRUE.equals(includeSold)) {
                return cb.conjunction();
            }
            return cb.isFalse(root.get("sold"));
        };
    }

    public static Specification<Horse> verifiedOnly() {
        return (root, query, cb) ->
                cb.equal(root.get("status"), VerificationStatus.VERIFIED);
    }

    public static Specification<Horse> breed(String breed) {
        return (root, query, cb) -> {
            if (breed == null || breed.isBlank()) return cb.conjunction();
            return cb.equal(cb.lower(root.get("breed")), breed.toLowerCase());
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
            return cb.like(cb.lower(root.get("location")),
                    "%" + location.toLowerCase() + "%");
        };
    }

    public static Specification<Horse> search(String text) {
        return (root, query, cb) -> {
            if (text == null || text.isBlank()) return cb.conjunction();

            String pattern = "%" + text.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("description")), pattern),
                    cb.like(cb.lower(root.get("breed")), pattern)
            );
        };
    }
}

