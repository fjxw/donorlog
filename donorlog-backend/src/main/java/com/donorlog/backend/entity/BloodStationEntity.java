package com.donorlog.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "blood_stations")
public class BloodStationEntity {

    @Id
    @Column(length = 64)
    private String id;

    private String name;
    private String city;
    private String address;
    private String phone;
    private String workingHours;

    @Column(length = 1000)
    private String trafficLightJson;

    private Long updatedAt;

    public BloodStationEntity() {
    }

    public BloodStationEntity(String id, String name, String city, String address, String phone,
                              String workingHours, String trafficLightJson, Long updatedAt) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.workingHours = workingHours;
        this.trafficLightJson = trafficLightJson;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getTrafficLightJson() {
        return trafficLightJson;
    }

    public void setTrafficLightJson(String trafficLightJson) {
        this.trafficLightJson = trafficLightJson;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
