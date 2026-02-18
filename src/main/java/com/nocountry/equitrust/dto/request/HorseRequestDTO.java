package com.nocountry.equitrust.dto.request;

import java.math.BigDecimal;

public record HorseRequestDTO(

        String breed,

        String description,

        String location,

        BigDecimal price,

        Long ownerId
) {}
