package com.donorlog.backend.dto;

import java.time.LocalDate;

public class DonorProfileDto {
    private String userId;
    private String fullName;
    private String bloodGroup;
    private String rhFactor;
    private String kellFactor;
    private String gender;
    private LocalDate birthDate;
    private Long updatedAt;

    public DonorProfileDto() {
    }

    public DonorProfileDto(String userId, String fullName, String bloodGroup, String rhFactor,
                           String kellFactor, String gender, LocalDate birthDate, Long updatedAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.bloodGroup = bloodGroup;
        this.rhFactor = rhFactor;
        this.kellFactor = kellFactor;
        this.gender = gender;
        this.birthDate = birthDate;
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getRhFactor() {
        return rhFactor;
    }

    public void setRhFactor(String rhFactor) {
        this.rhFactor = rhFactor;
    }

    public String getKellFactor() {
        return kellFactor;
    }

    public void setKellFactor(String kellFactor) {
        this.kellFactor = kellFactor;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
