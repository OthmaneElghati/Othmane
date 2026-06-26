package com.humanitaire.backend.repository;

import com.humanitaire.backend.entity.Mission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
    Page<Mission> findByStatus(Mission.MissionStatus status, Pageable pageable);
    Page<Mission> findByRegion(String region, Pageable pageable);
    Page<Mission> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    long countByStatus(Mission.MissionStatus status);

    @Query("SELECT m.region, COUNT(m) FROM Mission m GROUP BY m.region")
    List<Object[]> countByRegion();

    @Query("SELECT m.status, COUNT(m) FROM Mission m GROUP BY m.status")
    List<Object[]> countByStatusGroup();

    @Query("SELECT MONTH(m.createdAt), COUNT(m) FROM Mission m WHERE YEAR(m.createdAt) = YEAR(CURRENT_DATE) GROUP BY MONTH(m.createdAt)")
    List<Object[]> countByMonth();
}
