package com.nocountry.equitrust.controller.dto.horse;

import com.nocountry.equitrust.model.horse.Temperament;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HorseFilterRequest {
    private String breed;

    private Temperament temperament;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private Integer minAge;
    private Integer maxAge;

    private String location;

    private String search;

    private Boolean includeSold = false; // por defecto no mostrar vendidos   private String breed;
}
