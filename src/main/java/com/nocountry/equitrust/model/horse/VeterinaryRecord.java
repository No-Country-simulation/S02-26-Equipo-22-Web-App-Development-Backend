package com.nocountry.equitrust.model.horse;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "veterinary_records")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class VeterinaryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "pdf_link", nullable = false)
    private String pdfLink;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "clinic_address", nullable = false)
    private String clinicAddress;

    @Column(name = "veterinarian_license", nullable = false)
    private String veterinarianLicense;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    public VeterinaryRecord(String description, String pdfLink, LocalDate date, String clinicAddress, String veterinarianLicense, Horse horse) {
        this.description = description;
        this.pdfLink = pdfLink;
        this.date = date;
        this.clinicAddress = clinicAddress;
        this.veterinarianLicense = veterinarianLicense;
        this.horse = horse;
    }
}
