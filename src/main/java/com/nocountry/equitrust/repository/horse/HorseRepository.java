package com.nocountry.equitrust.repository.horse;

import com.nocountry.equitrust.model.horse.HorsePost;
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
}
