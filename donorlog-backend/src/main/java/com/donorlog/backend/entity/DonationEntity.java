package com.donorlog.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "donations")
public class DonationEntity {

    @Id
    @Column(length = 64)
    private String id;

    private String userId;
    private LocalDate donationDate;
    private String componentType; // WHOLE_BLOOD, PLASMA, PLATELETS, ERYTHROCYTES
    private Integer volumeMl;
    private String stationName;
    private Integer hemoglobinGramsPerLiter;
    private Integer systolicBp;
    private Integer diastolicBp;
    private Integer pulseBpm;
    private String wellbeing; // GOOD, SATISFACTORY, WEAK

    @Column(length = 1000)
    private String notes;

    private Long updatedAt;
    private Boolean isDeleted = false;

    public DonationEntity() {
    }

    public DonationEntity(String id, String userId, LocalDate donationDate, String componentType,
                          Integer volumeMl, String stationName, Integer hemoglobinGramsPerLiter,
                          Integer systolicBp, Integer diastolicBp, Integer pulseBpm,
                          String wellbeing, String notes, Long updatedAt, Boolean isDeleted) {
        this.id = id;
        this.userId = userId;
        this.donationDate = donationDate;
        this.componentType = componentType;
        this.volumeMl = volumeMl;
        this.stationName = stationName;
        this.hemoglobinGramsPerLiter = hemoglobinGramsPerLiter;
        this.systolicBp = systolicBp;
        this.diastolicBp = diastolicBp;
        this.pulseBpm = pulseBpm;
        this.wellbeing = wellbeing;
        this.notes = notes;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted != null ? isDeleted : false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
