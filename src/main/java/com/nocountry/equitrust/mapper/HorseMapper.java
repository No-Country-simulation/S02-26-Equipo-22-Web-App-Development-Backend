package com.nocountry.equitrust.mapper;

import com.nocountry.equitrust.controller.dto.horse.HorseRequestDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseResponseDTO;
import com.nocountry.equitrust.model.horse.Horse;
import com.nocountry.equitrust.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class HorseMapper {

    public Horse toEntity(HorseRequestDTO dto, User owner) {
        return new Horse(
                dto.breed(),
                dto.description(),
                dto.location(),
                dto.price(),
                owner);
    }

    public HorseResponseDTO toDTO(Horse horse) {
        return new HorseResponseDTO(
                horse.getId(),
                horse.getBreed(),
                horse.getDescription(),
                horse.getLocation(),
                horse.getPrice(),
                horse.getStatus(),
                horse.getOwner() != null ? horse.getOwner().getId() : null);
    }
}
