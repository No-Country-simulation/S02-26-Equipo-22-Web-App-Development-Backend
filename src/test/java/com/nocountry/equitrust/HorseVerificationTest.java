package com.nocountry.equitrust;

import com.nocountry.equitrust.controller.dto.horse.CreateHorseDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseDetailDTO;
import com.nocountry.equitrust.controller.dto.horse.HorseResponseDTO;
import com.nocountry.equitrust.model.horse.Discipline;
import com.nocountry.equitrust.model.horse.Gender;
import com.nocountry.equitrust.model.horse.HorsePost;
import com.nocountry.equitrust.model.horse.Temperament;
import com.nocountry.equitrust.model.horse.VerificationStatus;
import com.nocountry.equitrust.model.horse.VeterinaryRecord;
import com.nocountry.equitrust.model.user.Role;
import com.nocountry.equitrust.model.user.User;
import com.nocountry.equitrust.repository.horse.HorseRepository;
import com.nocountry.equitrust.repository.UserRepository;
import com.nocountry.equitrust.repository.VeterinaryRecordRepository;
import com.nocountry.equitrust.service.impl.AdminHorseService;
import com.nocountry.equitrust.service.interfaces.HorseService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"JWT_SECRET=test-secret"})
@ActiveProfiles("test")
@DisplayName("Horse Verification System Integration Tests")
class HorseVerificationTest {

    @Autowired private HorseService horseService;
    @Autowired private AdminHorseService adminHorseService;
    @Autowired private HorseRepository horseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private VeterinaryRecordRepository vetRecordRepository;
    @Autowired private TransactionTemplate txTemplate;

    private User seller;
    private Long horseWithVetAndVideoId;
    private Long horseWithoutVetId;
    private Long horseWithoutVideoId;

    private Long createHorseWithVetAndVideo(String title, String breed, String location,
                                            String videoUrl, String vetLicense) {
        CreateHorseDTO dto = new CreateHorseDTO(
                title, breed, 5, Gender.STALLION, Temperament.CALM, Discipline.DRESSAGE,
                BigDecimal.valueOf(50000), null, location, "Description",
                List.of("img-" + vetLicense), videoUrl
        );
        HorseResponseDTO resp = horseService.createHorse(dto, seller);
        return txTemplate.execute(status -> {
            HorsePost horse = horseRepository.findById(resp.id()).orElseThrow();
            vetRecordRepository.save(new VeterinaryRecord(
                    "Health check", "https://example.com/" + vetLicense + ".pdf",
                    LocalDate.now(), "Clinic Test", vetLicense, horse
            ));
            return horse.getId();
        });
    }

    @BeforeEach
    void setUp() {
        seller = txTemplate.execute(status ->
                userRepository.save(User.builder()
                        .dni("12345678").name("John").lastName("Seller")
                        .email("seller@test.com").password("pass").role(Role.USER)
                        .build())
        );

        horseWithVetAndVideoId = txTemplate.execute(status -> {
            CreateHorseDTO dto = new CreateHorseDTO(
                    "Arabian Ready for Verification", "Arabian", 5,
                    Gender.STALLION, Temperament.CALM, Discipline.DRESSAGE,
                    BigDecimal.valueOf(50000), BigDecimal.valueOf(45000),
                    "Buenos Aires", "Perfect for verification",
                    List.of("img1", "img2"), "https://youtube.com/watch?v=video1"
            );
            HorseResponseDTO resp = horseService.createHorse(dto, seller);
            HorsePost horse = horseRepository.findById(resp.id()).orElseThrow();
            vetRecordRepository.save(new VeterinaryRecord(
                    "Health assessment", "https://example.com/vet1.pdf",
                    LocalDate.now().minusDays(10), "Clinic A, Buenos Aires", "VET-123456",
                    horse
            ));
            return horse.getId();
        });

        horseWithoutVetId = txTemplate.execute(status -> {
            CreateHorseDTO dto = new CreateHorseDTO(
                    "Thoroughbred Without Vet", "Thoroughbred", 6,
                    Gender.MARE, Temperament.ENERGIC, Discipline.SHOW_JUMPING,
                    BigDecimal.valueOf(60000), null,
                    "Córdoba", "No vet records yet",
                    List.of("img3"), "https://youtube.com/watch?v=video2"
            );
            return horseService.createHorse(dto, seller).id();
        });

        horseWithoutVideoId = txTemplate.execute(status -> {
            CreateHorseDTO dto = new CreateHorseDTO(
                    "Quarter Horse Without Video", "Quarter Horse", 4,
                    Gender.STALLION, Temperament.CALM, Discipline.ENDURANCE,
                    BigDecimal.valueOf(40000), null,
                    "Mendoza", "Missing performance video",
                    List.of("img4"), null
            );
            HorseResponseDTO resp = horseService.createHorse(dto, seller);
            HorsePost horse = horseRepository.findById(resp.id()).orElseThrow();
            vetRecordRepository.save(new VeterinaryRecord(
                    "General health check", "https://example.com/vet2.pdf",
                    LocalDate.now().minusDays(5), "Clinic B, Mendoza", "VET-654321",
                    horse
            ));
            return horse.getId();
        });
    }

