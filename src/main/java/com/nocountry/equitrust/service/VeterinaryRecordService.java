package com.nocountry.equitrust.service;

import com.nocountry.equitrust.controller.dto.veterinaryRecord.CreateVeterinaryRecordDTO;
import com.nocountry.equitrust.controller.dto.veterinaryRecord.UpdateVeterinaryRecordDTO;
import com.nocountry.equitrust.exception.ResourceNotFoundException;
import com.nocountry.equitrust.model.horse.VeterinaryRecord;
import com.nocountry.equitrust.model.horse.HorsePost;
import com.nocountry.equitrust.repository.VeterinaryRecordRepository;
import com.nocountry.equitrust.repository.horse.HorseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinaryRecordService {

    private final VeterinaryRecordRepository recordRepository;
    private final HorseRepository horseRepository;

    /**
     * Creates a veterinary record associated with a horse post.
     */
    @Transactional
    public VeterinaryRecord createVeterinaryRecordForHorse(
            Long horseId,
            CreateVeterinaryRecordDTO dto) {
        HorsePost horsePost = horseRepository.findById(horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Horse post not found with id: " + horseId));

        VeterinaryRecord record = dto.toModel(horsePost);

        // If the horse was verified and a new record is added, the ADMIN must verify again
        horsePost.updateStatusToPending();

        return recordRepository.save(record);
    }

    /**
     * Gets all veterinary records for a horse post.
     */
    @Transactional(readOnly = true)
    public List<VeterinaryRecord> getAllRecordsByHorse(Long horseId) {
        return recordRepository.findAllByHorsePostId(horseId);
    }

    /**
     * Gets a specific veterinary record.
     */
    @Transactional(readOnly = true)
    public VeterinaryRecord getRecordById(Long horseId, Long recordId) {
        return recordRepository
                .findByIdAndHorsePostId(recordId, horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + recordId + " for horse post with id: " + horseId));
    }

    /**
     * Updates an existing veterinary record.
     */
    @Transactional
    public VeterinaryRecord updateRecord(
            Long horseId,
            Long recordId,
            UpdateVeterinaryRecordDTO dto) {

        VeterinaryRecord record = recordRepository.findByIdAndHorsePostId(recordId, horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + recordId + " for horse post with id: " + horseId));

        dto.updateModel(record);

        // Any change in documents invalidates the previous trust
        record.getHorsePost().updateStatusToPending();

        return recordRepository.save(record);
    }

    /**
     * Deletes a veterinary record.
     */
    @Transactional
    public void deleteRecord(Long horseId, Long recordId) {
        HorsePost horsePost = horseRepository.findById(horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Horse post not found with id: " + horseId));

        long count = recordRepository.deleteByIdAndHorsePostId(recordId, horseId);

        if (count == 0) {
            throw new ResourceNotFoundException("Record not found with id: " + recordId + " for horse post with id: " + horseId);
        }

        // If a record is deleted, the horse ALWAYS returns to pending verification
        horsePost.updateStatusToPending();
    }
}

