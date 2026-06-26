package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.AuthRequest;
import com.humanitaire.backend.dto.AuthResponse;
import com.humanitaire.backend.dto.RegisterRequest;
import com.humanitaire.backend.entity.Role;
import com.humanitaire.backend.entity.User;
import com.humanitaire.backend.exception.BadRequestException;
import com.humanitaire.backend.repository.RoleRepository;
import com.humanitaire.backend.repository.UserRepository;
import com.humanitaire.backend.security.JwtTokenProvider;
import com.humanitaire.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(userPrincipal.getId());

        User user = userRepository.findById(userPrincipal.getId()).orElseThrow();
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }

        Role.RoleName roleName = Role.RoleName.ROLE_VOLUNTEER;
        if (request.getRole() != null) {
            try {
                roleName = Role.RoleName.valueOf(request.getRole());
            } catch (IllegalArgumentException ignored) {
            }
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Rôle non trouvé"));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .city(request.getCity())
                .roles(Collections.singleton(role))
                .enabled(true)
                .build();

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .build();
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Aucun compte associé à cet email"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        return token;
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new BadRequestException("Token invalide"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token expiré");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}
