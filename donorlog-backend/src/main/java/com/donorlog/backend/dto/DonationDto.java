package com.donorlog.backend.dto;

import java.time.LocalDate;

public class DonationDto {
    private String id;
    private String userId;
    private LocalDate donationDate;
    private String componentType;
    private Integer volumeMl;
    private String stationName;
    private Integer hemoglobinGramsPerLiter;
    private Integer systolicBp;
    private Integer diastolicBp;
    private Integer pulseBpm;
    private String wellbeing;
    private String notes;
    private Long updatedAt;
    private Boolean isDeleted;

    public DonationDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClientUuid(String clientUuid) {
        if (this.id == null || this.id.isEmpty()) {
            this.id = clientUuid;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    @com.fasterxml.jackson.annotation.JsonSetter("donationDate")
    public void setDonationDateObj(Object dateObj) {
        if (dateObj instanceof String) {
            try {
                this.donationDate = LocalDate.parse((String) dateObj);
            } catch (Exception ignored) {}
        } else if (dateObj instanceof Number) {
            long millis = ((Number) dateObj).longValue();
            this.donationDate = java.time.Instant.ofEpochMilli(millis)
                    .atZone(java.time.ZoneId.of("Europe/Moscow"))
                    .toLocalDate();
        }
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public Integer getVolumeMl() {
        return volumeMl;
    }

    public void setVolumeMl(Integer volumeMl) {
        this.volumeMl = volumeMl;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Integer getHemoglobinGramsPerLiter() {
        return hemoglobinGramsPerLiter;
    }

    public void setHemoglobinGramsPerLiter(Integer hemoglobinGramsPerLiter) {
        this.hemoglobinGramsPerLiter = hemoglobinGramsPerLiter;
    }

    public void setHemoglobin(Object hb) {
        if (hb instanceof Number) {
            this.hemoglobinGramsPerLiter = ((Number) hb).intValue();
        }
    }

    public Integer getSystolicBp() {
        return systolicBp;
    }

    public void setSystolicBp(Integer systolicBp) {
        this.systolicBp = systolicBp;
    }

    public Integer getDiastolicBp() {
        return diastolicBp;
    }

    public void setDiastolicBp(Integer diastolicBp) {
        this.diastolicBp = diastolicBp;
    }

    public Integer getPulseBpm() {
        return pulseBpm;
    }

    public void setPulseBpm(Integer pulseBpm) {
        this.pulseBpm = pulseBpm;
    }

    public void setPulse(Integer pulse) {
        this.pulseBpm = pulse;
    }

    public String getWellbeing() {
        return wellbeing;
    }

    public void setWellbeing(String wellbeing) {
        this.wellbeing = wellbeing;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
