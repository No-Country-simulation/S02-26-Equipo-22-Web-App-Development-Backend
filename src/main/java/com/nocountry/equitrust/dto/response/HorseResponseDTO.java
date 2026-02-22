package com.nocountry.equitrust.dto.response;

import com.nocountry.equitrust.model.horse.VerificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record HorseResponseDTO(

        @Schema(description = "Identificador único del caballo", example = "1")
        Long id,

        @Schema(description = "Raza del caballo", example = "Andaluz")
        String breed,

        @Schema(description = "Descripción detallada del caballo", example = "Un hermoso semental blanco, bien entrenado.")
        String description,

        @Schema(description = "Ubicación actual del caballo", example = "Buenos Aires, Argentina")
        String location,

        @Schema(description = "Precio de venta del caballo en dólares", example = "15000.00")
        BigDecimal price,

        @Schema(description = "Estado de verificación", example = "PENDING_DATA")
        VerificationStatus status,

        @Schema(description = "ID del usuario dueño del caballo", example = "1")
        Long ownerId
) { }