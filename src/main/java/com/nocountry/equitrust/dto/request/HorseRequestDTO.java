package com.nocountry.equitrust.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record HorseRequestDTO(

        @NotBlank(message = "La raza es requerido")
        String breed,

        String description,

        String location,

        @Positive(message = "El precio debe ser positivo")
        BigDecimal price,

        @NotNull(message = "El ID del dueño es requerido")
        Long ownerId
) {}
