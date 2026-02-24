package com.nocountry.equitrust.service.impl;

import com.nocountry.equitrust.controller.dto.horse.HorseFilterRequest;
import com.nocountry.equitrust.controller.dto.horse.CreateHorseDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseResponseDTO;
import com.nocountry.equitrust.controller.dto.horse.UpdateHorseDTO;
import com.nocountry.equitrust.exception.ResourceNotFoundException;
import com.nocountry.equitrust.model.horse.Horse;
import com.nocountry.equitrust.model.user.User;
import com.nocountry.equitrust.repository.horse.HorseRepository;
import com.nocountry.equitrust.repository.UserRepository;
import com.nocountry.equitrust.repository.horse.specification.HorseSpecifications;
import com.nocountry.equitrust.service.interfaces.HorseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HorseServiceImpl implements HorseService {

    private final HorseRepository horseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public HorseResponseDTO createHorse(CreateHorseDTO createHorseDTO) {
        User owner = userRepository.findById(createHorseDTO.ownerId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Owner not found with id: " + createHorseDTO.ownerId()));

        Horse horse = createHorseDTO.toModel(owner);
        Horse savedHorse = horseRepository.save(horse);
        return HorseResponseDTO.fromModel(savedHorse);
    }

    @Override
    public HorseResponseDTO getHorseById(Long id) {
        Horse horse = horseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + id));
        return HorseResponseDTO.fromModel(horse);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<HorseResponseDTO> getHorses(HorseFilterRequest filter, Pageable pageable) {
        Specification<Horse> spec = HorseSpecifications.withFilters(filter);

        return horseRepository.findAll(spec, pageable)
                .map(HorseResponseDTO::fromModel);
    }

    @Override
    @Transactional
    public HorseResponseDTO updateHorse(Long id, UpdateHorseDTO updateHorseDTO) {
        Horse horse = horseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + id));

        updateHorseDTO.updateModel(horse);

        Horse updatedHorse = horseRepository.save(horse);
        return HorseResponseDTO.fromModel(updatedHorse);
    }

    @Override
    public void deleteHorse(Long id) {
        if (!horseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Horse not found with id: " + id);
        }
        horseRepository.deleteById(id);
    }
}
