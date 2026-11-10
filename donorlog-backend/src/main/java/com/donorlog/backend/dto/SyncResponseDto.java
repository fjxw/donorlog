package com.donorlog.backend.dto;

import java.util.List;

public class SyncResponseDto {
    private Long serverSyncTimestamp;
    private DonorProfileDto profile;
    private List<DonationDto> updatedDonations;
    private List<BloodStationDto> stations;

    public SyncResponseDto() {
    }

    public SyncResponseDto(Long serverSyncTimestamp, DonorProfileDto profile,
                           List<DonationDto> updatedDonations, List<BloodStationDto> stations) {
        this.serverSyncTimestamp = serverSyncTimestamp;
        this.profile = profile;
        this.updatedDonations = updatedDonations;
        this.stations = stations;
    }

    public Long getServerSyncTimestamp() {
        return serverSyncTimestamp;
    }

    public void setServerSyncTimestamp(Long serverSyncTimestamp) {
        this.serverSyncTimestamp = serverSyncTimestamp;
    }

    public DonorProfileDto getProfile() {
        return profile;
    }

    public void setProfile(DonorProfileDto profile) {
        this.profile = profile;
    }

    public List<DonationDto> getUpdatedDonations() {
        return updatedDonations;
    }

    public void setUpdatedDonations(List<DonationDto> updatedDonations) {
        this.updatedDonations = updatedDonations;
    }

    public List<BloodStationDto> getStations() {
        return stations;
    }

    public void setStations(List<BloodStationDto> stations) {
        this.stations = stations;
    }
}
