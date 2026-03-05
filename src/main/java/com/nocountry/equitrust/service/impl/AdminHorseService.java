package com.nocountry.equitrust.service.impl;

import com.nocountry.equitrust.controller.dto.admin.VerificationResponse;
import com.nocountry.equitrust.controller.dto.horse.HorseDetailDTO;
import com.nocountry.equitrust.exception.ResourceNotFoundException;
import com.nocountry.equitrust.model.horse.HorsePost;
import com.nocountry.equitrust.model.horse.VerificationStatus;
import com.nocountry.equitrust.repository.horse.HorseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminHorseService {

    private final HorseRepository horseRepository;

    /**
     * Get all horses pending verification (PENDING_VERIFICATION status)
     */
    @Transactional(readOnly = true)
    public Page<HorseDetailDTO> getPendingHorses(Pageable pageable) {
        return horseRepository.findByStatus(VerificationStatus.PENDING_VERIFICATION, pageable)
                .map(HorseDetailDTO::fromModel);
    }

    /**
     * Approve a horse verification
     */
    @Transactional
    public VerificationResponse approveHorse(Long horseId) {
        HorsePost horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Horse post not found with id: " + horseId));

        horse.approve();
        horseRepository.save(horse);

        log.info("Horse {} approved by admin", horseId);

        return new VerificationResponse(
                horse.getId(),
                horse.getStatus(),
                null,
                LocalDateTime.now()
        );
    }

    /**
     * Reject a horse verification with a reason
     */
    @Transactional
    public VerificationResponse rejectHorse(Long horseId, String reason) {
        HorsePost horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Horse post not found with id: " + horseId));

        horse.reject(reason);
        horseRepository.save(horse);

        log.info("Horse {} rejected by admin. Reason: {}", horseId, reason);

        return new VerificationResponse(
                horse.getId(),
                horse.getStatus(),
                horse.getRejectionReason(),
                LocalDateTime.now()
        );
    }

    /**
     * Get count of pending verifications
     */
    @Transactional(readOnly = true)
    public long getPendingCount() {
        return horseRepository.countByStatus(VerificationStatus.PENDING_VERIFICATION);
    }
}
