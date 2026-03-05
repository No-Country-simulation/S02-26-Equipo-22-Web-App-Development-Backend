package com.nocountry.equitrust.controller;

import com.nocountry.equitrust.controller.dto.admin.RejectHorseRequest;
import com.nocountry.equitrust.controller.dto.admin.VerificationResponse;
import com.nocountry.equitrust.controller.dto.horse.HorseDetailDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseResponseDTO;
import com.nocountry.equitrust.service.impl.AdminHorseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/horses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Horse Verification")
public class AdminHorseController {

    private final AdminHorseService adminHorseService;

    @GetMapping("/pending")
    @Operation(summary = "Get all horses pending verification",
               description = "Returns a paginated list of horses with PENDING_VERIFICATION status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending horses retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Not authorized (admin only)")
    })
    public ResponseEntity<Page<HorseDetailDTO>> getPendingHorses(
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable) {
        Page<HorseDetailDTO> pending = adminHorseService.getPendingHorses(pageable);
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/pending/count")
    @Operation(summary = "Get count of pending verifications",
               description = "Returns the total number of horses awaiting verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Not authorized (admin only)")
    })
    public ResponseEntity<Long> getPendingCount() {
        long count = adminHorseService.getPendingCount();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/{id}/verify")
    @Operation(summary = "Approve horse verification",
               description = "Changes horse status to VERIFIED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horse verified successfully"),
            @ApiResponse(responseCode = "400", description = "Horse is not in pending verification status"),
            @ApiResponse(responseCode = "403", description = "Not authorized (admin only)"),
            @ApiResponse(responseCode = "404", description = "Horse post not found")
    })
    public ResponseEntity<VerificationResponse> approveHorse(@PathVariable Long id) {
        VerificationResponse response = adminHorseService.approveHorse(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject horse verification",
               description = "Changes horse status to REJECTED with a reason")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horse rejected successfully"),
            @ApiResponse(responseCode = "400", description = "Horse is not in pending verification status or reason is missing"),
            @ApiResponse(responseCode = "403", description = "Not authorized (admin only)"),
            @ApiResponse(responseCode = "404", description = "Horse post not found")
    })
    public ResponseEntity<VerificationResponse> rejectHorse(
            @PathVariable Long id,
            @Valid @RequestBody RejectHorseRequest request) {
        VerificationResponse response = adminHorseService.rejectHorse(id, request.reason());
        return ResponseEntity.ok(response);
    }
}

