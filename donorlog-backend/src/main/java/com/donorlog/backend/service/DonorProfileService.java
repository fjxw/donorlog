package com.donorlog.backend.service;

import com.donorlog.backend.dto.DonorProfileDto;
import com.donorlog.backend.entity.DonorProfileEntity;
import com.donorlog.backend.repository.DonorProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonorProfileService {

    private final DonorProfileRepository repository;

    public DonorProfileService(DonorProfileRepository repository) {
        this.repository = repository;
    }

    public DonorProfileDto getProfile(String userId) {
        return repository.findByUserId(userId)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional
    public DonorProfileDto saveOrUpdate(DonorProfileDto dto) {
        DonorProfileEntity entity = repository.findByUserId(dto.getUserId())
                .orElseGet(DonorProfileEntity::new);

        entity.setUserId(dto.getUserId());
        entity.setFullName(dto.getFullName());
        entity.setBloodGroup(dto.getBloodGroup());
        entity.setRhFactor(dto.getRhFactor());
        entity.setKellFactor(dto.getKellFactor());
        entity.setGender(dto.getGender());
        entity.setBirthDate(dto.getBirthDate());
        entity.setUpdatedAt(System.currentTimeMillis());

        DonorProfileEntity saved = repository.save(entity);
        return toDto(saved);
    }

    public DonorProfileDto toDto(DonorProfileEntity entity) {
        return new DonorProfileDto(
                entity.getUserId(),
                entity.getFullName(),
                entity.getBloodGroup(),
                entity.getRhFactor(),
                entity.getKellFactor(),
                entity.getGender(),
                entity.getBirthDate(),
                entity.getUpdatedAt()
        );
    }
}
