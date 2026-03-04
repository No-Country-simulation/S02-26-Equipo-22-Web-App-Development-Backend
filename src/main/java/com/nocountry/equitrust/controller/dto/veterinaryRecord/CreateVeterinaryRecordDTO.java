package com.nocountry.equitrust.controller.dto.veterinaryRecord;


import com.nocountry.equitrust.model.horse.VeterinaryRecord;
import com.nocountry.equitrust.model.horse.HorsePost;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

//Este se usa cuando se quiera hacer POST.
public record CreateVeterinaryRecordDTO(
        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "PDF link is required")
        @URL(message = "Invalid URL format")
        String pdfLink,

        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate date,

        @NotBlank(message = "Clinic address is required")
        String clinicAddress,

        @NotBlank(message = "Veterinarian license/data is required")
        String veterinarianLicense
) {
    public VeterinaryRecord toModel(HorsePost horsePost) {
        return new VeterinaryRecord(
                this.description,
                this.pdfLink,
                this.date,
                this.clinicAddress,
                this.veterinarianLicense,
                horsePost
        );
    }
}

