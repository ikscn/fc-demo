package com.applissima.fitconnectdemo;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ilkerkuscan on 30/01/17.
 *
 * ios: Activity
 *
 */

public class FitWork extends RealmObject {

    @PrimaryKey
    private String activityId; // UUID
    private FitWorkType activityType;
    private String activityName;
    private FitPerson owner;
    private Date createDate;
    private Date startDate;
    private Date finishDate;
    private int status = 0;
    private double totalCal = 0;
    private int avgHr = 0;
    private int avgPerf = 0;
    private int totalDistance = 0;
    private int avgSpeed = 0;
    private int avgCadence = 0;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public FitWorkType getActivityType() {
        return activityType;
    }

    public void setActivityType(FitWorkType activityType) {
        this.activityType = activityType;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public FitPerson getOwner() {
        return owner;
    }

    public void setOwner(FitPerson owner) {
        this.owner = owner;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getTotalCal() {
        return totalCal;
    }

    public void setTotalCal(double totalCal) {
        this.totalCal = totalCal;
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

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
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
}
