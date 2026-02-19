package com.nocountry.equitrust.controller;

import com.nocountry.equitrust.dto.request.CreateVeterinaryRecordDTO;
import com.nocountry.equitrust.dto.response.VeterinaryRecordResponseDTO;
import com.nocountry.equitrust.service.VeterinaryRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/horses/{horseId}/records")
@RequiredArgsConstructor
@Tag(name = "Veterinary Records", description = "API para la gestión de registros veterinarios de un caballo")
public class VeterinaryRecordController {

    private final VeterinaryRecordService recordService;

    /**
     * Crea un nuevo registro veterinario para un caballo específico.
     */
    @PostMapping
    @Operation(summary = "Crear registro veterinario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Caballo no encontrado")
    })
    public ResponseEntity<VeterinaryRecordResponseDTO> createVeterinaryRecord(
            @PathVariable Long horseId,
            @Valid @RequestBody CreateVeterinaryRecordDTO dto) {

        VeterinaryRecordResponseDTO response =
                recordService.createVeterinaryRecordForHorse(horseId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene todos los registros veterinarios de un caballo.
     */
    @GetMapping
    @Operation(summary = "Obtener todos los registros veterinarios de un caballo")
    @ApiResponse(responseCode = "200", description = "Lista de registros obtenida correctamente")
    public ResponseEntity<List<VeterinaryRecordResponseDTO>> getAllRecords(
            @PathVariable Long horseId) {

        List<VeterinaryRecordResponseDTO> records =
                recordService.getAllRecordsByHorse(horseId);

        return ResponseEntity.ok(records);
    }

    /**
     * Obtiene un registro veterinario específico por ID.
     */
    @GetMapping("/{recordId}")
    @Operation(summary = "Obtener registro veterinario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro o caballo no encontrado")
    })
    public ResponseEntity<VeterinaryRecordResponseDTO> getRecordById(
            @PathVariable Long horseId,
            @PathVariable Long recordId) {

        VeterinaryRecordResponseDTO record =
                recordService.getRecordById(horseId, recordId);

        return ResponseEntity.ok(record);
    }

    /**
     * Actualiza un registro veterinario existente.
     */
    @PutMapping("/{recordId}")
    @Operation(summary = "Actualizar registro veterinario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Registro o caballo no encontrado")
    })
    public ResponseEntity<VeterinaryRecordResponseDTO> updateRecord(
            @PathVariable Long horseId,
            @PathVariable Long recordId,
            @Valid @RequestBody CreateVeterinaryRecordDTO dto) {

        VeterinaryRecordResponseDTO updated =
                recordService.updateRecord(horseId, recordId, dto);

        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina un registro veterinario.
     */
    @DeleteMapping("/{recordId}")
    @Operation(summary = "Eliminar registro veterinario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro o caballo no encontrado")
    })
    public ResponseEntity<Void> deleteRecord(
            @PathVariable Long horseId,
            @PathVariable Long recordId) {

        recordService.deleteRecord(horseId, recordId);

        return ResponseEntity.noContent().build();
    }
}
