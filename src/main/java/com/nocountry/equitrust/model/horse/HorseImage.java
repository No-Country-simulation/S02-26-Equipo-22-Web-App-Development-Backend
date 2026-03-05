package com.nocountry.equitrust.model.horse;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "horse_images")
public class HorseImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false)
    private String publicId;

    private boolean mainImage = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_post_id", nullable = false)
    private HorsePost horsePost;

    public HorseImage(String publicId, HorsePost horsePost) {
        this.publicId = publicId;
        this.horsePost = horsePost;
    }
}