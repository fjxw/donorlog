package com.donorlog.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "donor_profiles")
public class DonorProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String fullName;
    private String bloodGroup; // 0(I), A(II), B(III), AB(IV)
    private String rhFactor; // +, -
    private String kellFactor; // Kell-, Kell+
    private String gender; // MALE, FEMALE
    private LocalDate birthDate;
    private Long updatedAt;

    public DonorProfileEntity() {
    }

    public DonorProfileEntity(String userId, String fullName, String bloodGroup, String rhFactor,
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
