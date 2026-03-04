package com.nocountry.equitrust.service;

import com.nocountry.equitrust.controller.dto.horse.CreateHorseDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseFilterRequest;
import com.nocountry.equitrust.controller.dto.horse.HorseResponseDTO;
import com.nocountry.equitrust.controller.dto.horse.UpdateHorseDTO;
import com.nocountry.equitrust.model.horse.Discipline;
import com.nocountry.equitrust.model.horse.Gender;
import com.nocountry.equitrust.model.horse.HorsePost;
import com.nocountry.equitrust.model.horse.Temperament;
import com.nocountry.equitrust.model.user.User;
import com.nocountry.equitrust.model.user.Role;
import com.nocountry.equitrust.repository.horse.HorseRepository;
import com.nocountry.equitrust.repository.UserRepository;
import com.nocountry.equitrust.service.interfaces.HorseService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"JWT_SECRET=test-secret"})
@ActiveProfiles("test")
@DisplayName("HorseService Integration Tests")
class HorseServiceIntegrationTest {

    @Autowired
    private HorseService horseService;

    @Autowired
    private HorseRepository horseRepository;

    @Autowired
    private UserRepository userRepository;

    private User seller;
    private HorsePost defaultHorse;

    @BeforeEach
    void setUp() {
        seller = User.builder()
                .dni("12345678")
                .name("Seller")
                .lastName("One")
                .email("seller@test.com")
                .password("pass")
                .role(Role.USER)
                .build();
        userRepository.save(seller);

        CreateHorseDTO dto = new CreateHorseDTO(
                "Beautiful Arabian",
                "Arabian",
                5,
                Gender.STALLION,
                Temperament.CALM,
                Discipline.DRESSAGE,
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(45000),
                "Buenos Aires",
                "Excellent horse",
                List.of("img1"),
                "https://youtube.com/watch?v=test"
        );

        HorseResponseDTO resp = horseService.createHorse(dto, seller);
        defaultHorse = horseRepository.findById(resp.id()).orElseThrow();
    }

    @AfterEach
    void tearDown() {
        horseRepository.hardDeleteAll();
        userRepository.hardDeleteAll();
    }

    @Nested
    @DisplayName("Create Tests")
    class CreateTests {

        @Test
        @DisplayName("Should create horse post successfully")
        void createSuccess() {
            assertNotNull(defaultHorse.getId());
            assertEquals("Beautiful Arabian", defaultHorse.getTitle());
            assertEquals("Arabian", defaultHorse.getBreed());
            assertEquals("Buenos Aires", defaultHorse.getLocation());
        }

        @Test
        @DisplayName("Should fail when price is negative")
        void createInvalidPrice() {
            CreateHorseDTO dto = new CreateHorseDTO(
                    "Invalid Price", "Arabian", 4,
                    Gender.MARE, Temperament.CALM, Discipline.DRESSAGE,
                    BigDecimal.valueOf(-100), null,
                    "Cordoba", "Invalid", List.of("img"), null
            );
            assertThrows(IllegalArgumentException.class, () -> horseService.createHorse(dto, seller));
        }

        @Test
        @DisplayName("Should fail when price is zero")
        void createZeroPrice() {
            CreateHorseDTO dto = new CreateHorseDTO(
                    "Zero Price", "Arabian", 4,
                    Gender.MARE, Temperament.CALM, Discipline.DRESSAGE,
                    BigDecimal.ZERO, null,
                    "Cordoba", "Invalid", List.of("img"), null
            );
            assertThrows(IllegalArgumentException.class, () -> horseService.createHorse(dto, seller));
        }
    }

    @Nested
    @DisplayName("Read & Filter Tests")
    class ReadFilterTests {

        @Test
        @DisplayName("Should retrieve horse by id")
        @Transactional // images es lazy — requiere sesión abierta para inicializar la colección
        void getById() {
            HorseResponseDTO found = horseService.getHorseById(defaultHorse.getId());
            assertEquals(defaultHorse.getId(), found.id());
            assertEquals("Beautiful Arabian", found.title());
        }

        @Test
        @DisplayName("Should fail when horse not found")
        void getByIdNotFound() {
            assertThrows(RuntimeException.class, () -> horseService.getHorseById(999L));
        }

        @Test
        @DisplayName("Should filter by multiple breeds and locations")
        @Transactional // images es lazy — el DTO mapea imágenes de cada resultado
        void filterMultipleBreedsLocations() {
            CreateHorseDTO h2 = new CreateHorseDTO(
                    "Thoroughbred Horse", "Thoroughbred", 6,
                    Gender.MARE, Temperament.ENERGIC, Discipline.SHOW_JUMPING,
                    BigDecimal.valueOf(80000), null,
                    "Córdoba", "Jump champion", List.of("img2"), null
            );
            horseService.createHorse(h2, seller);

            HorseFilterRequest filter = new HorseFilterRequest();
            filter.setBreeds(List.of("Arabian", "Thoroughbred"));
            filter.setLocations(List.of("Buenos Aires", "Córdoba"));

            Page<HorseResponseDTO> page = horseService.getHorses(filter, PageRequest.of(0, 10));
            assertEquals(2, page.getTotalElements());
        }

