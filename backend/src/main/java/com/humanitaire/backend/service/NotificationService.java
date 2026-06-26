package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.NotificationDTO;
import com.humanitaire.backend.entity.Notification;
import com.humanitaire.backend.entity.User;
import com.humanitaire.backend.exception.ResourceNotFoundException;
import com.humanitaire.backend.repository.NotificationRepository;
import com.humanitaire.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Page<NotificationDTO> getByUser(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(this::toDTO);
    }

    public List<NotificationDTO> getUnreadByUser(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::toDTO).toList();
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public NotificationDTO create(NotificationDTO dto) {
        Notification notification = Notification.builder()
                .title(dto.getTitle())
                .message(dto.getMessage())
                .type(dto.getType() != null ? Notification.NotificationType.valueOf(dto.getType()) : Notification.NotificationType.INFO)
                .build();
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
            notification.setUser(user);
        }
        return toDTO(notificationRepository.save(notification));
    }

    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    @Transactional
    public void delete(Long id) {
        notificationRepository.deleteById(id);
    }

    public void createSystemNotification(Long userId, String title, String message, Notification.NotificationType type) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            notificationRepository.save(Notification.builder()
                    .title(title)
                    .message(message)
                    .type(type)
                    .user(user)
                    .build());
        }
    }

    private NotificationDTO toDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType() != null ? n.getType().name() : null)
                .read(n.isRead())
                .userId(n.getUser() != null ? n.getUser().getId() : null)
                .createdAt(n.getCreatedAt())
                .build();
    }
}
