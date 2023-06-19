package com.applissima.fitconnectdemo;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ilkerkuscan on 13/02/17.
 */

public class LogFitConnect extends RealmObject {

    @PrimaryKey
    private String id;
    private long logIndex;
    private Date logDate;
    private String logText;
    private int logType; // 0:AUTO  1:MANUAL

    public int getLogType() {
        return logType;
    }

    public void setLogType(int logType) {
        this.logType = logType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLogIndex() {
        return logIndex;
    }

    public void setLogIndex(long logIndex) {
        this.logIndex = logIndex;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }
}
