package com.humanitaire.backend.repository;

import com.humanitaire.backend.entity.Mission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    Page<Mission> findByStatus(Mission.MissionStatus status, Pageable pageable);
    Page<Mission> findByRegion(String region, Pageable pageable);
    Page<Mission> findByPriority(Mission.MissionPriority priority, Pageable pageable);

    @Query("SELECT m FROM Mission m WHERE " +
           "LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.region) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Mission> search(@Param("search") String search, Pageable pageable);

    long countByStatus(Mission.MissionStatus status);

    @Query("SELECT m.region, COUNT(m) FROM Mission m GROUP BY m.region")
    List<Object[]> countByRegion();

    @Query("SELECT m.priority, COUNT(m) FROM Mission m GROUP BY m.priority")
    List<Object[]> countByPriority();

    @Query("SELECT m.status, COUNT(m) FROM Mission m GROUP BY m.status")
    List<Object[]> countByStatusGroup();

    List<Mission> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT m FROM Mission m WHERE m.status = 'ACTIVE' ORDER BY m.priorityScore DESC")
    List<Mission> findActiveMissionsSorted();

    Page<Mission> findByManagerId(Long managerId, Pageable pageable);

    @Query("SELECT SUM(m.budget) FROM Mission m")
    Double sumBudget();
}
