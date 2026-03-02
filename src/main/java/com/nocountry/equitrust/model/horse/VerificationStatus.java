package com.nocountry.equitrust.model.horse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Verification status of the horse")
public enum VerificationStatus {

    @Schema(description = "Waiting for required data")
    PENDING_DATA,

    @Schema(description = "Pending manual verification")
    PENDING_VERIFICATION,

    @Schema(description = "Verified and approved")
    VERIFIED,

    @Schema(description = "Rejected during verification")
    REJECTED
}
