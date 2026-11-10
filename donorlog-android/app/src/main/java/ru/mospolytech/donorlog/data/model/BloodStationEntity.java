package ru.mospolytech.donorlog.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "stations")
public class BloodStationEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String city;
    private String address;
    private String phone;
    private String schedule;
    private String needStatus;

    private String oPosStatus;
    private String oNegStatus;
    private String aPosStatus;
    private String aNegStatus;
    private String bPosStatus;
    private String bNegStatus;
    private String abPosStatus;
    private String abNegStatus;

    public BloodStationEntity() {
    }

    @androidx.room.Ignore
    public BloodStationEntity(String name, String city, String address, String phone,
                              String schedule, String needStatus,
                              String oPosStatus, String oNegStatus,
                              String aPosStatus, String aNegStatus,
                              String bPosStatus, String bNegStatus,
                              String abPosStatus, String abNegStatus) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.schedule = schedule;
        this.needStatus = needStatus;
        this.oPosStatus = oPosStatus;
        this.oNegStatus = oNegStatus;
        this.aPosStatus = aPosStatus;
        this.aNegStatus = aNegStatus;
        this.bPosStatus = bPosStatus;
        this.bNegStatus = bNegStatus;
        this.abPosStatus = abPosStatus;
        this.abNegStatus = abNegStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getNeedStatus() {
        return needStatus;
    }

    public void setNeedStatus(String needStatus) {
        this.needStatus = needStatus;
    }

    public String getOPosStatus() {
        return oPosStatus;
    }

    public void setOPosStatus(String oPosStatus) {
        this.oPosStatus = oPosStatus;
    }

    public String getONegStatus() {
        return oNegStatus;
    }

    public void setONegStatus(String oNegStatus) {
        this.oNegStatus = oNegStatus;
    }

    public String getAPosStatus() {
        return aPosStatus;
    }

    public void setAPosStatus(String aPosStatus) {
        this.aPosStatus = aPosStatus;
    }

    public String getANegStatus() {
        return aNegStatus;
    }

    public void setANegStatus(String aNegStatus) {
        this.aNegStatus = aNegStatus;
    }

    public String getBPosStatus() {
        return bPosStatus;
    }

    public void setBPosStatus(String bPosStatus) {
        this.bPosStatus = bPosStatus;
    }

    public String getBNegStatus() {
        return bNegStatus;
    }

    public void setBNegStatus(String bNegStatus) {
        this.bNegStatus = bNegStatus;
    }

    public String getAbPosStatus() {
        return abPosStatus;
    }

    public void setAbPosStatus(String abPosStatus) {
        this.abPosStatus = abPosStatus;
    }

    public String getAbNegStatus() {
        return abNegStatus;
    }

    public void setAbNegStatus(String abNegStatus) {
        this.abNegStatus = abNegStatus;
    }
}
