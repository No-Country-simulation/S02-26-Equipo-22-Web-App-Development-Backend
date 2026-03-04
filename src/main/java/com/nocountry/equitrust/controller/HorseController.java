package com.nocountry.equitrust.controller;

import com.nocountry.equitrust.controller.dto.horse.HorseFilterRequest;
import com.nocountry.equitrust.controller.dto.horse.CreateHorseDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseResponseDTO;
import com.nocountry.equitrust.controller.dto.horse.UpdateHorseDTO;
import com.nocountry.equitrust.model.user.User;
import com.nocountry.equitrust.service.interfaces.HorseService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/horses")
@RequiredArgsConstructor
@Tag(name = "Horses", description = "API for horse post management")
public class HorseController {

    private final HorseService horseService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @Operation(summary = "Create a new horse post", description = "Creates a new horse post in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Horse post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    public ResponseEntity<HorseResponseDTO> createHorse(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateHorseDTO createHorseDTO) {
        HorseResponseDTO createdHorse = horseService.createHorse(createHorseDTO, user);
        return new ResponseEntity<>(createdHorse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get horse post by ID", description = "Retrieves details of a specific horse post by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horse post found"),
            @ApiResponse(responseCode = "404", description = "Horse post not found")
    })
    public ResponseEntity<HorseResponseDTO> getHorseById(@PathVariable Long id) {
        HorseResponseDTO horse = horseService.getHorseById(id);
        return ResponseEntity.ok(horse);
    }

    @GetMapping
    @Operation(
            summary = "Get horse posts catalog",
            description = "Returns a paginated list of horse posts with the ability to apply filters such as price, age, breed, location, discipline, and verification status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated horse posts catalog retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters")
    })
    public ResponseEntity<Page<HorseResponseDTO>> getHorses(
            @Valid @ParameterObject HorseFilterRequest filter,
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 12,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        Page<HorseResponseDTO> horses = horseService.getHorses(filter, pageable);
        return ResponseEntity.ok(horses);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}")
    @Operation(summary = "Update horse post", description = "Updates an existing horse post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horse post updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Horse post or owner not found")
    })
    public ResponseEntity<HorseResponseDTO> updateHorse(@PathVariable Long id,
                                                        @AuthenticationPrincipal User currentUser,
                                                        @Valid @RequestBody UpdateHorseDTO updateHorseDTO) {
        HorseResponseDTO updatedHorse = horseService.updateHorse(id, updateHorseDTO, currentUser);
        return ResponseEntity.ok(updatedHorse);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete horse post", description = "Deletes a horse post (soft delete).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Horse post deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Horse post not found")
    })
    public ResponseEntity<Void> deleteHorse(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        horseService.deleteHorse(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}