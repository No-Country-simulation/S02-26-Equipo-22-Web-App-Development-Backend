package com.nocountry.equitrust.controller.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to reject a horse verification")
public record RejectHorseRequest(

        @Schema(description = "Reason for rejection", example = "Veterinary documents are incomplete or unclear")
        @NotBlank(message = "Rejection reason is required")
        String reason
) {
}

