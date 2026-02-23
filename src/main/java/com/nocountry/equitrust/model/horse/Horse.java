package com.nocountry.equitrust.model.horse;

import com.nocountry.equitrust.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "horses")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE horses SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
public class Horse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String breed;

    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Temperament temperament;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HorseType type;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "discount_price")
    private BigDecimal discountPrice;

    @Column(nullable = false)
    private boolean sold = false;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus status = VerificationStatus.PENDING_DATA;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "horse", cascade = CascadeType.ALL)
    private List<VeterinaryRecord> records = new ArrayList<>();

    public Horse(String breed, Integer age, Temperament temperament, HorseType type, BigDecimal price, String location, String description, User owner) {
        this.breed = breed;
        this.age = age;
        this.temperament = temperament;
        this.type = type;
        this.price = price;
        this.location = location;
        this.description = description;
        this.owner = owner;
    }

    public void updateStatusToPending(){
        this.status = VerificationStatus.PENDING_VERIFICATION;
    }
}