package com.humanitaire.backend.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VolunteerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String region;
    private String skills;
    private boolean available;
    private String avatar;
    private Integer missionCount;
}
