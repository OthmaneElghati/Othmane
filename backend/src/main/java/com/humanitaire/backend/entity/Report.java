package com.humanitaire.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private ReportType type;

    private String generatedBy;
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ReportType {
        MISSION, DONATION, VOLUNTEER, MONTHLY
    }
}
