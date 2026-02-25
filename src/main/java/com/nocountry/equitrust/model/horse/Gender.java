package com.nocountry.equitrust.model.horse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Horse gender")
public enum Gender {

    @Schema(description = "Stallion (intact male)")
    STALLION,

    @Schema(description = "Mare (female)")
    MARE,

    @Schema(description = "Gelding (castrated male)")
    GELDING
}
