package com.humanitaire.backend.repository;

import com.humanitaire.backend.entity.Donation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    Page<Donation> findByDonorId(Long donorId, Pageable pageable);
    Page<Donation> findByMissionId(Long missionId, Pageable pageable);
    Page<Donation> findByStatus(Donation.DonationStatus status, Pageable pageable);

    @Query("SELECT SUM(d.amount) FROM Donation d WHERE d.status = 'COMPLETED'")
    Double getTotalDonationAmount();

    @Query("SELECT MONTH(d.createdAt), SUM(d.amount) FROM Donation d WHERE d.status = 'COMPLETED' AND YEAR(d.createdAt) = YEAR(CURRENT_DATE) GROUP BY MONTH(d.createdAt)")
    List<Object[]> getMonthlyDonations();

    @Query("SELECT d.paymentMethod, SUM(d.amount) FROM Donation d WHERE d.status = 'COMPLETED' GROUP BY d.paymentMethod")
    List<Object[]> getDonationsByMethod();
}
