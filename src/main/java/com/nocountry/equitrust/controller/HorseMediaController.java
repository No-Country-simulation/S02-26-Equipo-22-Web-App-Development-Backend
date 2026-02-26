package com.nocountry.equitrust.controller;

import com.nocountry.equitrust.controller.dto.horse.HorseMediaResponseDTO;
import com.nocountry.equitrust.model.media.MediaType;
import com.nocountry.equitrust.service.HorseMediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/horses/{horseId}/media")
@RequiredArgsConstructor
@Tag(name = "Horse Media", description = "API para la gestión de imágenes y videos de caballos via Cloudinary")
public class HorseMediaController {

    private final HorseMediaService horseMediaService;

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir imagen/video para caballo", description = "Subida de imagen o video a Cloudinary y asociarlo al caballo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subida de imagen/video exitosamente"),
            @ApiResponse(responseCode = "404", description = "Caballo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Subida fallida")
    })
    public ResponseEntity<HorseMediaResponseDTO> uploadMedia(
            @PathVariable Long horseId,
            @Parameter(description = "Archivo a subir (imagen o video)") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Tipo de archivo: IMAGE o VIDEO") @RequestParam("mediaType") MediaType mediaType)
            throws IOException {

        HorseMediaResponseDTO response = horseMediaService.uploadMedia(horseId, file, mediaType);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener imagen/video de un caballo", description = "Retorna una lista de todas las imagenes y videos asociados al caballo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagenes/videos obtenidos exitosamente"),
            @ApiResponse(responseCode = "404", description = "Caballo no encontrado")
    })
    public ResponseEntity<List<HorseMediaResponseDTO>> getMedia(@PathVariable Long horseId) {
        List<HorseMediaResponseDTO> media = horseMediaService.getMediaByHorseId(horseId);
        return ResponseEntity.ok(media);
    }

    @DeleteMapping("/{mediaId}")
    @Operation(summary = "Elimina imagen/video", description = "Elimina las imagenes y videos de Cloudinary y elimina el registro en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Imagen/video eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Caballo o imagen/video no encontrado")
    })
    public ResponseEntity<Void> deleteMedia(
            @PathVariable Long horseId,
            @PathVariable Long mediaId) throws IOException {
        horseMediaService.deleteMedia(horseId, mediaId);
        return ResponseEntity.noContent().build();
    }
}
