package com.nocountry.equitrust.service;

import com.nocountry.equitrust.dto.VeterinaryRecordRequestDTO;
import com.nocountry.equitrust.dto.VeterinaryRecordResponseDTO;
import com.nocountry.equitrust.model.Horse;
import com.nocountry.equitrust.model.VeterinaryRecord;
import com.nocountry.equitrust.repository.HorseRepository;
import com.nocountry.equitrust.repository.VeterinaryRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class VeterinaryRecordService {

    private final VeterinaryRecordRepository recordRepository;
    private final HorseRepository horseRepository;

    public VeterinaryRecordService(
            VeterinaryRecordRepository recordRepository,
            HorseRepository horseRepository) {
        this.recordRepository = recordRepository;
        this.horseRepository = horseRepository;
    }

    public VeterinaryRecordResponseDTO createRecord(
            Long horseId,
            VeterinaryRecordRequestDTO dto) {

        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() -> new RuntimeException("Horse not found"));

        VeterinaryRecord record = new VeterinaryRecord();
        record.setDescription(dto.description());
        record.setPdfLink(dto.pdfLink());
        record.setDate(dto.date());
        record.setClinicAddress(dto.clinicAddress());
        record.setVeterinarianLicense(dto.veterinarianLicense());
        record.setHorse(horse);
        record.setVerified(false);

        VeterinaryRecord savedRecord = recordRepository.save(record);

        return new VeterinaryRecordResponseDTO(
                savedRecord.getId(),
                savedRecord.getDescription(),
                savedRecord.getPdfLink(),
                savedRecord.getDate(),
                savedRecord.getClinicAddress(),
                savedRecord.getVeterinarianLicense(),
                savedRecord.isVerified(),
                horse.getId()
        );
    }


}
