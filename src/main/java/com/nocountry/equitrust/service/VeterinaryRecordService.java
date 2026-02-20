package com.nocountry.equitrust.service;

import com.nocountry.equitrust.controller.dto.veterinaryRecord.CreateVeterinaryRecordDTO;
import com.nocountry.equitrust.controller.dto.veterinaryRecord.UpdateVeterinaryRecordDTO;
import com.nocountry.equitrust.model.Horse;
import com.nocountry.equitrust.model.VeterinaryRecord;
import com.nocountry.equitrust.repository.HorseRepository;
import com.nocountry.equitrust.repository.VeterinaryRecordRepository;
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
     * Crea un registro veterinario asociado a un caballo.
     */
    @Transactional
    public VeterinaryRecord createVeterinaryRecordForHorse(
            Long horseId,
            CreateVeterinaryRecordDTO dto) {
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + horseId));

        VeterinaryRecord record = dto.toModel(horse);

        // Si el caballo estaba verificado y agrega un registro nuevo, el ADMIN debe verificar nuevamente
        horse.updateStatusToPending();

        return recordRepository.save(record);
    }

    /**
     * Obtiene todos los registros veterinarios de un caballo.
     */
    @Transactional(readOnly = true)
    public List<VeterinaryRecord> getAllRecordsByHorse(Long horseId) {
        return recordRepository.findAllByHorseId(horseId);
    }

    /**
     * Obtiene un registro veterinario específico.
     */
    @Transactional(readOnly = true)
    public VeterinaryRecord getRecordById(Long horseId, Long recordId) {
        return recordRepository
                .findByIdAndHorseId(recordId, horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + recordId + " for horse with id: " + horseId));
    }

    /**
     * Actualiza un registro veterinario existente.
     */
    @Transactional
    public VeterinaryRecord updateRecord(
            Long horseId,
            Long recordId,
            UpdateVeterinaryRecordDTO dto) {

        VeterinaryRecord record = recordRepository.findByIdAndHorseId(recordId, horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + recordId + " for horse with id: " + horseId));

        dto.updateModel(record);

        // Cualquier cambio en documentos invalida la confianza previa
        record.getHorse().updateStatusToPending();

        return recordRepository.save(record);
    }

    /**
     * Elimina un registro veterinario.
     */
    @Transactional
    public void deleteRecord(Long horseId, Long recordId) {
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + horseId));

        long count = recordRepository.deleteByIdAndHorseId(recordId, horseId);

        if (count == 0) {
            throw new ResourceNotFoundException("Record not found with id: " + recordId + " for horse with id: " + horseId");
        }

        // Si borra un registro, el caballo SIEMPRE vuelve a estar pendiente de verificación
        horse.updateStatusToPending();
    }
}

