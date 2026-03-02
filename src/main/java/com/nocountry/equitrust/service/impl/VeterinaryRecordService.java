package com.nocountry.equitrust.service.impl;

import com.nocountry.equitrust.controller.dto.veterinaryRecord.CreateVeterinaryRecordDTO;
import com.nocountry.equitrust.controller.dto.veterinaryRecord.UpdateVeterinaryRecordDTO;
import com.nocountry.equitrust.controller.dto.veterinaryRecord.VeterinaryRecordResponseDTO;
import com.nocountry.equitrust.exception.ResourceNotFoundException;
import com.nocountry.equitrust.model.horse.VeterinaryRecord;
import com.nocountry.equitrust.model.horse.Horse;
import com.nocountry.equitrust.model.user.User;
import com.nocountry.equitrust.repository.VeterinaryRecordRepository;
import com.nocountry.equitrust.repository.horse.HorseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinaryRecordService {

    private final VeterinaryRecordRepository recordRepository;
    private final HorseRepository horseRepository;

    @Transactional
    public VeterinaryRecordResponseDTO createVeterinaryRecordForHorse(Long horseId, CreateVeterinaryRecordDTO dto, User currentUser) {
        Horse horse = findHorseOrThrow(horseId);
        checkOwnerOrAdmin(horse, currentUser);

        VeterinaryRecord record = dto.toModel(horse);
        horse.updateStatusToPending();

        return VeterinaryRecordResponseDTO.fromModel(recordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<VeterinaryRecordResponseDTO> getAllRecordsByHorse(Long horseId) {
        return recordRepository.findAllByHorseId(horseId)
                .stream()
                .map(VeterinaryRecordResponseDTO::fromModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public VeterinaryRecordResponseDTO getRecordById(Long horseId, Long recordId) {
        return VeterinaryRecordResponseDTO.fromModel(findRecordOrThrow(horseId, recordId));
    }

    @Transactional
    public VeterinaryRecordResponseDTO updateRecord(Long horseId, Long recordId, UpdateVeterinaryRecordDTO dto, User currentUser) {
        VeterinaryRecord record = findRecordOrThrow(horseId, recordId);
        checkOwnerOrAdmin(record.getHorse(), currentUser);

        dto.updateModel(record);
        record.getHorse().updateStatusToPending();

        return VeterinaryRecordResponseDTO.fromModel(recordRepository.save(record));
    }

    @Transactional
    public void deleteRecord(Long horseId, Long recordId, User currentUser) {
        Horse horse = findHorseOrThrow(horseId);
        checkOwnerOrAdmin(horse, currentUser);

        long count = recordRepository.deleteByIdAndHorseId(recordId, horseId);
        if (count == 0) {
            throw new ResourceNotFoundException("Record not found with id: " + recordId + " for horse with id: " + horseId);
        }

        horse.updateStatusToPending();
    }

    // --- Private helpers ---

    private Horse findHorseOrThrow(Long horseId) {
        return horseRepository.findById(horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + horseId));
    }

    private VeterinaryRecord findRecordOrThrow(Long horseId, Long recordId) {
        return recordRepository.findByIdAndHorseId(recordId, horseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Record not found with id: " + recordId + " for horse with id: " + horseId));
    }

    private void checkOwnerOrAdmin(Horse horse, User currentUser) {
        if (!horse.isOwnedBy(currentUser) && !currentUser.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to modify this horse's records");
        }
    }
}