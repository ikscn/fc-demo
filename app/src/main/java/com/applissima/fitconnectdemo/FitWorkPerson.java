package com.applissima.fitconnectdemo;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by ilkerkuscan on 30/01/17.
 *
 * ios: ActivityPerson
 *
 */

public class FitWorkPerson extends RealmObject {

    private FitWork fitWork;
    private FitPerson fitPerson;

    private String actHrSensorId = "";
    private String actScSensorId = "";
    private int status = 0;
    private Date addedDate;

    private Date hrLastDataUpdateDate;
    private Date scLastDataUpdateDate;

    //current values
    private int currentHr = 0;
    private int currentPerf = 0;
    private int currentZone = 0;
    private int currentSpeed = 0;
    private int currentCadence = 0;
    private int currentRssi = 0;

    //aggregate values
    private double totalCal = 0;
    private int maxHr = 0;
    private int avgHr = 0;
    private int avgPerf = 0;
    private int avgSpeed = 0;
    private int avgCadence = 0;
    private int totalDistance = 0;

    public void clearFitWorkData(){
        this.setTotalCal(0);
        this.setTotalDistance(0);
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

    public String getActHrSensorId() {
        return actHrSensorId;
    }

    public void setActHrSensorId(String actHrSensorId) {
        this.actHrSensorId = actHrSensorId;
    }

    public String getActScSensorId() {
        return actScSensorId;
    }

    public void setActScSensorId(String actScSensorId) {
        this.actScSensorId = actScSensorId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public Date getHrLastDataUpdateDate() {
        return hrLastDataUpdateDate;
    }

    public void setHrLastDataUpdateDate(Date hrLastDataUpdateDate) {
        this.hrLastDataUpdateDate = hrLastDataUpdateDate;
    }

    public Date getScLastDataUpdateDate() {
        return scLastDataUpdateDate;
    }

    public void setScLastDataUpdateDate(Date scLastDataUpdateDate) {
        this.scLastDataUpdateDate = scLastDataUpdateDate;
    }

    public int getCurrentHr() {
        return currentHr;
    }

    public void setCurrentHr(int currentHr) {
        this.currentHr = currentHr;
    }

    public int getCurrentPerf() {
        return currentPerf;
    }

    public void setCurrentPerf(int currentPerf) {
        this.currentPerf = currentPerf;
    }

    public int getCurrentZone() {
        return currentZone;
    }

    public void setCurrentZone(int currentZone) {
        this.currentZone = currentZone;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(int currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public int getCurrentCadence() {
        return currentCadence;
    }

    public void setCurrentCadence(int currentCadence) {
        this.currentCadence = currentCadence;
    }

    public int getCurrentRssi() {
        return currentRssi;
    }

    public void setCurrentRssi(int currentRssi) {
        this.currentRssi = currentRssi;
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
}
