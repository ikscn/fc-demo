package com.applissima.fitconnectdemo;

import io.realm.RealmObject;

/**
 * Created by ilkerkuscan on 06/02/17.
 */

public class SettingsData extends RealmObject {

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
    private String clubEmail;
    private String clubPw;

    //private String appVersion;
    //private String hubMacAddress;

    public void setDefaultSettings(){
        this.simulationPersonCount = 0;
        this.checkProfileUpdates = 5;
        this.checkUnknownProfile = 5;
        this.checkTempBelts = 15;
        this.tempBeltsExp = 30;
        this.uploadMinAgg = 3;
        this.maxUploadData = 1000;
        this.uploadLogs = 10;
        this.distSensibility = -65;
        this.workSumPeriod = 1;
        this.workSumDuration = 15;
        this.zoneColors = 0;
        this.cardDistribution = "4|9|16|20";
        this.timeDiff = 0;
        this.lockAppForDevice = false;
        this.siteId = 0;
        this.locationId = 0;
        this.clubName = "";
        this.clubEmail = "";
        this.clubPw = "";
        //this.appVersion = "";
        //this.hubMacAddress = "";
    }

    public void updateDBFromSettings(){
        this.simulationPersonCount = Settings.simulationPersonCount;
        this.checkProfileUpdates = Settings.checkProfileUpdates;
        this.checkUnknownProfile = Settings.checkUnknownProfile;
        this.checkTempBelts = Settings.checkTempBelts;
        this.tempBeltsExp = Settings.tempBeltsExp;
        this.uploadMinAgg = Settings.uploadMinAgg;
        this.maxUploadData = Settings.maxUploadData;
        this.uploadLogs = Settings.uploadLogs;
        this.distSensibility = Settings.distSensibility;
        this.workSumPeriod = Settings.workSumPeriod;
        this.workSumDuration = Settings.workSumDuration;
        this.zoneColors = Settings.zoneColors;
        this.cardDistribution = Settings.cardDistribution;
        this.timeDiff = Settings.timeDiff;
        this.lockAppForDevice = Settings.lockAppForDevice;
        this.siteId = Settings.siteId;
        this.locationId = Settings.locationId;
        this.clubName = Settings.clubName;
        this.clubEmail = Settings.clubEmail;
        this.clubPw = Settings.clubPw;
        //this.appVersion = Settings.appVersion;
        //this.hubMacAddress = Settings.hubMacAddress;
    }

    public SettingsObj toObject(){
        SettingsObj obj = new SettingsObj();
        obj.setSimulationPersonCount(this.simulationPersonCount);
        obj.setCheckProfileUpdates(this.checkProfileUpdates);
        obj.setCheckUnknownProfile(this.checkUnknownProfile);
        obj.setCheckTempBelts(this.checkTempBelts);
        obj.setTempBeltsExp(this.tempBeltsExp);
        obj.setUploadMinAgg(this.uploadMinAgg);
        obj.setMaxUploadData(this.maxUploadData);
        obj.setUploadLogs(this.uploadLogs);
        obj.setDistSensibility(this.distSensibility);
        obj.setWorkSumPeriod(this.workSumPeriod);
        obj.setWorkSumDuration(this.workSumDuration);
        obj.setZoneColors(this.zoneColors);
        obj.setCardDistribution(this.cardDistribution);
        obj.setTimeDiff(this.timeDiff);
        obj.setLockAppForDevice(this.lockAppForDevice);
        obj.setSiteId(this.siteId);
        obj.setLocationId(this.locationId);
        obj.setClubName(this.clubName);
        return obj;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
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

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubEmail() {
        return clubEmail;
    }

    public void setClubEmail(String clubEmail) {
        this.clubEmail = clubEmail;
    }

    public String getClubPw() {
        return clubPw;
    }

    public void setClubPw(String clubPw) {
        this.clubPw = clubPw;
    }

    /*public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }*/

    /*public String getHubMacAddress() {
        return hubMacAddress;
    }

    public void setHubMacAddress(String hubMacAddress) {
        this.hubMacAddress = hubMacAddress;
    }*/
}
