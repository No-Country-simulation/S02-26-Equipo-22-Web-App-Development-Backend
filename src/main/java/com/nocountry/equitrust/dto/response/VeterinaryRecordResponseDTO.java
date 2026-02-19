package com.nocountry.equitrust.dto.response;

import com.nocountry.equitrust.model.VeterinaryRecord;

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
) {

    public static VeterinaryRecordResponseDTO fromModel(VeterinaryRecord record) {
        return new VeterinaryRecordResponseDTO(
                record.getId(),
                record.getDescription(),
                record.getPdfLink(),
                record.getDate(),
                record.getClinicAddress(),
                record.getVeterinarianLicense(),
                record.isVerified(),
                record.getHorse().getId()
        );
    }
}
