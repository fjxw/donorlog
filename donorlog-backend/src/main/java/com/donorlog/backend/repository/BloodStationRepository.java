package com.donorlog.backend.repository;

import com.donorlog.backend.entity.BloodStationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BloodStationRepository extends JpaRepository<BloodStationEntity, String> {
    List<BloodStationEntity> findAllByOrderByCityAscNameAsc();
}
