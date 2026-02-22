package com.nocountry.equitrust.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record HorseRequestDTO(

        @Schema(description = "Raza del caballo", example = "Andaluz")
        @NotBlank(message = "La raza es requerido")
        String breed,

        @Schema(description = "Descripción detallada del caballo", example = "Un hermoso semental blanco, bien entrenado.")
        String description,

        @Schema(description = "Ubicación actual del caballo", example = "Buenos Aires, Argentina")
        String location,

        @Schema(description = "Precio de venta del caballo en dólares", example = "15000.00")
        @Positive(message = "El precio debe ser positivo")
        BigDecimal price,

        @Schema(description = "ID del usuario dueño del caballo", example = "1")
        @NotNull(message = "El ID del dueño es requerido")
        Long ownerId
) {}
