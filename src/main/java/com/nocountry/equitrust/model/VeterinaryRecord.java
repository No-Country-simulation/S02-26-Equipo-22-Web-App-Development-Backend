package com.nocountry.equitrust.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table (name = "veterinary_records")
@Getter @Setter
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
    
    @Column(nullable = false)
    private boolean verified = false;

    @ManyToOne
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    //en caso de usar la entidad veterinaria
    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private User veterinarian;

}
