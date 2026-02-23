package com.nocountry.equitrust.repository;

import com.nocountry.equitrust.model.horse.VeterinaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeterinaryRecordRepository
        extends JpaRepository<VeterinaryRecord, Long> {

    Optional<VeterinaryRecord> findByIdAndHorseId(Long recordId, Long horseId);

    List<VeterinaryRecord> findAllByHorseId(Long horseId);

    long deleteByIdAndHorseId(Long recordId, Long horseId);
}
