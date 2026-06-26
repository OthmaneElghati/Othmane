package com.humanitaire.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "missions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;
    private Double budget;

    @Enumerated(EnumType.STRING)
    private MissionStatus status;

    @Enumerated(EnumType.STRING)
    private MissionPriority priority;

    private Double latitude;
    private Double longitude;
    private String city;
    private String region;
    private String image;

    private Integer priorityScore;

    @ManyToMany
    @JoinTable(name = "mission_volunteers",
            joinColumns = @JoinColumn(name = "mission_id"),
            inverseJoinColumns = @JoinColumn(name = "volunteer_id"))
    @Builder.Default
    private Set<Volunteer> volunteers = new HashSet<>();

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Beneficiary> beneficiaries = new HashSet<>();

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

    public enum MissionStatus {
        PLANNED, ACTIVE, COMPLETED, CANCELLED
    }

    public enum MissionPriority {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
