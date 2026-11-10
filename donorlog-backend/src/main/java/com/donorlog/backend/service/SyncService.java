package com.donorlog.backend.service;

import com.donorlog.backend.dto.BloodStationDto;
import com.donorlog.backend.dto.DonationDto;
import com.donorlog.backend.dto.DonorProfileDto;
import com.donorlog.backend.dto.SyncRequestDto;
import com.donorlog.backend.dto.SyncResponseDto;
import com.donorlog.backend.entity.DonationEntity;
import com.donorlog.backend.repository.DonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SyncService {

    private final DonationService donationService;
    private final DonationRepository donationRepository;
    private final DonorProfileService profileService;
    private final BloodStationService stationService;

    public SyncService(DonationService donationService,
                       DonationRepository donationRepository,
                       DonorProfileService profileService,
                       BloodStationService stationService) {
        this.donationService = donationService;
        this.donationRepository = donationRepository;
        this.profileService = profileService;
        this.stationService = stationService;
    }

    @Transactional
    public SyncResponseDto processSync(SyncRequestDto request) {
        String userId = (request.getUserId() != null && !request.getUserId().trim().isEmpty())
                ? request.getUserId()
                : "default_user";

        Long clientSyncTime = request.getLastSyncTimestamp() != null ? request.getLastSyncTimestamp() : 0L;

        // 1. Reconcile profile (Last-Write-Wins)
        DonorProfileDto serverProfile = profileService.getProfile(userId);
        if (request.getProfile() != null) {
            if (serverProfile == null || (request.getProfile().getUpdatedAt() != null && request.getProfile().getUpdatedAt() > serverProfile.getUpdatedAt())) {
                serverProfile = profileService.saveOrUpdate(request.getProfile());
            }
        }

        // 2. Process dirty donations from mobile client
        if (request.getDirtyDonations() != null) {
            for (DonationDto dirty : request.getDirtyDonations()) {
                dirty.setUserId(userId);
                donationService.saveOrUpdate(dirty);
            }
        }

        // 3. Collect updated donations for client (all donations modified after client's last sync time)
        List<DonationEntity> updatedEntities = donationRepository.findByUserIdAndUpdatedAtGreaterThan(userId, clientSyncTime);
        List<DonationDto> updatedDtos = updatedEntities.stream()
                .map(donationService::toDto)
                .collect(Collectors.toList());

        // 4. Get active blood stations
        List<BloodStationDto> stations = stationService.getAll();

        Long currentServerTime = System.currentTimeMillis();
        return new SyncResponseDto(currentServerTime, serverProfile, updatedDtos, stations);
    }
}
