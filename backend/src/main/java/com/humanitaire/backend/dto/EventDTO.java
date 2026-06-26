package com.humanitaire.backend.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate eventDate;
    private String location;
    private String city;
    private String region;
    private String organizer;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String image;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
