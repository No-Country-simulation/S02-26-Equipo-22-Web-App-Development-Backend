package com.nocountry.equitrust.service.impl;

import com.nocountry.equitrust.controller.dto.horse.HorseRequestDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseResponseDTO;
import com.nocountry.equitrust.exception.ResourceNotFoundException;
import com.nocountry.equitrust.mapper.HorseMapper;
import com.nocountry.equitrust.model.horse.Horse;
import com.nocountry.equitrust.model.user.User;
import com.nocountry.equitrust.repository.HorseRepository;
import com.nocountry.equitrust.repository.UserRepository;
import com.nocountry.equitrust.service.interfaces.HorseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorseServiceImpl implements HorseService {

    private final HorseRepository horseRepository;
    private final HorseMapper horseMapper;
    private final UserRepository userRepository;

    @Override
    public HorseResponseDTO createHorse(HorseRequestDTO horseRequestDTO) {
        User owner = userRepository.findById(horseRequestDTO.ownerId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Dueño no encontrado con id: " + horseRequestDTO.ownerId()));

        Horse horse = horseMapper.toEntity(horseRequestDTO, owner);
        Horse savedHorse = horseRepository.save(horse);
        return horseMapper.toDTO(savedHorse);
    }

    @Override
    public HorseResponseDTO getHorseById(Long id) {
        Horse horse = horseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caballo no encontrado con id: " + id));
        return horseMapper.toDTO(horse);
    }

    @Override
    public List<HorseResponseDTO> getAllHorses() {
        return horseRepository.findAll().stream()
                .map(horseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HorseResponseDTO updateHorse(Long id, HorseRequestDTO horseRequestDTO) {
        Horse horse = horseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caballo no encontrado con id: " + id));

        horse.setBreed(horseRequestDTO.breed());
        horse.setDescription(horseRequestDTO.description());
        horse.setLocation(horseRequestDTO.location());
        horse.setPrice(horseRequestDTO.price());

        if (!horse.getOwner().getId().equals(horseRequestDTO.ownerId())) {
            User newOwner = userRepository.findById(horseRequestDTO.ownerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Dueño no encontrado con id: " + horseRequestDTO.ownerId()));
            horse.setOwner(newOwner);
        }

        Horse updatedHorse = horseRepository.save(horse);
        return horseMapper.toDTO(updatedHorse);
    }

    @Override
    public void deleteHorse(Long id) {
        if (!horseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Caballo no encontrado con id: " + id);
        }
        horseRepository.deleteById(id);
    }
}
