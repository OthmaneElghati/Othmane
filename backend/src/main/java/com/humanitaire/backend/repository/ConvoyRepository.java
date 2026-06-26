package com.humanitaire.backend.repository;

import com.humanitaire.backend.entity.Convoy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConvoyRepository extends JpaRepository<Convoy, Long> {
    Page<Convoy> findByStatus(Convoy.ConvoyStatus status, Pageable pageable);
    List<Convoy> findByStatus(Convoy.ConvoyStatus status);
    Page<Convoy> findByMissionId(Long missionId, Pageable pageable);
}
