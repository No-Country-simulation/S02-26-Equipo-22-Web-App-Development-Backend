package com.nocountry.equitrust.controller.dto.veterinaryRecord;

import com.nocountry.equitrust.model.VeterinaryRecord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

/**
 * DTO para actualizaciones parciales (PATCH).
 * Todos los campos son opcionales.
 */
public record UpdateVeterinaryRecordDTO(
        String description,

        @URL(message = "Invalid URL format")
        String pdfLink,

        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate date,

        String clinicAddress,

        String veterinarianLicense
) {
    public void updateModel(VeterinaryRecord record) {
        if (description != null) record.setDescription(description);
        if (pdfLink != null) record.setPdfLink(pdfLink);
        if (date != null) record.setDate(date);
        if (clinicAddress != null) record.setClinicAddress(clinicAddress);
        if (veterinarianLicense != null) record.setVeterinarianLicense(veterinarianLicense);
    }
}
