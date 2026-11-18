package ru.mospolytech.donorlog.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "donations")
public class DonationEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String userId;
    private String serverSyncId;
    private String componentType;
    private long donationDate;
    private int volumeMl;
    private String stationName;
    private double hemoglobin;
    private int systolicBp;
    private int diastolicBp;
    private int pulse;
    private boolean isGratuitous;
    private String notes;
    private boolean isSynced;
    private long updatedAt;

    public DonationEntity() {
    }

    @androidx.room.Ignore
    public DonationEntity(String componentType, long donationDate, int volumeMl,
                          String stationName, double hemoglobin, int systolicBp,
                          int diastolicBp, int pulse, boolean isGratuitous, String notes) {
        this.componentType = componentType;
        this.donationDate = donationDate;
        this.volumeMl = volumeMl;
        this.stationName = stationName;
        this.hemoglobin = hemoglobin;
        this.systolicBp = systolicBp;
        this.diastolicBp = diastolicBp;
        this.pulse = pulse;
        this.isGratuitous = isGratuitous;
        this.notes = notes;
        this.isSynced = false;
        this.updatedAt = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServerSyncId() {
        return serverSyncId;
    }

    public void setServerSyncId(String serverSyncId) {
        this.serverSyncId = serverSyncId;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public long getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(long donationDate) {
        this.donationDate = donationDate;
    }

    public int getVolumeMl() {
        return volumeMl;
    }

    public void setVolumeMl(int volumeMl) {
        this.volumeMl = volumeMl;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public double getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(double hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public int getSystolicBp() {
        return systolicBp;
    }

    public void setSystolicBp(int systolicBp) {
        this.systolicBp = systolicBp;
    }

    public int getDiastolicBp() {
        return diastolicBp;
    }

    public void setDiastolicBp(int diastolicBp) {
        this.diastolicBp = diastolicBp;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public boolean isGratuitous() {
        return isGratuitous;
    }

    public void setGratuitous(boolean gratuitous) {
        isGratuitous = gratuitous;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
