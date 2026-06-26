package com.humanitaire.backend.repository;

import com.humanitaire.backend.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByStatus(Event.EventStatus status, Pageable pageable);
    Page<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Event> findByCity(String city, Pageable pageable);
}
