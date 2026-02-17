package com.nocountry.equitrust.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    private String name;
    private String last_name;
    private String phoneNumber; //podria no usarse
    private String address; //podria no usarse

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<Horse> horsesForSale = new HashSet<Horse>();

    public User(String email, String password, String name, String last_name, String number, String adress, UserRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.last_name = last_name;
        this.phoneNumber = number;
        this.address = adress;
        this.role = role;
    }
}
