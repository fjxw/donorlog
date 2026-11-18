package com.donorlog.backend.service;

import com.donorlog.backend.dto.DonationDto;
import com.donorlog.backend.entity.DonationEntity;
import com.donorlog.backend.repository.DonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DonationService {

    private final DonationRepository repository;

    public DonationService(DonationRepository repository) {
        this.repository = repository;
    }

    public List<DonationDto> getAllForUser(String userId) {
        List<DonationDto> list = repository.findByUserIdAndIsDeletedFalseOrderByDonationDateDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        if (list.isEmpty() && ("alexey.ryakhovsky@example.com".equalsIgnoreCase(userId) || "default-user".equals(userId) || "default_user".equals(userId))) {
            List<DonationDto> fallback = repository.findByUserIdAndIsDeletedFalseOrderByDonationDateDesc("default-user")
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            if (fallback.isEmpty()) {
                fallback = repository.findByUserIdAndIsDeletedFalseOrderByDonationDateDesc("alexey.ryakhovsky@example.com")
                        .stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
            }
            return fallback;
        }

        return list;
    }

    public DonationDto getById(String id) {
        return repository.findById(id).map(this::toDto).orElse(null);
    }

    @Transactional
    public DonationDto saveOrUpdate(DonationDto dto) {
        String id = (dto.getId() != null && !dto.getId().trim().isEmpty())
                ? dto.getId()
                : UUID.randomUUID().toString();

        DonationEntity entity = repository.findById(id).orElseGet(() -> {
            DonationEntity e = new DonationEntity();
            e.setId(id);
            return e;
        });

        entity.setUserId(dto.getUserId() != null ? dto.getUserId() : "default_user");
        entity.setDonationDate(dto.getDonationDate());
        entity.setComponentType(dto.getComponentType());
        entity.setVolumeMl(dto.getVolumeMl());
        entity.setStationName(dto.getStationName());
        entity.setHemoglobinGramsPerLiter(dto.getHemoglobinGramsPerLiter());
        entity.setSystolicBp(dto.getSystolicBp());
        entity.setDiastolicBp(dto.getDiastolicBp());
        entity.setPulseBpm(dto.getPulseBpm());
        entity.setWellbeing(dto.getWellbeing());
        entity.setNotes(dto.getNotes());
        entity.setIsDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : false);
        entity.setUpdatedAt(System.currentTimeMillis());

        DonationEntity saved = repository.save(entity);
        return toDto(saved);
    }

    @Transactional
    public boolean delete(String id) {
        return repository.findById(id).map(e -> {
            e.setIsDeleted(true);
            e.setUpdatedAt(System.currentTimeMillis());
            repository.save(e);
            return true;
        }).orElse(false);
    }

    public DonationDto toDto(DonationEntity entity) {
        DonationDto dto = new DonationDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setDonationDate(entity.getDonationDate());
        dto.setComponentType(entity.getComponentType());
        dto.setVolumeMl(entity.getVolumeMl());
        dto.setStationName(entity.getStationName());
        dto.setHemoglobinGramsPerLiter(entity.getHemoglobinGramsPerLiter());
        dto.setSystolicBp(entity.getSystolicBp());
        dto.setDiastolicBp(entity.getDiastolicBp());
        dto.setPulseBpm(entity.getPulseBpm());
        dto.setWellbeing(entity.getWellbeing());
        dto.setNotes(entity.getNotes());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setIsDeleted(entity.getIsDeleted());
        return dto;
    }
}
