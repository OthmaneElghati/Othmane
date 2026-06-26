package com.humanitaire.backend.repository;

import com.humanitaire.backend.entity.Donation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    Page<Donation> findByStatus(Donation.DonationStatus status, Pageable pageable);
    Page<Donation> findByDonorId(Long donorId, Pageable pageable);
    Page<Donation> findByMissionId(Long missionId, Pageable pageable);

    @Query("SELECT d FROM Donation d WHERE " +
           "LOWER(d.donorName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.donorEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.transactionId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Donation> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.status = 'COMPLETED'")
    Double sumCompletedAmount();

    @Query("SELECT CONCAT(YEAR(d.createdAt), '-', MONTH(d.createdAt)), SUM(d.amount) FROM Donation d WHERE d.status = 'COMPLETED' GROUP BY YEAR(d.createdAt), MONTH(d.createdAt) ORDER BY YEAR(d.createdAt), MONTH(d.createdAt)")
    List<Object[]> sumByMonth();

    @Query("SELECT d.paymentMethod, COUNT(d) FROM Donation d GROUP BY d.paymentMethod")
    List<Object[]> countByPaymentMethod();

    List<Donation> findTop5ByOrderByCreatedAtDesc();

    long countByDonorId(Long donorId);

    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.donor.id = :donorId AND d.status = 'COMPLETED'")
    Double sumByDonorId(@Param("donorId") Long donorId);

    long countByStatus(Donation.DonationStatus status);
}
