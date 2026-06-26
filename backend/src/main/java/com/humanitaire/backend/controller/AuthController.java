package com.humanitaire.backend.controller;

import com.humanitaire.backend.dto.AuthRequest;
import com.humanitaire.backend.dto.AuthResponse;
import com.humanitaire.backend.dto.RegisterRequest;
import com.humanitaire.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String token = authService.forgotPassword(request.get("email"));
        return ResponseEntity.ok(Map.of("message", "Un email de réinitialisation a été envoyé", "token", token));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        authService.resetPassword(request.get("token"), request.get("password"));
        return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
    }
}
