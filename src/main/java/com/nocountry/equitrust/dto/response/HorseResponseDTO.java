package com.nocountry.equitrust.dto.response;

import com.nocountry.equitrust.model.horse.VerificationStatus;

import java.math.BigDecimal;

public record HorseResponseDTO(

        Long id,

        String breed,

        String description,

        String location,

        BigDecimal price,

        VerificationStatus status,

        Long ownerId
) { }