package com.applissima.fitconnectdemo;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ilkerkuscan on 09/02/17.
 */

public class RawData implements Serializable {

    private int heartRate;
    private String deviceNo;
    private int deviceType;
    private int rssi;
    private String hexString;

    public RawData(){

    }

    public RawData(int heartRate, String deviceNo, int deviceType, int rssi) {
        this.heartRate = heartRate;
        this.deviceNo = deviceNo;
        this.deviceType = deviceType;
        this.rssi = rssi;
    }

    public String getHexString() {
        return hexString;
    }

    public void setHexString(String hexString) {
        this.hexString = hexString;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
