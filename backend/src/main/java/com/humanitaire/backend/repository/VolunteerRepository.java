package com.humanitaire.backend.repository;

import com.humanitaire.backend.entity.Volunteer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    Page<Volunteer> findByAvailableTrue(Pageable pageable);
    Page<Volunteer> findByRegion(String region, Pageable pageable);
    Optional<Volunteer> findByUserId(Long userId);

    @Query("SELECT v FROM Volunteer v WHERE " +
           "LOWER(v.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(v.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(v.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(v.skills) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Volunteer> search(@Param("search") String search, Pageable pageable);

    long countByAvailableTrue();

    @Query("SELECT v.region, COUNT(v) FROM Volunteer v GROUP BY v.region")
    List<Object[]> countByRegion();

    @Query("SELECT v FROM Volunteer v WHERE v.available = true AND v.region = :region")
    List<Volunteer> findAvailableByRegion(@Param("region") String region);

    @Query("SELECT v FROM Volunteer v WHERE v.available = true AND v.skills LIKE %:skill%")
    List<Volunteer> findAvailableBySkill(@Param("skill") String skill);
}
