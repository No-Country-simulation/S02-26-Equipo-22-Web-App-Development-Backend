package com.nocountry.equitrust.repository;

import com.nocountry.equitrust.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByDni(String dni);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    // Metodo para testear a eliminación física de usuarios, se recomienda usar solo en pruebas unitarias o de integración
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE users CASCADE", nativeQuery = true)
    void hardDeleteAll();
}
