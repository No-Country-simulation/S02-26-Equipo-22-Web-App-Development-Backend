package com.nocountry.equitrust.controller.dto.admin;

import com.nocountry.equitrust.model.horse.VerificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Horse verification response")
public record VerificationResponse(

        @Schema(description = "Horse ID")
        Long horseId,

        @Schema(description = "New verification status")
        VerificationStatus status,

        @Schema(description = "Rejection reason (if rejected)")
        String rejectionReason,

        @Schema(description = "Timestamp of the action")
        LocalDateTime timestamp
) {
}

