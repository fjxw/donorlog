package com.donorlog.backend.repository;

import com.donorlog.backend.entity.DonorProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DonorProfileRepository extends JpaRepository<DonorProfileEntity, Long> {
    Optional<DonorProfileEntity> findByUserId(String userId);
}
