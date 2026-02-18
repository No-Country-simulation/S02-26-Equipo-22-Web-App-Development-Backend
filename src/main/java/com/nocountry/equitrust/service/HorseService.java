package com.nocountry.equitrust.service;

import com.nocountry.equitrust.dto.request.HorseRequestDTO;
import com.nocountry.equitrust.dto.response.HorseResponseDTO;

import java.util.List;

public interface HorseService {

    HorseResponseDTO createHorse(HorseRequestDTO horseRequestDTO);

    HorseResponseDTO getHorseById(Long id);

    List<HorseResponseDTO> getAllHorses();

    HorseResponseDTO updateHorse(Long id, HorseRequestDTO horseRequestDTO);
}
