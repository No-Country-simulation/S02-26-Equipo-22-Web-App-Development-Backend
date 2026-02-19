package com.nocountry.equitrust.dto.request;


import com.nocountry.equitrust.model.Horse;
import com.nocountry.equitrust.model.VeterinaryRecord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

//Este se usa cuando se quiera hacer POST.
public record CreateVeterinaryRecordDTO(
        @NotBlank(message = "Se requiere descripcion")
        String description,

        @NotBlank(message = "Se requiere link a PDF")
        String pdfLink,

        @NotNull(message = "Se requiere fecha")
        LocalDate date,

        @NotBlank(message = "Se requiere direccion de Clinica veterinaria")
        String clinicAddress,

        @NotBlank(message = "Se requiere datos del Veterinario")
        String veterinarianLicense
) {
    public VeterinaryRecord toModel(Horse horse) {
        return new VeterinaryRecord(
                description,
                pdfLink,
                date,
                clinicAddress,
                veterinarianLicense,
                horse
        );
    }
}