    @AfterEach
    void tearDown() {
        vetRecordRepository.deleteAll();
        horseRepository.hardDeleteAll();
        userRepository.hardDeleteAll();
    }

    // ==================== REQUEST VERIFICATION ====================

    @Nested
    @DisplayName("Request Verification - Favorable Cases")
    class RequestVerificationFavorable {

        @Test
        @DisplayName("Should successfully request verification with vet records and video")
        void requestVerificationSuccess() {
            HorseResponseDTO response = horseService.requestVerification(horseWithVetAndVideoId, seller);

            assertEquals(VerificationStatus.PENDING_VERIFICATION, response.status());
            assertNull(response.rejectionReason());
            assertEquals(horseWithVetAndVideoId, response.id());
        }

        @Test
        @DisplayName("Should request verification again after rejection")
        void requestVerificationAfterRejection() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);
            adminHorseService.rejectHorse(horseWithVetAndVideoId, "Missing documentation");

            HorseResponseDTO rejected = horseService.getHorseById(horseWithVetAndVideoId);
            assertEquals(VerificationStatus.REJECTED, rejected.status());
            assertEquals("Missing documentation", rejected.rejectionReason());

            HorseResponseDTO resubmitted = horseService.requestVerification(horseWithVetAndVideoId, seller);
            assertEquals(VerificationStatus.PENDING_VERIFICATION, resubmitted.status());
            assertNull(resubmitted.rejectionReason());
        }
    }

    @Nested
    @DisplayName("Request Verification - Unfavorable Cases")
    class RequestVerificationUnfavorable {

        @Test
        @DisplayName("Should fail when requesting verification without veterinary records")
        void requestVerificationNoVetRecords() {
            assertThrows(IllegalStateException.class, () ->
                    horseService.requestVerification(horseWithoutVetId, seller));
        }

        @Test
        @DisplayName("Should fail when requesting verification without performance video")
        void requestVerificationNoVideo() {
            assertThrows(IllegalStateException.class, () ->
                    horseService.requestVerification(horseWithoutVideoId, seller));
        }

        @Test
        @DisplayName("Should fail when non-owner requests verification")
        void requestVerificationNotOwner() {
            User otherUser = txTemplate.execute(status ->
                    userRepository.save(User.builder()
                            .dni("11111111").name("Other").lastName("User")
                            .email("other@test.com").password("pass").role(Role.USER)
                            .build())
            );

            assertThrows(AccessDeniedException.class, () ->
                    horseService.requestVerification(horseWithVetAndVideoId, otherUser));
        }

        @Test
        @DisplayName("Should fail when horse is already verified")
        void requestVerificationAlreadyVerified() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);
            adminHorseService.approveHorse(horseWithVetAndVideoId);

            assertThrows(IllegalStateException.class, () ->
                    horseService.requestVerification(horseWithVetAndVideoId, seller));
        }

        @Test
        @DisplayName("Should fail when horse is already under verification")
        void requestVerificationAlreadyPending() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);

            assertThrows(IllegalStateException.class, () ->
                    horseService.requestVerification(horseWithVetAndVideoId, seller));
        }

        @Test
        @DisplayName("Should fail when horse has empty video URL")
        void requestVerificationEmptyVideoUrl() {
            txTemplate.execute(status -> {
                HorsePost horse = horseRepository.findById(horseWithVetAndVideoId).orElseThrow();
                horse.setVideoUrl("");
                return horseRepository.save(horse);
            });

            assertThrows(IllegalStateException.class, () ->
                    horseService.requestVerification(horseWithVetAndVideoId, seller));
        }

        @Test
        @DisplayName("Should fail when horse has blank video URL")
        void requestVerificationBlankVideoUrl() {
            txTemplate.execute(status -> {
                HorsePost horse = horseRepository.findById(horseWithVetAndVideoId).orElseThrow();
                horse.setVideoUrl("   ");
                return horseRepository.save(horse);
            });

            assertThrows(IllegalStateException.class, () ->
                    horseService.requestVerification(horseWithVetAndVideoId, seller));
        }
    }

    // ==================== APPROVE VERIFICATION ====================

    @Nested
    @DisplayName("Approve Verification - Favorable Cases")
    class ApproveVerificationFavorable {

        @Test
        @DisplayName("Should successfully approve pending horse verification")
        void approveSuccess() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);

            var response = adminHorseService.approveHorse(horseWithVetAndVideoId);

            assertEquals(VerificationStatus.VERIFIED, response.status());
            assertNull(response.rejectionReason());
            assertEquals(horseWithVetAndVideoId, response.horseId());
        }

        @Test
        @DisplayName("Should persist VERIFIED status in database")
        void approvePersistence() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);
            adminHorseService.approveHorse(horseWithVetAndVideoId);

            HorsePost updated = horseRepository.findById(horseWithVetAndVideoId).orElseThrow();
            assertEquals(VerificationStatus.VERIFIED, updated.getStatus());
            assertNull(updated.getRejectionReason());
        }
    }

    @Nested
    @DisplayName("Approve Verification - Unfavorable Cases")
    class ApproveVerificationUnfavorable {

        @Test
        @DisplayName("Should fail when approving non-pending horse")
        void approveFalseStatus() {
            assertThrows(IllegalStateException.class, () ->
                    adminHorseService.approveHorse(horseWithVetAndVideoId));
        }

        @Test
        @DisplayName("Should fail when approving non-existent horse")
        void approveNonExistent() {
            assertThrows(RuntimeException.class, () ->
                    adminHorseService.approveHorse(999L));
        }

        @Test
        @DisplayName("Should clear rejection reason when approving after resubmission")
        void approveClearsRejectionReason() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);
            adminHorseService.rejectHorse(horseWithVetAndVideoId, "Missing documents");

            HorseResponseDTO rejected = horseService.getHorseById(horseWithVetAndVideoId);
            assertEquals("Missing documents", rejected.rejectionReason());

            horseService.requestVerification(horseWithVetAndVideoId, seller);
            var response = adminHorseService.approveHorse(horseWithVetAndVideoId);

            assertNull(response.rejectionReason());
        }
    }

    // ==================== REJECT VERIFICATION ====================

    @Nested
    @DisplayName("Reject Verification - Favorable Cases")
    class RejectVerificationFavorable {

        @Test
        @DisplayName("Should successfully reject pending horse with reason")
        void rejectSuccess() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);

            var response = adminHorseService.rejectHorse(horseWithVetAndVideoId, "Veterinary documents incomplete");

            assertEquals(VerificationStatus.REJECTED, response.status());
            assertEquals("Veterinary documents incomplete", response.rejectionReason());
        }

        @Test
        @DisplayName("Should persist rejection reason in database")
        void rejectPersistence() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);
            adminHorseService.rejectHorse(horseWithVetAndVideoId, "Missing signature");

            HorsePost updated = horseRepository.findById(horseWithVetAndVideoId).orElseThrow();
            assertEquals(VerificationStatus.REJECTED, updated.getStatus());
            assertEquals("Missing signature", updated.getRejectionReason());
        }

        @Test
        @DisplayName("Should allow vendor to see rejection reason via getHorseById")
        void rejectReasonVisible() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);
            adminHorseService.rejectHorse(horseWithVetAndVideoId, "Date on documents is outdated - please update");

            HorseResponseDTO response = horseService.getHorseById(horseWithVetAndVideoId);
            assertEquals("Date on documents is outdated - please update", response.rejectionReason());
        }
    }

    @Nested
    @DisplayName("Reject Verification - Unfavorable Cases")
    class RejectVerificationUnfavorable {

        @Test
        @DisplayName("Should fail when rejecting non-pending horse")
        void rejectWrongStatus() {
            assertThrows(IllegalStateException.class, () ->
                    adminHorseService.rejectHorse(horseWithVetAndVideoId, "Invalid"));
        }

        @Test
        @DisplayName("Should fail when rejecting with null reason")
        void rejectNullReason() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);
            assertThrows(IllegalArgumentException.class, () ->
                    adminHorseService.rejectHorse(horseWithVetAndVideoId, null));
        }

        @Test
        @DisplayName("Should fail when rejecting with blank reason")
        void rejectBlankReason() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);
            assertThrows(IllegalArgumentException.class, () ->
                    adminHorseService.rejectHorse(horseWithVetAndVideoId, "   "));
        }

        @Test
        @DisplayName("Should fail when rejecting with empty reason")
        void rejectEmptyReason() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);
            assertThrows(IllegalArgumentException.class, () ->
                    adminHorseService.rejectHorse(horseWithVetAndVideoId, ""));
        }

        @Test
        @DisplayName("Should fail when rejecting non-existent horse")
        void rejectNonExistent() {
            assertThrows(RuntimeException.class, () ->
                    adminHorseService.rejectHorse(999L, "Any reason"));
        }
    }

    // ==================== ADMIN PENDING LIST ====================

    @Nested
    @DisplayName("Admin Pending List Operations")
    class AdminPendingList {

        @Test
        @DisplayName("Should retrieve all pending horses for admin")
        void getPendingList() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);

            Page<HorseDetailDTO> pending = adminHorseService.getPendingHorses(PageRequest.of(0, 10));

            assertEquals(1, pending.getTotalElements());
            assertEquals(horseWithVetAndVideoId, pending.getContent().get(0).id());
        }

        @Test
        @DisplayName("Should return empty list when no pending horses")
        void getPendingListEmpty() {
            Page<HorseDetailDTO> pending = adminHorseService.getPendingHorses(PageRequest.of(0, 10));
            assertEquals(0, pending.getTotalElements());
        }

        @Test
        @DisplayName("Should paginate pending horses correctly")
        void getPendingListPagination() {
            for (int i = 0; i < 5; i++) {
                Long horseId = createHorseWithVetAndVideo(
                        "Horse " + i, "Breed" + i, "Location" + i,
                        "https://youtube.com/watch?v=" + i, "VET-PAG-" + i
                );
                horseService.requestVerification(horseId, seller);
            }

            Page<HorseDetailDTO> page1 = adminHorseService.getPendingHorses(PageRequest.of(0, 3));
            assertEquals(3, page1.getContent().size());
            assertEquals(5, page1.getTotalElements());
            assertEquals(2, page1.getTotalPages());

            Page<HorseDetailDTO> page2 = adminHorseService.getPendingHorses(PageRequest.of(1, 3));
            assertEquals(2, page2.getContent().size());
        }

        @Test
        @DisplayName("Should get count of pending verifications")
        void getPendingCount() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);

            long count = adminHorseService.getPendingCount();
            assertEquals(1, count);
        }

        @Test
        @DisplayName("Should not include approved horses in pending list")
        void pendingListExcludesApproved() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);
            adminHorseService.approveHorse(horseWithVetAndVideoId);

            Page<HorseDetailDTO> pending = adminHorseService.getPendingHorses(PageRequest.of(0, 10));
            assertEquals(0, pending.getTotalElements());
        }

        @Test
        @DisplayName("Should not include rejected horses in pending list")
        void pendingListExcludesRejected() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);
            adminHorseService.rejectHorse(horseWithVetAndVideoId, "Rejected");

            Page<HorseDetailDTO> pending = adminHorseService.getPendingHorses(PageRequest.of(0, 10));
            assertEquals(0, pending.getTotalElements());
        }
    }

    // ==================== STATE TRANSITIONS ====================

    @Nested
    @DisplayName("State Transition Workflow")
    class StateTransitionWorkflow {

        @Test
        @DisplayName("Complete flow: Request → Approve")
        void completeApprovalFlow() {
            HorseResponseDTO initial = horseService.getHorseById(horseWithVetAndVideoId);
            assertEquals(VerificationStatus.PENDING_DATA, initial.status());

            HorseResponseDTO requested = horseService.requestVerification(horseWithVetAndVideoId, seller);
            assertEquals(VerificationStatus.PENDING_VERIFICATION, requested.status());

            var approved = adminHorseService.approveHorse(horseWithVetAndVideoId);
            assertEquals(VerificationStatus.VERIFIED, approved.status());

            HorseResponseDTO finalState = horseService.getHorseById(horseWithVetAndVideoId);
            assertEquals(VerificationStatus.VERIFIED, finalState.status());
            assertNull(finalState.rejectionReason());
        }

        @Test
        @DisplayName("Complete flow: Request → Reject → Request → Approve")
        void rejectResubmitApproveFlow() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);

            var rejected = adminHorseService.rejectHorse(horseWithVetAndVideoId, "Missing recent vet records");
            assertEquals(VerificationStatus.REJECTED, rejected.status());

            HorseResponseDTO afterReject = horseService.getHorseById(horseWithVetAndVideoId);
            assertEquals(VerificationStatus.REJECTED, afterReject.status());
            assertEquals("Missing recent vet records", afterReject.rejectionReason());

            HorseResponseDTO resubmitted = horseService.requestVerification(horseWithVetAndVideoId, seller);
            assertEquals(VerificationStatus.PENDING_VERIFICATION, resubmitted.status());
            assertNull(resubmitted.rejectionReason());

            var approved = adminHorseService.approveHorse(horseWithVetAndVideoId);
            assertEquals(VerificationStatus.VERIFIED, approved.status());
            assertNull(approved.rejectionReason());
        }

        @Test
        @DisplayName("Multiple horses with different statuses coexist correctly")
        void multipleHorseStatuses() {
            horseService.requestVerification(horseWithVetAndVideoId, seller);
            adminHorseService.approveHorse(horseWithVetAndVideoId);

            Long horse2Id = createHorseWithVetAndVideo(
                    "Horse 2", "Breed2", "Location2",
                    "https://youtube.com/video2", "VET-MULTI-2"
            );
            horseService.requestVerification(horse2Id, seller);
            adminHorseService.rejectHorse(horse2Id, "Issue");

            HorseResponseDTO h1 = horseService.getHorseById(horseWithVetAndVideoId);
            HorseResponseDTO h2 = horseService.getHorseById(horse2Id);

            assertEquals(VerificationStatus.VERIFIED, h1.status());
            assertEquals(VerificationStatus.REJECTED, h2.status());
            assertEquals("Issue", h2.rejectionReason());
        }
    }
}