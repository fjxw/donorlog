package com.donorlog.backend.service;

import com.donorlog.backend.dto.DonorProfileDto;
import com.donorlog.backend.entity.DonorProfileEntity;
import com.donorlog.backend.entity.UserEntity;
import com.donorlog.backend.repository.DonorProfileRepository;
import com.donorlog.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonorProfileService {

    private final DonorProfileRepository repository;
    private final UserRepository userRepository;

    public DonorProfileService(DonorProfileRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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

        // Также синхронизируем с таблицей users для pgAdmin
        try {
            String email = (dto.getUserId() != null && !dto.getUserId().trim().isEmpty())
                    ? dto.getUserId().trim().toLowerCase()
                    : "default_user";
            UserEntity user = userRepository.findByEmail(email).orElseGet(UserEntity::new);
            user.setEmail(email);
            if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
                user.setFullName(dto.getFullName().trim());
            } else if (user.getFullName() == null) {
                user.setFullName(email);
            }
            if (dto.getBloodGroup() != null) user.setBloodGroup(dto.getBloodGroup());
            if (dto.getRhFactor() != null) user.setRhFactor(dto.getRhFactor());
            if (user.getRegion() == null) user.setRegion("г. Москва");
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(System.currentTimeMillis());
            }
            userRepository.save(user);
        } catch (Exception ignored) {}

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
