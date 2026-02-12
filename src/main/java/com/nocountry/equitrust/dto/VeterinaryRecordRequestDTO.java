package com.nocountry.equitrust.dto;

import java.time.LocalDate;

//Este se usa cuando se quiera hacer POST.
public record VeterinaryRecordRequestDTO(
        String description,
        String pdfLink,
        LocalDate date,
        String clinicAddress,
        String veterinarianLicense
) {}
