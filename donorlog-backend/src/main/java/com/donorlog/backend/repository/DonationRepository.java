package com.donorlog.backend.repository;

import com.donorlog.backend.entity.DonationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<DonationEntity, String> {
    List<DonationEntity> findByUserIdAndIsDeletedFalseOrderByDonationDateDesc(String userId);
    List<DonationEntity> findByUserIdAndUpdatedAtGreaterThan(String userId, Long updatedAt);
}
