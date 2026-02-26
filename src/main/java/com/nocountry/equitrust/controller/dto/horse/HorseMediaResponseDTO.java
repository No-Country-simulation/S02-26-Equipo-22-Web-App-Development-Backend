package com.nocountry.equitrust.controller.dto.horse;

import com.nocountry.equitrust.model.horse.HorseMedia;
import com.nocountry.equitrust.model.media.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record HorseMediaResponseDTO(

        @Schema(description = "Media ID", example = "1")
        Long id,

        @Schema(description = "Cloudinary public URL", example = "https://res.cloudinary.com/demo/image/upload/v1/equitrust/horses/abc123")
        String url,

        @Schema(description = "Cloudinary public ID", example = "horsetrust/horses/abc123")
        String publicId,

        @Schema(description = "Type of media", example = "IMAGE")
        MediaType mediaType
) {

        public static HorseMediaResponseDTO fromHorseMedia(HorseMedia horseMedia) {
                return HorseMediaResponseDTO.builder()
                        .id(horseMedia.getId())
                        .url(horseMedia.getUrl())
                        .publicId(horseMedia.getPublicId())
                        .mediaType(horseMedia.getMediaType())
                        .build();
        }
}
