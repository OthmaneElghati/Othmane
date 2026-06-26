package com.humanitaire.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;

    public enum RoleName {
        ROLE_SUPER_ADMIN,
        ROLE_MISSION_MANAGER,
        ROLE_VOLUNTEER,
        ROLE_BENEFICIARY,
        ROLE_DONOR
    }
}
