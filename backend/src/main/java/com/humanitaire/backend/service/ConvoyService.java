package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.ConvoyDTO;
import com.humanitaire.backend.entity.Convoy;
import com.humanitaire.backend.exception.ResourceNotFoundException;
import com.humanitaire.backend.repository.ConvoyRepository;
import com.humanitaire.backend.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConvoyService {

    private final ConvoyRepository convoyRepository;
    private final MissionRepository missionRepository;

    public Page<ConvoyDTO> getAll(Pageable pageable) {
        return convoyRepository.findAll(pageable).map(this::toDTO);
    }

    public ConvoyDTO getById(Long id) {
        return toDTO(findById(id));
    }

    public Page<ConvoyDTO> search(String query, Pageable pageable) {
        return convoyRepository.search(query, pageable).map(this::toDTO);
    }

    public Page<ConvoyDTO> getByStatus(String status, Pageable pageable) {
        return convoyRepository.findByStatus(Convoy.ConvoyStatus.valueOf(status), pageable).map(this::toDTO);
    }

    public List<ConvoyDTO> getActiveConvoys() {
        return convoyRepository.findByStatusNot(Convoy.ConvoyStatus.DELIVERED)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public ConvoyDTO create(ConvoyDTO dto) {
        return toDTO(convoyRepository.save(toEntity(dto)));
    }

    @Transactional
    public ConvoyDTO update(Long id, ConvoyDTO dto) {
        Convoy convoy = findById(id);
        convoy.setName(dto.getName());
        convoy.setDepartureCity(dto.getDepartureCity());
        convoy.setDestinationCity(dto.getDestinationCity());
        convoy.setCurrentLatitude(dto.getCurrentLatitude());
        convoy.setCurrentLongitude(dto.getCurrentLongitude());
        convoy.setDepartureLatitude(dto.getDepartureLatitude());
        convoy.setDepartureLongitude(dto.getDepartureLongitude());
        convoy.setDestinationLatitude(dto.getDestinationLatitude());
        convoy.setDestinationLongitude(dto.getDestinationLongitude());
        convoy.setEstimatedArrival(dto.getEstimatedArrival());
        convoy.setDepartureTime(dto.getDepartureTime());
        if (dto.getStatus() != null) convoy.setStatus(Convoy.ConvoyStatus.valueOf(dto.getStatus()));
        convoy.setDescription(dto.getDescription());
        convoy.setCargo(dto.getCargo());
        return toDTO(convoyRepository.save(convoy));
    }

    @Transactional
    public void delete(Long id) {
        convoyRepository.deleteById(id);
    }

    private Convoy findById(Long id) {
        return convoyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Convoi non trouvé avec l'id: " + id));
    }

    private ConvoyDTO toDTO(Convoy c) {
        return ConvoyDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .departureCity(c.getDepartureCity())
                .destinationCity(c.getDestinationCity())
                .currentLatitude(c.getCurrentLatitude())
                .currentLongitude(c.getCurrentLongitude())
                .departureLatitude(c.getDepartureLatitude())
                .departureLongitude(c.getDepartureLongitude())
                .destinationLatitude(c.getDestinationLatitude())
                .destinationLongitude(c.getDestinationLongitude())
                .estimatedArrival(c.getEstimatedArrival())
                .departureTime(c.getDepartureTime())
                .status(c.getStatus() != null ? c.getStatus().name() : null)
                .description(c.getDescription())
                .cargo(c.getCargo())
                .missionId(c.getMission() != null ? c.getMission().getId() : null)
                .missionTitle(c.getMission() != null ? c.getMission().getTitle() : null)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private Convoy toEntity(ConvoyDTO dto) {
        Convoy.ConvoyBuilder builder = Convoy.builder()
                .name(dto.getName())
                .departureCity(dto.getDepartureCity())
                .destinationCity(dto.getDestinationCity())
                .currentLatitude(dto.getCurrentLatitude())
                .currentLongitude(dto.getCurrentLongitude())
                .departureLatitude(dto.getDepartureLatitude())
                .departureLongitude(dto.getDepartureLongitude())
                .destinationLatitude(dto.getDestinationLatitude())
                .destinationLongitude(dto.getDestinationLongitude())
                .estimatedArrival(dto.getEstimatedArrival())
                .departureTime(dto.getDepartureTime())
                .status(dto.getStatus() != null ? Convoy.ConvoyStatus.valueOf(dto.getStatus()) : Convoy.ConvoyStatus.PENDING)
                .description(dto.getDescription())
                .cargo(dto.getCargo());
        if (dto.getMissionId() != null) {
            missionRepository.findById(dto.getMissionId()).ifPresent(builder::mission);
        }
        return builder.build();
    }
}
