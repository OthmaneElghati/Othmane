package com.humanitaire.backend.repository;

import com.humanitaire.backend.entity.Beneficiary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    Page<Beneficiary> findByEmergencyLevel(Beneficiary.EmergencyLevel level, Pageable pageable);
    Page<Beneficiary> findByRegion(String region, Pageable pageable);
    Page<Beneficiary> findByMissionId(Long missionId, Pageable pageable);

    @Query("SELECT b FROM Beneficiary b WHERE " +
           "LOWER(b.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.region) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Beneficiary> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT b.emergencyLevel, COUNT(b) FROM Beneficiary b GROUP BY b.emergencyLevel")
    List<Object[]> countByEmergencyLevel();

    @Query("SELECT b.region, COUNT(b) FROM Beneficiary b GROUP BY b.region")
    List<Object[]> countByRegion();

    @Query("SELECT SUM(b.familySize) FROM Beneficiary b")
    Long sumFamilySize();
}
