package com.nocountry.equitrust.controller.dto.veterinaryRecord;

import com.nocountry.equitrust.model.horse.VeterinaryRecord;

import java.time.LocalDate;

public record VeterinaryRecordResponseDTO(
        Long id,
        String description,
        String pdfLink,
        LocalDate date,
        String clinicAddress,
        String veterinarianLicense,
        Long horseId
) {

    public static VeterinaryRecordResponseDTO fromModel(VeterinaryRecord record) {
        return new VeterinaryRecordResponseDTO(
                record.getId(),
                record.getDescription(),
                record.getPdfLink(),
                record.getDateRecord(),
                record.getClinicAddress(),
                record.getVeterinarianLicense(),
                record.getHorse().getId()
        );
    }
}
