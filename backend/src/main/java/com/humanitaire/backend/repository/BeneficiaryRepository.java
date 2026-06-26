package com.humanitaire.backend.repository;

import com.humanitaire.backend.entity.Beneficiary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    Page<Beneficiary> findByFullNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Beneficiary> findByEmergencyLevel(Beneficiary.EmergencyLevel level, Pageable pageable);
    Page<Beneficiary> findByMissionId(Long missionId, Pageable pageable);
    long countByMissionId(Long missionId);

    @Query("SELECT b.region, COUNT(b) FROM Beneficiary b GROUP BY b.region")
    List<Object[]> countByRegion();
}
