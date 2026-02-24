package com.nocountry.equitrust.controller.dto.horse;

import com.nocountry.equitrust.model.horse.Discipline;
import com.nocountry.equitrust.model.horse.Gender;
import com.nocountry.equitrust.model.horse.Horse;
import com.nocountry.equitrust.model.horse.Temperament;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "DTO for partially updating a horse (PATCH)")
public record HorseUpdateDTO(

        @Schema(description = "Horse breed", example = "Arabian")
        String breed,

        @Schema(description = "Horse age in years", example = "6", minimum = "0", maximum = "40")
        Integer age,

        @Schema(description = "Horse gender", example = "FEMALE")
        Gender gender,

        @Schema(description = "Horse temperament", example = "ENERGETIC")
        Temperament temperament,

        @Schema(description = "Horse discipline", example = "DRESSAGE")
        Discipline discipline,

        @Schema(description = "Base price", example = "18000.00", minimum = "0.01")
        BigDecimal price,

        @Schema(description = "Discount price", example = "15000.00", minimum = "0.01")
        BigDecimal discountPrice,

        @Schema(description = "Horse location", example = "Cordoba, Argentina")
        String location,

        @Schema(description = "Detailed description")
        String description,

        @Schema(description = "Replace all image public IDs", example = "[\"new_img_1\", \"new_img_2\"]")
        List<String> imageIds,

        @Schema(description = "Cloudinary video public ID", example = "updated_video_1")
        String videoId

) {
    public void updateModel(Horse horse) {

        if (breed != null) horse.setBreed(breed);
        if (age != null) horse.setAge(age);
        if (gender != null) horse.setGender(gender);
        if (temperament != null) horse.setTemperament(temperament);
        if (discipline != null) horse.setDiscipline(discipline);

        if (price != null) horse.changeBasePrice(price);
        if (discountPrice != null) horse.changeDiscountPrice(discountPrice);

        if (location != null) horse.setLocation(location);
        if (description != null) horse.setDescription(description);

        if (videoId != null) horse.setVideoId(videoId);

        if (imageIds != null) {
            horse.replaceImages(imageIds);
        }
    }
}
