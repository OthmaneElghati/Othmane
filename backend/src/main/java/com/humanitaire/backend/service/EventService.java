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

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Page<EventDTO> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<EventDTO> searchEvents(String query, Pageable pageable) {
        return eventRepository.findByTitleContainingIgnoreCase(query, pageable).map(this::toDTO);
    }

    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement", "id", id));
        return toDTO(event);
    }

    @Transactional
    public EventDTO createEvent(EventDTO dto) {
        Event event = toEntity(dto);
        event = eventRepository.save(event);
        return toDTO(event);
    }

    @Transactional
    public EventDTO updateEvent(Long id, EventDTO dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement", "id", id));

        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setCity(dto.getCity());
        event.setRegion(dto.getRegion());
        event.setOrganizer(dto.getOrganizer());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setCurrentParticipants(dto.getCurrentParticipants());
        event.setImage(dto.getImage());
        event.setStatus(dto.getStatus() != null ? Event.EventStatus.valueOf(dto.getStatus()) : event.getStatus());

        event = eventRepository.save(event);
        return toDTO(event);
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Événement", "id", id);
        }
        eventRepository.deleteById(id);
    }

    private EventDTO toDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .city(event.getCity())
                .region(event.getRegion())
                .organizer(event.getOrganizer())
                .maxParticipants(event.getMaxParticipants())
                .currentParticipants(event.getCurrentParticipants())
                .image(event.getImage())
                .status(event.getStatus() != null ? event.getStatus().name() : null)
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
                .image(dto.getImage())
                .status(dto.getStatus() != null ? Event.EventStatus.valueOf(dto.getStatus()) : Event.EventStatus.UPCOMING)
                .build();
    }
}
