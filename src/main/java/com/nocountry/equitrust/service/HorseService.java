package com.nocountry.equitrust.service;

import com.nocountry.equitrust.dto.request.HorseRequestDTO;
import com.nocountry.equitrust.dto.response.HorseResponseDTO;

public interface HorseService {

    HorseResponseDTO createHorse(HorseRequestDTO horseRequestDTO);

    HorseResponseDTO getHorseById(Long id);
}
