package com.applissima.fitconnectdemo;

/**
 * Created by ilkerkuscan on 01/06/17.
 * Using GSON
 */

public class DataMinAggUpload  {

    private int avgSpeed;
    private int hrZone;
    private int avgHr;
    private int hrDataCount;
    private String beltId;
    private int siteId = Settings.siteId;
    private int maxHr;
    private int avgPerf;
    private String insertSourceId = "COLL" + String.valueOf(Settings.siteId) + "001";
    private int cadence;
    private String insertSourceType = "FcCollector";
    private int distance;
    private int scDataCount;
    private String userEmail = "etezel@gmail.com";
    private int pace;
    private double calBurned;
    private int avgHrRSSI;
    private int avgScRSSI;
    private String fctTimestamp;
    private String activity;

    public int getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(int avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public int getHrZone() {
        return hrZone;
    }

    public void setHrZone(int hrZone) {
        this.hrZone = hrZone;
    }

    public int getAvgHr() {
        return avgHr;
    }

    public void setAvgHr(int avgHr) {
        this.avgHr = avgHr;
    }

    public int getHrDataCount() {
        return hrDataCount;
    }

    public void setHrDataCount(int hrDataCount) {
        this.hrDataCount = hrDataCount;
    }

    public String getBeltId() {
        return beltId;
    }

    public void setBeltId(String beltId) {
        this.beltId = beltId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getMaxHr() {
        return maxHr;
    }

    public void setMaxHr(int maxHr) {
        this.maxHr = maxHr;
    }

    public int getAvgPerf() {
        return avgPerf;
    }

    public void setAvgPerf(int avgPerf) {
        this.avgPerf = avgPerf;
    }

    public String getInsertSourceId() {
        return insertSourceId;
    }

    public void setInsertSourceId(String insertSourceId) {
        this.insertSourceId = insertSourceId;
    }

    public int getCadence() {
        return cadence;
    }

    public void setCadence(int cadence) {
        this.cadence = cadence;
    }

    public String getInsertSourceType() {
        return insertSourceType;
    }

    public void setInsertSourceType(String insertSourceType) {
        this.insertSourceType = insertSourceType;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getScDataCount() {
        return scDataCount;
    }

    public void setScDataCount(int scDataCount) {
        this.scDataCount = scDataCount;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getPace() {
        return pace;
    }

    public void setPace(int pace) {
        this.pace = pace;
    }

    public double getCalBurned() {
        return calBurned;
    }

    public void setCalBurned(double calBurned) {
        this.calBurned = calBurned;
    }

    public int getAvgHrRSSI() {
        return avgHrRSSI;
    }

    public void setAvgHrRSSI(int avgHrRSSI) {
        this.avgHrRSSI = avgHrRSSI;
    }

    public int getAvgScRSSI() {
        return avgScRSSI;
    }

    public void setAvgScRSSI(int avgScRSSI) {
        this.avgScRSSI = avgScRSSI;
    }

    public String getFctTimestamp() {
        return fctTimestamp;
    }

    public void setFctTimestamp(String fctTimestamp) {
        this.fctTimestamp = fctTimestamp;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
