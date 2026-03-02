package com.nocountry.equitrust.controller.dto.horse;

import com.nocountry.equitrust.model.horse.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Horse response DTO")
public record HorseResponseDTO(

        @Schema(description = "Horse ID", example = "10")
        Long id,

        @Schema(description = "Breed", example = "Arabian")
        String breed,

        @Schema(description = "Age", example = "5")
        Integer age,

        @Schema(description = "Gender", example = "STALLION")
        Gender gender,

        @Schema(description = "Temperament", example = "CALM")
        Temperament temperament,

        @Schema(description = "Discipline", example = "SHOW_JUMPING")
        Discipline discipline,

        @Schema(description = "Base price", example = "15000.00")
        BigDecimal price,

        @Schema(description = "Discount price", example = "12000.00")
        BigDecimal discountPrice,

        @Schema(description = "Indicates if the horse is sold", example = "false")
        boolean sold,

        @Schema(description = "Location", example = "Buenos Aires")
        String location,

        @Schema(description = "Description")
        String description,

        @Schema(description = "List of image public IDs", example = "[\"horse_img_1\", \"horse_img_2\"]")
        List<String> imageIds,

        @Schema(description = "Youtube video url", example = "https://www.youtube.com/watch?v=s_IPgb7rF-g")
        String videoUrl,

        @Schema(description = "Verification status", example = "APPROVED")
        VerificationStatus status,

        @Schema(description = "Owner ID", example = "1")
        Long ownerId

) {

    public static HorseResponseDTO fromModel(Horse horse) {
        return new HorseResponseDTO(
                horse.getId(),
                horse.getBreed(),
                horse.getAge(),
                horse.getGender(),
                horse.getTemperament(),
                horse.getDiscipline(),
                horse.getPrice(),
                horse.getDiscountPrice(),
                horse.isSold(),
                horse.getLocation(),
                horse.getDescription(),
                horse.getImages()
                        .stream()
                        .map(HorseImage::getPublicId)
                        .toList(),
                horse.getVideoUrl(),
                horse.getStatus(),
                horse.getOwner() != null ? horse.getOwner().getId() : null
        );
    }
}