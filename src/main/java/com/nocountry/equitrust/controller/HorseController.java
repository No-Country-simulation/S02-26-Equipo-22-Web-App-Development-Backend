package com.nocountry.equitrust.controller;

import com.nocountry.equitrust.controller.dto.horse.HorseFilterRequest;
import com.nocountry.equitrust.controller.dto.horse.CreateHorseDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseResponseDTO;
import com.nocountry.equitrust.controller.dto.horse.UpdateHorseDTO;
import com.nocountry.equitrust.service.interfaces.HorseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/horses")
@RequiredArgsConstructor
@Tag(name = "Horses", description = "API for horse management")
public class HorseController {

    private final HorseService horseService;

    @PostMapping
    @Operation(summary = "Create a new horse", description = "Creates a new horse in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Horse created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    public ResponseEntity<HorseResponseDTO> createHorse(@Valid @RequestBody CreateHorseDTO createHorseDTO) {
        HorseResponseDTO createdHorse = horseService.createHorse(createHorseDTO);
        return new ResponseEntity<>(createdHorse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get horse by ID", description = "Retrieves details of a specific horse by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horse found"),
            @ApiResponse(responseCode = "404", description = "Horse not found")
    })
    public ResponseEntity<HorseResponseDTO> getHorseById(@PathVariable Long id) {
        HorseResponseDTO horse = horseService.getHorseById(id);
        return ResponseEntity.ok(horse);
    }

    @GetMapping
    @Operation(
            summary = "Get horse catalog",
            description = "Returns a paginated list of horses with the ability to apply filters such as price, age, breed, location, and verification status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated horse catalog retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters (e.g., invalid price or age range)")
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

    @PutMapping("/{id}")
    @Operation(summary = "Update horse", description = "Updates an existing horse.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horse updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Horse or owner not found")
    })
    public ResponseEntity<HorseResponseDTO> updateHorse(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateHorseDTO updateHorseDTO) {
        HorseResponseDTO updatedHorse = horseService.updateHorse(id, updateHorseDTO);
        return ResponseEntity.ok(updatedHorse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete horse (Soft delete)", description = "Deletes a horse by marking it as deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Horse deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Horse not found")
    })
    public ResponseEntity<Void> deleteHorse(@PathVariable Long id) {
        horseService.deleteHorse(id);
        return ResponseEntity.noContent().build();
    }
}