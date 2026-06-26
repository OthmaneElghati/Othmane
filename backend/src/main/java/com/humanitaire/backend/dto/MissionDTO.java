package com.humanitaire.backend.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MissionDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double budget;
    private String status;
    private String priority;
    private Double latitude;
    private Double longitude;
    private String city;
    private String region;
    private String image;
    private Integer priorityScore;
    private Integer volunteerCount;
    private Integer beneficiaryCount;
    private Set<Long> volunteerIds;
}
