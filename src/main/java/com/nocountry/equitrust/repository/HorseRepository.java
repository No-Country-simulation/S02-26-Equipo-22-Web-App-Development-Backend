package com.nocountry.equitrust.repository;

import com.nocountry.equitrust.model.horse.Horse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorseRepository extends JpaRepository<Horse, Long> {
}
