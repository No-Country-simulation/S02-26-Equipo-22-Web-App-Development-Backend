package com.nocountry.equitrust.repository;

import com.nocountry.equitrust.model.horse.HorseMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorseMediaRepository extends JpaRepository<HorseMedia, Long> {

    List<HorseMedia> findByHorseId(Long horseId);
}
