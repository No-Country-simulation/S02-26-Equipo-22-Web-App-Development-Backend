package com.nocountry.equitrust.service.interfaces;

import com.nocountry.equitrust.controller.dto.horse.HorseFilterRequest;
import com.nocountry.equitrust.controller.dto.horse.CreateHorseDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseResponseDTO;
import com.nocountry.equitrust.controller.dto.horse.UpdateHorseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HorseService {

    HorseResponseDTO createHorse(CreateHorseDTO createHorseDTO);

    HorseResponseDTO getHorseById(Long id);

    Page<HorseResponseDTO> getHorses(HorseFilterRequest filter, Pageable pageable);

    HorseResponseDTO updateHorse(Long id, UpdateHorseDTO updateHorseDTO);

    void deleteHorse(Long id);

}
