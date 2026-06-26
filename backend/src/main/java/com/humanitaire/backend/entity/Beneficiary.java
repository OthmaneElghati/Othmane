package com.humanitaire.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiaries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String fullName;

    private Integer familySize;

    @Enumerated(EnumType.STRING)
    private EmergencyLevel emergencyLevel;

    private String address;
    private String city;
    private String region;
    private String phone;
    private String needs;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

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

    public enum EmergencyLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
