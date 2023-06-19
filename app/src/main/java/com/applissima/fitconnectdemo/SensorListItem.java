package com.applissima.fitconnectdemo;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by Franz Aufschläger on 02.08.2017.
 */

public class SensorListItem implements Serializable{

    private String devId;
    private String value1;
    private String value2;
    private String rssi;
    private boolean hasValue2;
    private int itemId;

    public SensorListItem(int itemId, String devId, String value1, String value2, String rssi, boolean hasValue2){
        this.itemId = itemId;
        set(devId, value1, value2, rssi, hasValue2);
    }

    public void set(String devId, String value1, String value2, String rssi, boolean hasValue2)
    {
        this.devId = String.format(Locale.getDefault(), "%05d", devId);
        this.value1 = value1;
        this.value2 = value2;
        this.rssi = rssi;
        this.hasValue2 = hasValue2;
    }

    public String getDeviceId(){
        return devId;
    }

    public String getValue1(){
        return  value1;
    }

    public String getValue2(){
        return value2;
    }

    public String getRssi(){
        return  rssi;
    }

    public int getId(){
        return itemId;
    }

    public boolean getHasValue2(){
        return hasValue2;
    }

}
