package com.nocountry.equitrust.controller.dto.horse;

import com.nocountry.equitrust.controller.dto.veterinaryRecord.VeterinaryRecordResponseDTO;
import com.nocountry.equitrust.model.horse.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Detailed horse post DTO for admin views, includes veterinary records")
public record HorseDetailDTO(
        Long id,
        String title,
        String breed,
        Integer age,
        Gender gender,
        Temperament temperament,
        Discipline discipline,
        BigDecimal price,
        BigDecimal discountPrice,
        boolean sold,
        String location,
        String description,
        List<String> imageIds,
        String videoUrl,
        List<VeterinaryRecordResponseDTO> veterinaryRecords,
        VerificationStatus status,
        String rejectionReason,
        Long ownerId,
        String ownerEmail,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static HorseDetailDTO fromModel(com.nocountry.equitrust.model.horse.HorsePost horse) {
        List<String> images = horse.getImages() == null ? List.of() : horse.getImages().stream().map(HorseImage::getPublicId).toList();
        List<VeterinaryRecordResponseDTO> records = horse.getRecords() == null ? List.of() : horse.getRecords().stream().map(VeterinaryRecordResponseDTO::fromModel).toList();
        return new HorseDetailDTO(
                horse.getId(),
                horse.getTitle(),
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
                images,
                horse.getVideoUrl(),
                records,
                horse.getStatus(),
                horse.getRejectionReason(),
                horse.getOwner() != null ? horse.getOwner().getId() : null,
                horse.getOwner() != null ? horse.getOwner().getEmail() : null,
                horse.getCreatedAt(),
                horse.getUpdatedAt()
        );
    }
}

