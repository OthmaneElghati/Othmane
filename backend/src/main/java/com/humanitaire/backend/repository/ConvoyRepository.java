package com.humanitaire.backend.repository;

import com.humanitaire.backend.entity.Convoy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConvoyRepository extends JpaRepository<Convoy, Long> {

    Page<Convoy> findByStatus(Convoy.ConvoyStatus status, Pageable pageable);

    @Query("SELECT c FROM Convoy c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.departureCity) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.destinationCity) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Convoy> search(@Param("search") String search, Pageable pageable);

    long countByStatus(Convoy.ConvoyStatus status);

    List<Convoy> findByStatusNot(Convoy.ConvoyStatus status);
}
