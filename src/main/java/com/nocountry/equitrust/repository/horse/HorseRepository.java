package com.nocountry.equitrust.repository.horse;

import com.nocountry.equitrust.model.horse.Horse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HorseRepository extends JpaRepository<Horse, Long>, JpaSpecificationExecutor<Horse> {
}
