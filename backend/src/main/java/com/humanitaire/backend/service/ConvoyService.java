package com.humanitaire.backend.service;

import com.humanitaire.backend.dto.ConvoyDTO;
import com.humanitaire.backend.entity.Convoy;
import com.humanitaire.backend.entity.Mission;
import com.humanitaire.backend.exception.ResourceNotFoundException;
import com.humanitaire.backend.repository.ConvoyRepository;
import com.humanitaire.backend.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConvoyService {

    private final ConvoyRepository convoyRepository;
    private final MissionRepository missionRepository;

    public Page<ConvoyDTO> getAllConvoys(Pageable pageable) {
        return convoyRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<ConvoyDTO> getConvoysByStatus(String status, Pageable pageable) {
        Convoy.ConvoyStatus convoyStatus = Convoy.ConvoyStatus.valueOf(status.toUpperCase());
        return convoyRepository.findByStatus(convoyStatus, pageable).map(this::toDTO);
    }

    public List<ConvoyDTO> getActiveConvoys() {
        return convoyRepository.findByStatus(Convoy.ConvoyStatus.IN_TRANSIT).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public ConvoyDTO getConvoyById(Long id) {
        Convoy convoy = convoyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Convoi", "id", id));
        return toDTO(convoy);
    }

    @Transactional
    public ConvoyDTO createConvoy(ConvoyDTO dto) {
        Convoy convoy = toEntity(dto);
        if (dto.getMissionId() != null) {
            Mission mission = missionRepository.findById(dto.getMissionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mission", "id", dto.getMissionId()));
            convoy.setMission(mission);
        }
        convoy = convoyRepository.save(convoy);
        return toDTO(convoy);
    }

    @Transactional
    public ConvoyDTO updateConvoy(Long id, ConvoyDTO dto) {
        Convoy convoy = convoyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Convoi", "id", id));

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
        convoy.setStatus(Convoy.ConvoyStatus.valueOf(dto.getStatus()));
        convoy.setDescription(dto.getDescription());
        convoy.setCargo(dto.getCargo());

        convoy = convoyRepository.save(convoy);
        return toDTO(convoy);
    }

    public void deleteConvoy(Long id) {
        if (!convoyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Convoi", "id", id);
        }
        convoyRepository.deleteById(id);
    }

    private ConvoyDTO toDTO(Convoy convoy) {
        return ConvoyDTO.builder()
                .id(convoy.getId())
                .name(convoy.getName())
                .departureCity(convoy.getDepartureCity())
                .destinationCity(convoy.getDestinationCity())
                .currentLatitude(convoy.getCurrentLatitude())
                .currentLongitude(convoy.getCurrentLongitude())
                .departureLatitude(convoy.getDepartureLatitude())
                .departureLongitude(convoy.getDepartureLongitude())
                .destinationLatitude(convoy.getDestinationLatitude())
                .destinationLongitude(convoy.getDestinationLongitude())
                .estimatedArrival(convoy.getEstimatedArrival())
                .departureTime(convoy.getDepartureTime())
                .status(convoy.getStatus() != null ? convoy.getStatus().name() : null)
                .description(convoy.getDescription())
                .cargo(convoy.getCargo())
                .missionId(convoy.getMission() != null ? convoy.getMission().getId() : null)
                .build();
    }

    private Convoy toEntity(ConvoyDTO dto) {
        return Convoy.builder()
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
                .cargo(dto.getCargo())
                .build();
    }
}
