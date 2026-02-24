package com.nocountry.equitrust.model.horse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Horse temperament")
public enum Temperament {

    @Schema(description = "Calm temperament")
    CALM,

    @Schema(description = "Moderate temperament")
    MODERATE,

    @Schema(description = "Energetic / hot-blooded temperament")
    ENERGIC
}
