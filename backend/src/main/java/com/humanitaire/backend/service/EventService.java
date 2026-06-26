package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.EventDTO;
import com.humanitaire.backend.entity.Event;
import com.humanitaire.backend.exception.ResourceNotFoundException;
import com.humanitaire.backend.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Page<EventDTO> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable).map(this::toDTO);
    }

    public EventDTO getById(Long id) {
        return toDTO(findById(id));
    }

    public Page<EventDTO> search(String query, Pageable pageable) {
        return eventRepository.search(query, pageable).map(this::toDTO);
    }

    public Page<EventDTO> getByStatus(String status, Pageable pageable) {
        return eventRepository.findByStatus(Event.EventStatus.valueOf(status), pageable).map(this::toDTO);
    }

    public List<EventDTO> getUpcomingEvents() {
        return eventRepository.findByStatusOrderByEventDateAsc(Event.EventStatus.UPCOMING)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public EventDTO create(EventDTO dto) {
        return toDTO(eventRepository.save(toEntity(dto)));
    }

    @Transactional
    public EventDTO update(Long id, EventDTO dto) {
        Event event = findById(id);
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setCity(dto.getCity());
        event.setRegion(dto.getRegion());
        event.setOrganizer(dto.getOrganizer());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setCurrentParticipants(dto.getCurrentParticipants());
        if (dto.getStatus() != null) event.setStatus(Event.EventStatus.valueOf(dto.getStatus()));
        return toDTO(eventRepository.save(event));
    }

    @Transactional
    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    private Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + id));
    }

    private EventDTO toDTO(Event e) {
        return EventDTO.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .location(e.getLocation())
                .city(e.getCity())
                .region(e.getRegion())
                .organizer(e.getOrganizer())
                .maxParticipants(e.getMaxParticipants())
                .currentParticipants(e.getCurrentParticipants())
                .image(e.getImage())
                .status(e.getStatus() != null ? e.getStatus().name() : null)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private Event toEntity(EventDTO dto) {
        return Event.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .location(dto.getLocation())
                .city(dto.getCity())
                .region(dto.getRegion())
                .organizer(dto.getOrganizer())
                .maxParticipants(dto.getMaxParticipants())
                .currentParticipants(dto.getCurrentParticipants() != null ? dto.getCurrentParticipants() : 0)
                .status(dto.getStatus() != null ? Event.EventStatus.valueOf(dto.getStatus()) : Event.EventStatus.UPCOMING)
                .build();
    }
}
