package com.donorlog.backend.dto;

import java.util.List;

public class SyncRequestDto {
    private String userId;
    private Long lastSyncTimestamp;
    private DonorProfileDto profile;
    private List<DonationDto> dirtyDonations;

    public SyncRequestDto() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getLastSyncTimestamp() {
        return lastSyncTimestamp;
    }

    public void setLastSyncTimestamp(Long lastSyncTimestamp) {
        this.lastSyncTimestamp = lastSyncTimestamp;
    }

    public DonorProfileDto getProfile() {
        return profile;
    }

    public void setProfile(DonorProfileDto profile) {
        this.profile = profile;
    }

    public List<DonationDto> getDirtyDonations() {
        return dirtyDonations;
    }

    public void setDirtyDonations(List<DonationDto> dirtyDonations) {
        this.dirtyDonations = dirtyDonations;
    }
}
