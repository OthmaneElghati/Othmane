package com.humanitaire.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "volunteers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @Column(unique = true)
    private String email;

    private String phone;
    private String address;
    private String city;
    private String region;
    private String skills;
    private boolean available = true;
    private String avatar;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "volunteers")
    @Builder.Default
    private Set<Mission> missions = new HashSet<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
