package com.donorlog.backend.repository;

import com.donorlog.backend.entity.ContraindicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContraindicationRepository extends JpaRepository<ContraindicationEntity, Long> {
    List<ContraindicationEntity> findByCategory(String category);
    List<ContraindicationEntity> findByTitleContainingIgnoreCase(String query);
}