        @Test
        @DisplayName("Should search by title full text")
        @Transactional // images es lazy — el DTO mapea imágenes de cada resultado
        void searchFullText() {
            HorseFilterRequest filter = new HorseFilterRequest();
            filter.setSearch("Beautiful");

            Page<HorseResponseDTO> page = horseService.getHorses(filter, PageRequest.of(0, 10));
            assertTrue(page.getTotalElements() >= 1);
            assertTrue(page.getContent().stream()
                    .anyMatch(h -> h.title().contains("Beautiful")));
        }

        @Test
        @DisplayName("Should filter by price range")
        @Transactional // images es lazy — el DTO mapea imágenes de cada resultado
        void filterByPriceRange() {
            HorseFilterRequest filter = new HorseFilterRequest();
            filter.setMinPrice(BigDecimal.valueOf(40000));
            filter.setMaxPrice(BigDecimal.valueOf(60000));

            Page<HorseResponseDTO> page = horseService.getHorses(filter, PageRequest.of(0, 10));
            assertTrue(page.getTotalElements() >= 1);
            assertTrue(page.getContent().stream()
                    .allMatch(h -> h.price().compareTo(BigDecimal.valueOf(40000)) >= 0
                            && h.price().compareTo(BigDecimal.valueOf(60000)) <= 0));
        }

        @Test
        @DisplayName("Should filter by age range")
        @Transactional // images es lazy — el DTO mapea imágenes de cada resultado
        void filterByAgeRange() {
            HorseFilterRequest filter = new HorseFilterRequest();
            filter.setMinAge(3);
            filter.setMaxAge(7);

            Page<HorseResponseDTO> page = horseService.getHorses(filter, PageRequest.of(0, 10));
            assertTrue(page.getTotalElements() >= 1);
            assertTrue(page.getContent().stream()
                    .allMatch(h -> h.age() >= 3 && h.age() <= 7));
        }

        @Test
        @DisplayName("Should filter by gender")
        @Transactional // images es lazy — el DTO mapea imágenes de cada resultado
        void filterByGender() {
            HorseFilterRequest filter = new HorseFilterRequest();
            filter.setGender(Gender.STALLION);

            Page<HorseResponseDTO> page = horseService.getHorses(filter, PageRequest.of(0, 10));
            assertTrue(page.getTotalElements() >= 1);
            assertTrue(page.getContent().stream()
                    .allMatch(h -> h.gender() == Gender.STALLION));
        }

        @Test
        @DisplayName("Should return empty page when no horses match filter")
        void filterNoResults() {
            HorseFilterRequest filter = new HorseFilterRequest();
            filter.setBreeds(List.of("NonExistentBreed"));

            Page<HorseResponseDTO> page = horseService.getHorses(filter, PageRequest.of(0, 10));
            assertEquals(0, page.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Update & Delete Tests")
    class UpdateDeleteTests {

        @Test
        @DisplayName("Should update horse post if owner")
        @Transactional // images es lazy — el DTO resultante mapea imágenes
        void updateSuccess() {
            UpdateHorseDTO dto = new UpdateHorseDTO(
                    "Updated Title", "Thoroughbred",
                    null, null, null, null,
                    null, null, null, null,
                    null, null
            );

            HorseResponseDTO updated = horseService.updateHorse(defaultHorse.getId(), dto, seller);
            assertEquals("Updated Title", updated.title());
            assertEquals("Thoroughbred", updated.breed());
        }

        @Test
        @DisplayName("Should not update horse if not owner")
        void updateNotOwner() {
            User other = User.builder()
                    .dni("99999999")
                    .name("Other").lastName("User")
                    .email("other@test.com")
                    .password("pass").role(Role.USER)
                    .build();
            userRepository.save(other);

            UpdateHorseDTO dto = new UpdateHorseDTO(
                    "Hacked Title", null, null, null, null, null,
                    null, null, null, null, null, null
            );

            assertThrows(RuntimeException.class,
                    () -> horseService.updateHorse(defaultHorse.getId(), dto, other));
        }

        @Test
        @DisplayName("Should delete horse post if owner")
        void deleteSuccess() {
            assertDoesNotThrow(() -> horseService.deleteHorse(defaultHorse.getId(), seller));
            assertThrows(RuntimeException.class,
                    () -> horseService.getHorseById(defaultHorse.getId()));
        }

        @Test
        @DisplayName("Should not delete horse if not owner")
        void deleteNotOwner() {
            User other = User.builder()
                    .dni("99999999")
                    .name("Other").lastName("User")
                    .email("other@test.com")
                    .password("pass").role(Role.USER)
                    .build();
            userRepository.save(other);

            assertThrows(RuntimeException.class,
                    () -> horseService.deleteHorse(defaultHorse.getId(), other));
        }
    }
}