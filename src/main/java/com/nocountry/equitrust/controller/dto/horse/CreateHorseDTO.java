package com.nocountry.equitrust.controller.dto.horse;

import com.nocountry.equitrust.model.horse.Discipline;
import com.nocountry.equitrust.model.horse.Gender;
import com.nocountry.equitrust.model.horse.Horse;
import com.nocountry.equitrust.model.horse.Temperament;
import com.nocountry.equitrust.model.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "DTO for creating a new horse")
public record CreateHorseDTO(

        @Schema(description = "Horse breed", example = "Arabian", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Breed is required")
        String breed,

        @Schema(description = "Horse age in years", example = "5", minimum = "0", maximum = "40", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Age is required")
        @Min(value = 0, message = "Age cannot be negative")
        @Max(value = 40, message = "Age cannot be greater than 40")
        Integer age,

        @Schema(description = "Horse gender", example = "STALLION", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Gender is required")
        Gender gender,

        @Schema(description = "Horse temperament", example = "CALM", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Temperament is required")
        Temperament temperament,

        @Schema(description = "Horse discipline", example = "SHOW_JUMPING", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Discipline is required")
        Discipline discipline,

        @Schema(description = "Base price of the horse", example = "15000.00", minimum = "0.01", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @Schema(description = "Optional discounted price", example = "12000.00", minimum = "0.01")
        @DecimalMin(value = "0.01", message = "Discount price must be greater than 0")
        BigDecimal discountPrice,

        @Schema(description = "Horse location", example = "Buenos Aires, Argentina", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Location is required")
        String location,

        @Schema(description = "Detailed description of the horse", example = "Excellent jumper with competition experience")
        String description,

        @Schema(
                description = "List of Cloudinary image public IDs",
                example = "[\"horse_img_1\", \"horse_img_2\"]",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "At least one image is required")
        List<String> imageIds,

        @Schema(description = "Cloudinary video public ID", example = "horse_video_1")
        String videoId

) {
    public Horse toModel(User owner) {
        return new Horse(
                breed,
                age,
                temperament,
                gender,
                discipline,
                price,
                discountPrice,
                location,
                description,
                imageIds,
                videoId,
                owner
        );
    }
}
