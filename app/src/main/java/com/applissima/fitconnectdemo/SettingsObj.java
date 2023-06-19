package com.applissima.fitconnectdemo;

/**
 * Created by ilkerkuscan on 31/10/17.
 */

public class SettingsObj {

    private int simulationPersonCount;
    private int checkProfileUpdates; // minutes
    private int checkUnknownProfile; // minutes
    private int checkTempBelts; // minutes
    private int tempBeltsExp; // minutes
    private int uploadMinAgg;
    private int maxUploadData;
    private int uploadLogs;
    private int distSensibility; // (1-10)
    private int workSumPeriod; // minutes
    private int workSumDuration; // minutes
    private int zoneColors;
    private String cardDistribution; // i.e. 4|9|16|20
    private int timeDiff;
    private boolean lockAppForDevice = false;
    private int siteId;     // Cihaz ID
    private int locationId; // Lokasyon ID
    private String clubName;

    public SettingsObj() {
    }

    public int getSimulationPersonCount() {
        return simulationPersonCount;
    }

    public void setSimulationPersonCount(int simulationPersonCount) {
        this.simulationPersonCount = simulationPersonCount;
    }

    public int getCheckProfileUpdates() {
        return checkProfileUpdates;
    }

    public void setCheckProfileUpdates(int checkProfileUpdates) {
        this.checkProfileUpdates = checkProfileUpdates;
    }

    public int getCheckUnknownProfile() {
        return checkUnknownProfile;
    }

    public void setCheckUnknownProfile(int checkUnknownProfile) {
        this.checkUnknownProfile = checkUnknownProfile;
    }

    public int getCheckTempBelts() {
        return checkTempBelts;
    }

    public void setCheckTempBelts(int checkTempBelts) {
        this.checkTempBelts = checkTempBelts;
    }

    public int getTempBeltsExp() {
        return tempBeltsExp;
    }

    public void setTempBeltsExp(int tempBeltsExp) {
        this.tempBeltsExp = tempBeltsExp;
    }

    public int getUploadMinAgg() {
        return uploadMinAgg;
    }

    public void setUploadMinAgg(int uploadMinAgg) {
        this.uploadMinAgg = uploadMinAgg;
    }

    public int getMaxUploadData() {
        return maxUploadData;
    }

    public void setMaxUploadData(int maxUploadData) {
        this.maxUploadData = maxUploadData;
    }

    public int getUploadLogs() {
        return uploadLogs;
    }

    public void setUploadLogs(int uploadLogs) {
        this.uploadLogs = uploadLogs;
    }

    public int getDistSensibility() {
        return distSensibility;
    }

    public void setDistSensibility(int distSensibility) {
        this.distSensibility = distSensibility;
    }

    public int getWorkSumPeriod() {
        return workSumPeriod;
    }

    public void setWorkSumPeriod(int workSumPeriod) {
        this.workSumPeriod = workSumPeriod;
    }

    public int getWorkSumDuration() {
        return workSumDuration;
    }

    public void setWorkSumDuration(int workSumDuration) {
        this.workSumDuration = workSumDuration;
    }

    public int getZoneColors() {
        return zoneColors;
    }

    public void setZoneColors(int zoneColors) {
        this.zoneColors = zoneColors;
    }

    public String getCardDistribution() {
        return cardDistribution;
    }

    public void setCardDistribution(String cardDistribution) {
        this.cardDistribution = cardDistribution;
    }

    public int getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(int timeDiff) {
        this.timeDiff = timeDiff;
    }

    public boolean isLockAppForDevice() {
        return lockAppForDevice;
    }

    public void setLockAppForDevice(boolean lockAppForDevice) {
        this.lockAppForDevice = lockAppForDevice;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }
}
