package com.nocountry.equitrust.dto;

import java.time.LocalDate;

public record VeterinaryRecordResponseDTO(
        Long id,
        String description,
        String pdfLink,
        LocalDate date,
        String clinicAddress,
        String veterinarianLicense,
        boolean verified,
        Long horseId
) {}
