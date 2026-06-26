package com.humanitaire.backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BeneficiaryDTO {
    private Long id;
    private String fullName;
    private Integer familySize;
    private String emergencyLevel;
    private String address;
    private String city;
    private String region;
    private String phone;
    private String needs;
    private Long missionId;
    private String missionTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
