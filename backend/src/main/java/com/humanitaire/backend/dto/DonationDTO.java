package com.humanitaire.backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DonationDTO {
    private Long id;
    private String donorName;
    private String donorEmail;
    private Double amount;
    private String currency;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private String description;
    private Long missionId;
    private String missionTitle;
    private Long donorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
