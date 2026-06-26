package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.ProfileUpdateRequest;
import com.humanitaire.backend.dto.UserDTO;
import com.humanitaire.backend.entity.User;
import com.humanitaire.backend.exception.BadRequestException;
import com.humanitaire.backend.exception.ResourceNotFoundException;
import com.humanitaire.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserDTO> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDTO);
    }

    public UserDTO getById(Long id) {
        return toDTO(findById(id));
    }

    public Page<UserDTO> search(String query, Pageable pageable) {
        return userRepository.search(query, pageable).map(this::toDTO);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        User user = findById(id);
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getCity() != null) user.setCity(dto.getCity());
        if (dto.getRegion() != null) user.setRegion(dto.getRegion());
        return toDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateProfile(Long id, ProfileUpdateRequest request) {
        User user = findById(id);
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getRegion() != null) user.setRegion(request.getRegion());
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getCurrentPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadRequestException("Mot de passe actuel incorrect");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        return toDTO(userRepository.save(user));
    }

    @Transactional
    public void toggleEnabled(Long id) {
        User user = findById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .city(user.getCity())
                .region(user.getRegion())
                .avatar(user.getAvatar())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
