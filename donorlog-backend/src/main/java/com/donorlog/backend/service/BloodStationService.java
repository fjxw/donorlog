package com.donorlog.backend.service;

import com.donorlog.backend.dto.BloodStationDto;
import com.donorlog.backend.entity.BloodStationEntity;
import com.donorlog.backend.repository.BloodStationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BloodStationService {

    private final BloodStationRepository repository;

    public BloodStationService(BloodStationRepository repository) {
        this.repository = repository;
    }

    public List<BloodStationDto> getAll() {
        return repository.findAllByOrderByCityAscNameAsc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BloodStationDto toDto(BloodStationEntity entity) {
        return new BloodStationDto(
                entity.getId(),
                entity.getName(),
                entity.getCity(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getWorkingHours(),
                entity.getTrafficLightJson(),
                entity.getUpdatedAt()
        );
    }
}
