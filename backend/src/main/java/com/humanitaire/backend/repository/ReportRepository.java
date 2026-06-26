package com.humanitaire.backend.repository;

import com.humanitaire.backend.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByType(Report.ReportType type, Pageable pageable);
    Page<Report> findByMissionId(Long missionId, Pageable pageable);
}
