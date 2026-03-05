package com.nocountry.equitrust.service.impl;

import com.nocountry.equitrust.controller.dto.horse.HorseFilterRequest;
import com.nocountry.equitrust.controller.dto.horse.CreateHorseDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseResponseDTO;
import com.nocountry.equitrust.controller.dto.horse.UpdateHorseDTO;
import com.nocountry.equitrust.exception.ResourceNotFoundException;
import com.nocountry.equitrust.model.horse.HorsePost;
import com.nocountry.equitrust.model.user.User;
import com.nocountry.equitrust.repository.horse.HorseRepository;
import com.nocountry.equitrust.repository.horse.specification.HorseSpecifications;
import com.nocountry.equitrust.service.interfaces.HorseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HorseServiceImpl implements HorseService {

    private final HorseRepository horseRepository;

    @Override
    @Transactional
    public HorseResponseDTO createHorse(CreateHorseDTO createHorseDTO, User currentUser) {
        HorsePost horsePost = createHorseDTO.toModel(currentUser);
        HorsePost savedHorsePost = horseRepository.save(horsePost);
        return HorseResponseDTO.fromModel(savedHorsePost);
    }

    @Override
    @Transactional(readOnly = true)
    public HorseResponseDTO getHorseById(Long id) {
        HorsePost horsePost = horseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horse post not found with id: " + id));
        return HorseResponseDTO.fromModel(horsePost);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<HorseResponseDTO> getHorses(HorseFilterRequest filter, Pageable pageable) {
        Specification<HorsePost> spec = HorseSpecifications.withFilters(filter);

        return horseRepository.findAll(spec, pageable)
                .map(HorseResponseDTO::fromModel);
    }

    @Override
    @Transactional
    public HorseResponseDTO updateHorse(Long id, UpdateHorseDTO updateHorseDTO, User currentUser) {
        HorsePost horsePost = horseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horse post not found with id: " + id));

        if (!horsePost.isOwnedBy(currentUser) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("Not allowed");
        }

        updateHorseDTO.updateModel(horsePost);

        HorsePost updatedHorsePost = horseRepository.save(horsePost);
        return HorseResponseDTO.fromModel(updatedHorsePost);
    }

    @Override
    @Transactional
    public void deleteHorse(Long id, User currentUser) {
        HorsePost horsePost = horseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horse post not found with id: " + id));

        if (!horsePost.isOwnedBy(currentUser) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("Not allowed");
        }
        horseRepository.delete(horsePost);
    }

    @Transactional
    public HorseResponseDTO requestVerification(Long id, User currentUser) {
        HorsePost horsePost = horseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horse post not found with id: " + id));

        if (!horsePost.isOwnedBy(currentUser)) {
            throw new AccessDeniedException("Not allowed");
        }

        horsePost.requestVerification();
        HorsePost updated = horseRepository.save(horsePost);

        return HorseResponseDTO.fromModel(updated);
    }
}
