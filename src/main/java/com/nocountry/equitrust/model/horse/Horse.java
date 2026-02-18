package com.nocountry.equitrust.model.horse;

import com.nocountry.equitrust.model.User;
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

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean sold = false;
    private String location;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus status = VerificationStatus.PENDING_DATA;

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

    public Horse(String breed, String description, String location, BigDecimal price, User owner) {
        this.breed = breed;
        this.description = description;
        this.location = location;
        this.price = price;
        this.owner = owner;
    }
}