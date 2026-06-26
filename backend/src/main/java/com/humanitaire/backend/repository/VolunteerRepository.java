package com.humanitaire.backend.repository;

import com.humanitaire.backend.entity.Volunteer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Page<Volunteer> findByAvailable(boolean available, Pageable pageable);
    Page<Volunteer> findByCity(String city, Pageable pageable);
    Page<Volunteer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);
    Optional<Volunteer> findByEmail(String email);

    @Query("SELECT v FROM Volunteer v WHERE v.available = true AND v.region = :region")
    List<Volunteer> findAvailableByRegion(String region);

    @Query("SELECT v FROM Volunteer v WHERE v.available = true AND v.skills LIKE %:skill%")
    List<Volunteer> findBySkillContaining(String skill);

    @Query("SELECT MONTH(v.createdAt), COUNT(v) FROM Volunteer v WHERE YEAR(v.createdAt) = YEAR(CURRENT_DATE) GROUP BY MONTH(v.createdAt)")
    List<Object[]> countByMonth();
}
