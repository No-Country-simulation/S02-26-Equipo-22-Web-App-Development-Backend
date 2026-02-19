package com.nocountry.equitrust.service.interfaces;

import com.nocountry.equitrust.controller.dto.horse.HorseRequestDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseResponseDTO;

import java.util.List;

public interface HorseService {

    HorseResponseDTO createHorse(HorseRequestDTO horseRequestDTO);

    HorseResponseDTO getHorseById(Long id);

    List<HorseResponseDTO> getAllHorses();

    HorseResponseDTO updateHorse(Long id, HorseRequestDTO horseRequestDTO);

    void deleteHorse(Long id);
}
