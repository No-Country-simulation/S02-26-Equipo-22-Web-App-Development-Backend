package com.nocountry.equitrust.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "veterinary_records")
@Getter
@Setter
@NoArgsConstructor
public class VeterinaryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String pdfLink;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String clinicAddress;

    @Column(nullable = false)
    private String veterinarianLicense;

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
