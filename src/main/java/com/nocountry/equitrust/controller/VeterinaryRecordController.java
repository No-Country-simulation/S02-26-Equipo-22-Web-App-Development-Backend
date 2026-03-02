package com.nocountry.equitrust.controller;

import com.nocountry.equitrust.controller.dto.veterinaryRecord.CreateVeterinaryRecordDTO;
import com.nocountry.equitrust.controller.dto.veterinaryRecord.UpdateVeterinaryRecordDTO;
import com.nocountry.equitrust.controller.dto.veterinaryRecord.VeterinaryRecordResponseDTO;
import com.nocountry.equitrust.model.user.User;
import com.nocountry.equitrust.service.impl.VeterinaryRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/horses/{horseId}/records")
@RequiredArgsConstructor
@Tag(name = "Veterinary Records", description = "API for managing veterinary records of a horse")
public class VeterinaryRecordController {

    private final VeterinaryRecordService recordService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a veterinary record", description = "Creates a new veterinary record for a specific horse. Only the owner or an admin can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Horse not found")
    })
    public ResponseEntity<VeterinaryRecordResponseDTO> createVeterinaryRecord(
            @PathVariable Long horseId,
            @Valid @RequestBody CreateVeterinaryRecordDTO dto,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recordService.createVeterinaryRecordForHorse(horseId, dto, currentUser));
    }

    @GetMapping
    @Operation(summary = "Get all veterinary records", description = "Returns all veterinary records associated with a specific horse.")
    @ApiResponse(responseCode = "200", description = "Records retrieved successfully")
    public ResponseEntity<List<VeterinaryRecordResponseDTO>> getAllRecords(
            @PathVariable Long horseId) {

        return ResponseEntity.ok(recordService.getAllRecordsByHorse(horseId));
    }

    @GetMapping("/{recordId}")
    @Operation(summary = "Get a veterinary record by ID", description = "Returns a specific veterinary record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record found"),
            @ApiResponse(responseCode = "404", description = "Record or horse not found")
    })
    public ResponseEntity<VeterinaryRecordResponseDTO> getRecordById(
            @PathVariable Long horseId,
            @PathVariable Long recordId) {

        return ResponseEntity.ok(recordService.getRecordById(horseId, recordId));
    }

    @PatchMapping("/{recordId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a veterinary record", description = "Updates an existing veterinary record. Only the owner or an admin can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Record or horse not found")
    })
    public ResponseEntity<VeterinaryRecordResponseDTO> updateRecord(
            @PathVariable Long horseId,
            @PathVariable Long recordId,
            @Valid @RequestBody UpdateVeterinaryRecordDTO dto,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(recordService.updateRecord(horseId, recordId, dto, currentUser));
    }

    @DeleteMapping("/{recordId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a veterinary record", description = "Deletes a veterinary record. Only the owner or an admin can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Record deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Record or horse not found")
    })
    public ResponseEntity<Void> deleteRecord(
            @PathVariable Long horseId,
            @PathVariable Long recordId,
            @AuthenticationPrincipal User currentUser) {

        recordService.deleteRecord(horseId, recordId, currentUser);
        return ResponseEntity.noContent().build();
    }
}