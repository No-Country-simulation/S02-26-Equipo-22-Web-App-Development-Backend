package com.nocountry.equitrust.controller.dto.horse;

import com.nocountry.equitrust.model.horse.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Horse post response DTO")
public record HorseResponseDTO(

        @Schema(description = "Horse post ID", example = "10")
        Long id,

        @Schema(description = "Post title", example = "Beautiful Arabian Horse for Sale")
        String title,

        @Schema(description = "Horse breed", example = "Arabian")
        String breed,

        @Schema(description = "Horse age", example = "5")
        Integer age,

        @Schema(description = "Horse gender", example = "STALLION")
        Gender gender,

        @Schema(description = "Horse temperament", example = "CALM")
        Temperament temperament,

        @Schema(description = "Horse discipline", example = "SHOW_JUMPING")
        Discipline discipline,

        @Schema(description = "Base price", example = "15000.00")
        BigDecimal price,

        @Schema(description = "Discounted price", example = "12000.00")
        BigDecimal discountPrice,

        @Schema(description = "Is the horse sold", example = "false")
        boolean sold,

        @Schema(description = "Horse location", example = "Buenos Aires, Argentina")
        String location,

        @Schema(description = "Detailed description")
        String description,

        @Schema(description = "List of image public IDs")
        List<String> imageIds,

        @Schema(description = "Youtube video url", example = "https://www.youtube.com/watch?v=s_IPgb7rF-g")
        String videoUrl,

        @Schema(description = "Verification status", example = "VERIFIED")
        VerificationStatus status,

        @Schema(description = "Rejection reason (if status is REJECTED)")
        String rejectionReason,

        @Schema(description = "Owner user ID")
        Long ownerId,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt

) {

    public static HorseResponseDTO fromModel(HorsePost horsePost) {
        return new HorseResponseDTO(
                horsePost.getId(),
                horsePost.getTitle(),
                horsePost.getBreed(),
                horsePost.getAge(),
                horsePost.getGender(),
                horsePost.getTemperament(),
                horsePost.getDiscipline(),
                horsePost.getPrice(),
                horsePost.getDiscountPrice(),
                horsePost.isSold(),
                horsePost.getLocation(),
                horsePost.getDescription(),
                horsePost.getImages()
                        .stream()
                        .map(HorseImage::getPublicId)
                        .toList(),
                horsePost.getVideoUrl(),
                horsePost.getStatus(),
                horsePost.getRejectionReason(),
                horsePost.getOwner() != null ? horsePost.getOwner().getId() : null,
                horsePost.getCreatedAt(),
                horsePost.getUpdatedAt()
        );
    }
}