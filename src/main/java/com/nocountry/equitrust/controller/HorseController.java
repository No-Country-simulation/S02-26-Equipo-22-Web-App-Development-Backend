package com.nocountry.equitrust.controller;

import com.nocountry.equitrust.controller.dto.horse.HorseFilterRequest;
import com.nocountry.equitrust.controller.dto.horse.HorseRequestDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseResponseDTO;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/horses")
@RequiredArgsConstructor
@Tag(name = "Horses", description = "API para la gestión de caballos")
public class HorseController {

    private final HorseService horseService;

    @PostMapping
    @Operation(summary = "Crear nuevo caballo", description = "Crea un nuevo caballo en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Caballo creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Dueño no encontrado")
    })
    public ResponseEntity<HorseResponseDTO> createHorse(@Valid @RequestBody HorseRequestDTO horseRequestDTO) {
        HorseResponseDTO createdHorse = horseService.createHorse(horseRequestDTO);
        return new ResponseEntity<>(createdHorse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener caballo por ID", description = "Obtiene detalles de un caballo específico por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caballo encontrado"),
            @ApiResponse(responseCode = "404", description = "Caballo no encontrado")
    })
    public ResponseEntity<HorseResponseDTO> getHorseById(@PathVariable Long id) {
        HorseResponseDTO horse = horseService.getHorseById(id);
        return ResponseEntity.ok(horse);
    }

    @GetMapping
    @Operation(
            summary = "Obtener catálogo de caballos",
            description = "Devuelve una lista paginada de caballos con posibilidad de aplicar filtros como precio, edad, raza, ubicación y estado de verificación."
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
    @Operation(summary = "Actualizar caballo", description = "Actualiza un caballo existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caballo actualizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Caballo o dueño no encontrado")
    })
    public ResponseEntity<HorseResponseDTO> updateHorse(@PathVariable Long id,
                                                        @Valid @RequestBody HorseRequestDTO horseRequestDTO) {
        HorseResponseDTO updatedHorse = horseService.updateHorse(id, horseRequestDTO);
        return ResponseEntity.ok(updatedHorse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar caballo (Soft delete)", description = "elimina un caballo marcándolo como eliminado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Caballo eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Caballo no encontrado")
    })
    public ResponseEntity<Void> deleteHorse(@PathVariable Long id) {
        horseService.deleteHorse(id);
        return ResponseEntity.noContent().build();
    }
}
