package com.nocountry.equitrust.service;

import com.nocountry.equitrust.dto.request.CreateVeterinaryRecordDTO;
import com.nocountry.equitrust.dto.request.UpdateVeterinaryRecordDTO;
import com.nocountry.equitrust.dto.response.VeterinaryRecordResponseDTO;
import com.nocountry.equitrust.model.Horse;
import com.nocountry.equitrust.model.VeterinaryRecord;
import com.nocountry.equitrust.repository.HorseRepository;
import com.nocountry.equitrust.repository.VeterinaryRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.nocountry.equitrust.exception.VeterinaryRecordNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinaryRecordService {

    private final VeterinaryRecordRepository recordRepository;
    private final HorseRepository horseRepository;

    /**
     * Crea un registro veterinario asociado a un caballo.
     */
    public VeterinaryRecordResponseDTO createVeterinaryRecordForHorse(
            Long horseId,
            CreateVeterinaryRecordDTO dto) {
        //exeption de caballos
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("Caballo no encontrado"));

        VeterinaryRecord record = dto.toModel(horse);

        VeterinaryRecord saved = recordRepository.save(record);

        return VeterinaryRecordResponseDTO.fromModel(saved);
    }

    /**
     * Obtiene todos los registros veterinarios de un caballo.
     */
    public List<VeterinaryRecordResponseDTO> getAllRecordsByHorse(Long horseId) {
            //exeption de caballos
        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("Caballo no encontrado"));

        return recordRepository.findByHorse(horse)
                .stream()
                .map(VeterinaryRecordResponseDTO::fromModel)
                .toList();
    }

    /**
     * Obtiene un registro veterinario específico.
     */
    public VeterinaryRecordResponseDTO getRecordById(Long horseId, Long recordId) {
        VeterinaryRecord record = recordRepository
                .findByIdAndHorseId(recordId, horseId)
                .orElseThrow(() -> new VeterinaryRecordNotFoundException(recordId));


        if (!record.getHorse().getId().equals(horse.getId())) {
            throw new VeterinaryRecordNotFoundException(recordId);
        }

        return VeterinaryRecordResponseDTO.fromModel(record);
    }

    /**
     * Elimina un registro veterinario.
     */
    public void deleteRecord(Long horseId, Long recordId) {

        VeterinaryRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new VeterinaryRecordNotFoundException(recordId));

        if (!record.getHorse().getId().equals(horseId)) {
            throw new VeterinaryRecordNotFoundException(recordId);
        }

        recordRepository.delete(record);
    }

    /**
     * Actualiza un registro veterinario existente.
     */
    public VeterinaryRecordResponseDTO updateRecord(
            Long horseId,
            Long recordId,
            UpdateVeterinaryRecordDTO dto) {

        VeterinaryRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new VeterinaryRecordNotFoundException(recordId));

        if (!record.getHorse().getId().equals(horseId)) {
            throw new VeterinaryRecordNotFoundException(recordId);
        }

        record.setDescription(dto.description());
        record.setPdfLink(dto.pdfLink());
        record.setDate(dto.date());
        record.setClinicAddress(dto.clinicAddress());
        record.setVeterinarianLicense(dto.veterinarianLicense());

        VeterinaryRecord updated = recordRepository.save(record);

        return VeterinaryRecordResponseDTO.fromModel(updated);
    }
}

