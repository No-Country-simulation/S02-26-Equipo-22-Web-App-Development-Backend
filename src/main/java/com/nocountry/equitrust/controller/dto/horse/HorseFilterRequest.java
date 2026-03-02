package com.nocountry.equitrust.controller.dto.horse;

import com.nocountry.equitrust.model.horse.Discipline;
import com.nocountry.equitrust.model.horse.Gender;
import com.nocountry.equitrust.model.horse.Temperament;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HorseFilterRequest {
    private String breed;
    private Gender gender;
    private Temperament temperament;
    private Discipline discipline;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minAge;
    private Integer maxAge;

    private String location;
    private Boolean isVerified;
    private Boolean includeSold = false; // por defecto no mostrar vendidos

    @AssertTrue(message = "minPrice must be less than or equal to maxPrice")
    public boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null) return true;
        return minPrice.compareTo(maxPrice) <= 0;
    }

    @AssertTrue(message = "minAge must be less than or equal to maxAge")
    public boolean isAgeRangeValid() {
        if (minAge == null || maxAge == null) return true;
        return minAge <= maxAge;
    }

    private String search;
}
