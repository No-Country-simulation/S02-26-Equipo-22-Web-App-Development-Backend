package com.nocountry.equitrust.repository;

import com.nocountry.equitrust.model.horse.Horse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorseRepository extends JpaRepository<Horse, Long> {
}
