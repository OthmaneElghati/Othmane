package com.humanitaire.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "convoys")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Convoy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String departureCity;
    private String destinationCity;

    private Double currentLatitude;
    private Double currentLongitude;

    private Double departureLatitude;
    private Double departureLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;

    private LocalDateTime estimatedArrival;
    private LocalDateTime departureTime;

    @Enumerated(EnumType.STRING)
    private ConvoyStatus status;

    private String description;
    private String cargo;

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

    public enum ConvoyStatus {
        PENDING, IN_TRANSIT, DELIVERED, DELAYED
    }
}
