package com.applissima.fitconnectdemo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ilkerkuscan on 30/01/17.
 *
 * ios: ActivityDataMinAgg
 *
 */

public class FitWorkDataMinAgg extends RealmObject {

    @PrimaryKey
    private String minAggId;
    private FitWork fitWork;
    private FitPerson fitPerson;
    private Date insertDate;
    private Date uploadTryDate;
    private int uploadTryCount = 0;
    private Date uploadDate;
    private boolean uploadSuccessful = false;
    private double totalCal = 0;
    private int maxHr = 0;
    private int avgHr = 0;
    private int avgPerf = 0;
    private int avgZone = 0;
    private int avgSpeed = 0;
    private int avgCadence = 0;
    private int totalDistance = 0;
    private int hrDataCount = 0;

    public JSONObject toJSON(){

        JSONObject jsonObject = new JSONObject();

        int siteId = Settings.siteId;
        String beltId = "";
        String userEmail = "";
        FitPerson fitPerson = this.fitPerson;
        if(fitPerson!=null){
            beltId = fitPerson.getHrSensorId();
            userEmail = fitPerson.getUserName();
        }

        try {

            jsonObject.put("avgSpeed", this.avgSpeed);
            jsonObject.put("hrZone", this.avgZone);
            jsonObject.put("avgHr", this.avgHr);
            jsonObject.put("hrDataCount", this.hrDataCount);
            jsonObject.put("beltId", beltId);
            jsonObject.put("siteId", siteId);
            jsonObject.put("maxHr", this.maxHr);
            jsonObject.put("avgPerf", this.avgPerf);
            jsonObject.put("insertSourceId", "COLL" + String.valueOf(siteId) + "001");
            jsonObject.put("cadence", this.avgCadence);
            jsonObject.put("insertSourceType", "FcCollector");
            jsonObject.put("distance", this.totalDistance);
            jsonObject.put("scDataCount", 0);
            jsonObject.put("userEmail", userEmail);
            jsonObject.put("pace", 0);
            jsonObject.put("calBurned", this.totalCal);
            jsonObject.put("avgHrRSSI", 0);
            jsonObject.put("avgScRSSI", 0);
            jsonObject.put("fctTimestamp", AppUtils.getUploadDateString(this.insertDate));
            jsonObject.put("activity", this.getFitWork().getActivityName());

        } catch (JSONException e) {
            jsonObject = null;
            e.printStackTrace();
        }

        return jsonObject;

    }

    public String toJSONString(){

        int siteId = Settings.siteId;
        String beltId = "";
        FitPerson fitPerson = this.fitPerson;
        if(fitPerson!=null){
            beltId = fitPerson.getHrSensorId();
        }

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("avgSpeed", this.avgSpeed);
            jsonObject.put("hrZone", this.avgZone);
            jsonObject.put("avgHr", this.avgHr);
            jsonObject.put("hrDataCount", this.hrDataCount);
            jsonObject.put("beltId", beltId);
            jsonObject.put("siteId", siteId);
            jsonObject.put("maxHr", this.maxHr);
            jsonObject.put("avgPerf", this.avgPerf);
            jsonObject.put("insertSourceId", "COLL" + String.valueOf(siteId) + "001");
            jsonObject.put("cadence", this.avgCadence);
            jsonObject.put("insertSourceType", "FcCollector");
            jsonObject.put("distance", this.totalDistance);
            jsonObject.put("scDataCount", 0);
            jsonObject.put("userEmail", "etezel@gmail.com");
            jsonObject.put("pace", 0);
            jsonObject.put("calBurned", this.totalCal);
            jsonObject.put("avgHrRSSI", 0);
            jsonObject.put("avgScRSSI", 0);
            jsonObject.put("fctTimestamp", AppUtils.getUploadDateString(this.insertDate));
            jsonObject.put("activity", this.getFitWork().getActivityName());

        } catch (JSONException e) {
            jsonObject = null;
            e.printStackTrace();
        }

        return jsonObject==null? "" :jsonObject.toString();
    }

    public String getMinAggId() {
        return minAggId;
    }

    public void setMinAggId(String minAggId) {
        this.minAggId = minAggId;
    }

    public FitWork getFitWork() {
        return fitWork;
    }

    public void setFitWork(FitWork fitWork) {
        this.fitWork = fitWork;
    }

    public FitPerson getFitPerson() {
        return fitPerson;
    }

    public void setFitPerson(FitPerson fitPerson) {
        this.fitPerson = fitPerson;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public Date getUploadTryDate() {
        return uploadTryDate;
    }

    public void setUploadTryDate(Date uploadTryDate) {
        this.uploadTryDate = uploadTryDate;
    }

    public int getUploadTryCount() {
        return uploadTryCount;
    }

    public void setUploadTryCount(int uploadTryCount) {
        this.uploadTryCount = uploadTryCount;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public boolean isUploadSuccessful() {
        return uploadSuccessful;
    }

    public void setUploadSuccessful(boolean uploadSuccessful) {
        this.uploadSuccessful = uploadSuccessful;
    }

    public double getTotalCal() {
        return totalCal;
    }

    public void setTotalCal(double totalCal) {
        this.totalCal = totalCal;
    }

    public int getMaxHr() {
        return maxHr;
    }

    public void setMaxHr(int maxHr) {
        this.maxHr = maxHr;
    }

    public int getAvgHr() {
        return avgHr;
    }

    public void setAvgHr(int avgHr) {
        this.avgHr = avgHr;
    }

    public int getAvgPerf() {
        return avgPerf;
    }

    public void setAvgPerf(int avgPerf) {
        this.avgPerf = avgPerf;
    }

    public int getAvgZone() {
        return avgZone;
    }

    public void setAvgZone(int avgZone) {
        this.avgZone = avgZone;
    }

    public int getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(int avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public int getAvgCadence() {
        return avgCadence;
    }

    public void setAvgCadence(int avgCadence) {
        this.avgCadence = avgCadence;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getHrDataCount() {
        return hrDataCount;
    }

    public void setHrDataCount(int hrDataCount) {
        this.hrDataCount = hrDataCount;
    }
}
