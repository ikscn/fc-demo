package com.applissima.fitconnectdemo;

/**
 * Created by ilkerkuscan on 14/09/17.
 */

public class StatusData {

    private int personCount;
    private int activityPersonCount;
    private int activityDataMinAggCount;
    private int activityDataNotUploadedCount;
    private int logDbTotalCount;
    private int logDbNotUploadedCount;
    private long fitConRealmSize;
    private long logDbRealmSize;

    public String toString(){

        String dataString = "Database personCount: (" + this.personCount
                + "), activityPersonCount: (" + this.activityPersonCount
                + "), activityDataMinAggCount: (" + this.activityDataMinAggCount
                + "), logDbTotalCount: (" + this.logDbTotalCount
                + "), logDbNotUploadedCount: (" + this.logDbNotUploadedCount
                + "), logDbRealmSize: (" + AppUtils.readableByteCount(this.logDbRealmSize, false)
                + "), fitConRealmSize: (" + AppUtils.readableByteCount(this.fitConRealmSize, false)
                + ")";

        return dataString;

    }

    public int getPersonCount() {
        return personCount;
    }

    public void setPersonCount(int personCount) {
        this.personCount = personCount;
    }

    public int getActivityPersonCount() {
        return activityPersonCount;
    }

    public void setActivityPersonCount(int activityPersonCount) {
        this.activityPersonCount = activityPersonCount;
    }

    public int getActivityDataMinAggCount() {
        return activityDataMinAggCount;
    }

    public void setActivityDataMinAggCount(int activityDataMinAggCount) {
        this.activityDataMinAggCount = activityDataMinAggCount;
    }

    public int getActivityDataNotUploadedCount() {
        return activityDataNotUploadedCount;
    }

    public void setActivityDataNotUploadedCount(int activityDataNotUploadedCount) {
        this.activityDataNotUploadedCount = activityDataNotUploadedCount;
    }

    public int getLogDbTotalCount() {
        return logDbTotalCount;
    }

    public void setLogDbTotalCount(int logDbTotalCount) {
        this.logDbTotalCount = logDbTotalCount;
    }

    public int getLogDbNotUploadedCount() {
        return logDbNotUploadedCount;
    }

    public void setLogDbNotUploadedCount(int logDbNotUploadedCount) {
        this.logDbNotUploadedCount = logDbNotUploadedCount;
    }

    public long getFitConRealmSize() {
        return fitConRealmSize;
    }

    public void setFitConRealmSize(long fitConRealmSize) {
        this.fitConRealmSize = fitConRealmSize;
    }

    public long getLogDbRealmSize() {
        return logDbRealmSize;
    }

    public void setLogDbRealmSize(long logDbRealmSize) {
        this.logDbRealmSize = logDbRealmSize;
    }
}
