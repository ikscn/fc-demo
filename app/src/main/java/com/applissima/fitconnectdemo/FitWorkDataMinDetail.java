package com.applissima.fitconnectdemo;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by ilkerkuscan on 30/01/17.
 *
 * ios: ActivityDataMinDetail
 *
 */

public class FitWorkDataMinDetail extends RealmObject {

    private FitWork fitWork;
    private FitPerson fitPerson;
    private Date insertDate;
    private int currentHr = 0;
    private int currentPerf = 0;
    private int currentZone = 0;
    private int currentSpeed = 0;
    private int currentCadence = 0;
    private int currentRssi = 0;

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
}
