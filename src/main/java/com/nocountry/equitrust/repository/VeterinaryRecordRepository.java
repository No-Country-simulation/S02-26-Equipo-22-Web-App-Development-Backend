package com.nocountry.equitrust.repository;

import com.nocountry.equitrust.model.Horse;
import com.nocountry.equitrust.model.VeterinaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeterinaryRecordRepository
        extends JpaRepository<VeterinaryRecord, Long> {

    Optional<VeterinaryRecord> findByIdAndHorseId(Long recordId, Long horseId);

    List<VeterinaryRecord> findByHorseId(Long horseId);

    List<VeterinaryRecord> findByHorse(Horse horse);

}
