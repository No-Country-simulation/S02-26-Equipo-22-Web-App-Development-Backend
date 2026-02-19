package com.nocountry.equitrust.dto.request;

import java.time.LocalDate;

public record UpdateVeterinaryRecordDTO(
        String description,
        String pdfLink,
        LocalDate date,
        String clinicAddress,
        String veterinarianLicense
) {}
