package com.nocountry.equitrust.model.horse;

import com.nocountry.equitrust.model.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Temperament temperament;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Discipline discipline;

    @Setter(AccessLevel.NONE)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Setter(AccessLevel.NONE)
    @Column(name = "discount_price", precision = 15, scale = 2)
    private BigDecimal discountPrice;

    private boolean sold = false;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus status = VerificationStatus.PENDING_DATA;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "video_id")
    private String videoId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "horse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorseImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "horse", cascade = CascadeType.ALL)
    private List<VeterinaryRecord> records = new ArrayList<>();


    public Horse(String breed, Integer age, Temperament temperament, Gender gender, Discipline discipline, BigDecimal price, BigDecimal discountPrice, String location, String description, List<String> imagePublicIds,
                 String videoId, User owner) {
        validatePrice(price, discountPrice);
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.temperament = temperament;
        this.discipline = discipline;
        this.price = price;
        this.location = location;
        this.description = description;
        this.owner = owner;
        this.videoId = videoId;
        imagePublicIds.forEach(this::addImage);
    }


    private void validatePrice(BigDecimal price, BigDecimal discountPrice) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (discountPrice != null) {
            if (discountPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Discount price must be greater than 0");
            }
            if (discountPrice.compareTo(price) >= 0) {
                throw new IllegalArgumentException(
                        "Discount price must be lower than the base price"
                );
            }
        }
    }

    public void changeBasePrice(BigDecimal newPrice) {
        validatePrice(newPrice, this.discountPrice);
        this.price = newPrice;
    }

    public void changeDiscountPrice(BigDecimal discountPrice) {
        validatePrice(this.price, discountPrice);
        this.discountPrice = discountPrice;
    }

    public void removeDiscount() {
        this.discountPrice = null;
    }

    public void updateStatusToPending(){
        this.status = VerificationStatus.PENDING_VERIFICATION;
    }

    public void addImage(String publicId) {
        this.validateImage(publicId);
        HorseImage image = new HorseImage(publicId, this);
        this.images.add(image);
    }

    private void validateImage(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException("Image public ID cannot be empty");
        }
    }

    public void replaceImages(List<String> publicIds) {
        this.images.clear();
        publicIds.forEach(this::addImage);
    }

    public boolean isOwnedBy(User user) {
        return this.owner.getId().equals(user.getId());
    }
}