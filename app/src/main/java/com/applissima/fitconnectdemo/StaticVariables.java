package com.applissima.fitconnectdemo;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ilkerkuscan on 25/04/17.
 */

public class StaticVariables {

    public static FsVersion availableVersion;
    public static int gridSize;
    public static List<String> waitingList;
    public static boolean isReboot;
    public static boolean isAntOn;
    public static String machineIP;
    public static int cntWifiOn;
    public static int cntInternetOn;
    public static int cntHubOn;
    public static String hubIpAddress;
    public static String machineIPOnHub;
    public static Map<String, int[]> networkStatus;
    public static int[] wifiStatus;
    public static int[] internetStatus;
    public static int[] hubStatus;
    public static float appHeight;

    public static void init(WeakReference<Context> weakContext){
        machineIP = AppUtils.getMachineIP(weakContext);
        waitingList = new ArrayList<String>();
        isReboot = false;
        isAntOn = false;
        hubIpAddress = "";
        machineIPOnHub = "";
        appHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        initNetworkCounts();
        initNetworkStatus();
    }

    public static void initNetworkCounts(){
        cntWifiOn = 0;
        cntInternetOn = 0;
        cntHubOn = 0;
    }

    public static void initNetworkStatus(){
        wifiStatus = new int[]{0,0};
        internetStatus = new int[]{0,0};
        hubStatus = new int[]{0,0};
        networkStatus = new HashMap<String, int[]>();
        networkStatus.put("wifi", wifiStatus);
        networkStatus.put("internet", internetStatus);
        networkStatus.put("hub", hubStatus);
    }

    public static void addWifiStatus(boolean isOn){
        String prop = "wifi";
        if(isOn) {
            cntWifiOn ++;
            networkStatus.get(prop)[0]++;
        } else {
            networkStatus.get(prop)[1]++;
        }
    }

    public static void addInternetStatus(boolean isOn){
        String prop = "internet";
        if(isOn) {
            cntInternetOn ++;
            networkStatus.get(prop)[0]++;
        } else {
            networkStatus.get(prop)[1]++;
        }
    }

    public static void addHubStatus(boolean isOn){
        String prop = "hub";
        if(isOn) {
            cntHubOn ++;
            networkStatus.get(prop)[0]++;
        } else {
            networkStatus.get(prop)[1]++;
        }
    }

    public static String getNetworkOnPerc(String prop){

        int countOn = networkStatus.get(prop)[0];
        int countOff = networkStatus.get(prop)[1];
        int totalCount = countOn + countOff;
        int percentageOn = totalCount == 0? 0 :Math.round((100 * countOn) / totalCount);
        return String.valueOf(percentageOn);

    }

    public static boolean isNetworkAvailable(){
        return (cntWifiOn>0 && cntInternetOn>0);
    }

    public static boolean isPersonInWaitingList(String hrSensorId){
        return waitingList.contains(hrSensorId);
    }

    public static void addPersonToWaitingList(String hrSensorId){
        if(!waitingList.contains(hrSensorId)) {
            waitingList.add(hrSensorId);
            Log.i("WaitingList", "Belt " + hrSensorId + " is added to waitingList");
        }
    }
}
