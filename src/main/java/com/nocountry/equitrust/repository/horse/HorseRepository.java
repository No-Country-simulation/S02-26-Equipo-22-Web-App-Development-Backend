package com.nocountry.equitrust.repository.horse;

import com.nocountry.equitrust.model.horse.HorsePost;
import com.nocountry.equitrust.model.horse.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface HorseRepository extends JpaRepository<HorsePost, Long>, JpaSpecificationExecutor<HorsePost> {
    // Metodo para testear a eliminación física de caballos, se recomienda usar solo en pruebas unitarias o de integración
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE horse_posts CASCADE", nativeQuery = true)
    void hardDeleteAll();

    // Find horses by verification status with pagination
    Page<HorsePost> findByStatus(VerificationStatus status, Pageable pageable);

    // Count horses by verification status
    long countByStatus(VerificationStatus status);
}
