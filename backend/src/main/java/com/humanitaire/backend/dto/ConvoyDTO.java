package com.humanitaire.backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConvoyDTO {
    private Long id;
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
    private String status;
    private String description;
    private String cargo;
    private Long missionId;
    private String missionTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
