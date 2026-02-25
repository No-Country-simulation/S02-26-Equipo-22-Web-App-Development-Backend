package com.nocountry.equitrust.model.horse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Horse discipline")
public enum Discipline {

    @Schema(description = "Racing")
    RACING,

    @Schema(description = "Show jumping")
    SHOW_JUMPING,

    @Schema(description = "Dressage")
    DRESSAGE,

    @Schema(description = "Polo")
    POLO,

    @Schema(description = "Recreational riding")
    RECREATIONAL,

    @Schema(description = "Endurance")
    ENDURANCE
}
