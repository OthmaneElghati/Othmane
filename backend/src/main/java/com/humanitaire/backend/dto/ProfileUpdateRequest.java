package com.humanitaire.backend.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String city;
    private String region;
    private String currentPassword;
    private String newPassword;
}
