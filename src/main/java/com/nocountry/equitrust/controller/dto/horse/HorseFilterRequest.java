package com.nocountry.equitrust.controller.dto.horse;

import com.nocountry.equitrust.model.horse.Discipline;
import com.nocountry.equitrust.model.horse.Gender;
import com.nocountry.equitrust.model.horse.Temperament;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Filter parameters for horse catalog")
public class HorseFilterRequest {

    @Schema(description = "Filter by breeds (exact match, case insensitive)")
    private List<String> breeds;

    @Schema(description = "Filter by provinces (exact match, case insensitive)")
    private List<String> locations;

    @Schema(description = "Filter by disciplines")
    private List<Discipline> disciplines;

    @Schema(description = "Filter by gender")
    private Gender gender;

    @Schema(description = "Filter by temperament")
    private Temperament temperament;

    @Schema(description = "Minimum price filter")
    private BigDecimal minPrice;

    @Schema(description = "Maximum price filter")
    private BigDecimal maxPrice;

    @Schema(description = "Minimum age filter")
    private Integer minAge;

    @Schema(description = "Maximum age filter")
    private Integer maxAge;

    @Schema(description = "Filter only verified horses")
    private Boolean isVerified;

    @Schema(description = "Include sold horses in results")
    private Boolean includeSold = false;

    @Schema(description = "Full text search across title, breed, location and description")
    private String search;

    @AssertTrue(message = "minPrice must be less than or equal to maxPrice")
    @Schema(hidden = true)
    public boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null) return true;
        return minPrice.compareTo(maxPrice) <= 0;
    }

    @AssertTrue(message = "minAge must be less than or equal to maxAge")
    @Schema(hidden = true)
    public boolean isAgeRangeValid() {
        if (minAge == null || maxAge == null) return true;
        return minAge <= maxAge;
    }
}
