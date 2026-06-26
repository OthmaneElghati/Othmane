package com.humanitaire.backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportDTO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private String generatedBy;
    private String filePath;
    private Long missionId;
    private String missionTitle;
    private LocalDateTime createdAt;
}
