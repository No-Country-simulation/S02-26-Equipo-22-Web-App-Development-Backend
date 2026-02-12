package com.nocountry.equitrust.repository;

import com.nocountry.equitrust.model.VeterinaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeterinaryRecordRepository
        extends JpaRepository<VeterinaryRecord, Long> {

    List<VeterinaryRecord> findByHorseId(Long horseId);
}
