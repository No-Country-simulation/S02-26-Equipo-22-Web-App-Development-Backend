package com.nocountry.equitrust.repository;

import com.nocountry.equitrust.model.horse.VeterinaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeterinaryRecordRepository
        extends JpaRepository<VeterinaryRecord, Long> {

    Optional<VeterinaryRecord> findByIdAndHorsePostId(Long recordId, Long horsePostId);

    List<VeterinaryRecord> findAllByHorsePostId(Long horsePostId);

    long deleteByIdAndHorsePostId(Long recordId, Long horsePostId);
}
