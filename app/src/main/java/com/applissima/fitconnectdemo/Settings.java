package com.applissima.fitconnectdemo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by ilkerkuscan on 17/02/17.
 */

public class Settings {

    public static int simulationPersonCount;
    public static int checkProfileUpdates; // minutes
    public static int checkUnknownProfile; // minutes
    public static int checkTempBelts; // minutes
    public static int tempBeltsExp; // minutes
    public static int uploadMinAgg; // minutes
    public static int maxUploadData; // count
    public static int uploadLogs; // minutes
    public static int distSensibility; // (1-10)
    public static int workSumPeriod; // minutes
    public static int workSumDuration; // seconds
    public static int zoneColors;
    public static String cardDistribution; // i.e. 4|9|16|20
    public static int timeDiff;
    public static boolean lockAppForDevice = false;
    public static int siteId;
    public static int locationId;
    public static List<String> hubList;
    public static String clubName;
    public static String clubEmail;
    public static String clubPw;

    public static void fromRealmDB(SettingsData data){
        simulationPersonCount = data.getSimulationPersonCount();
        checkProfileUpdates = data.getCheckProfileUpdates();
        checkUnknownProfile = data.getCheckUnknownProfile();
        checkTempBelts = data.getCheckTempBelts();
        tempBeltsExp = data.getTempBeltsExp();
        uploadMinAgg = data.getUploadMinAgg();
        maxUploadData = data.getMaxUploadData();
        uploadLogs = data.getUploadLogs();
        distSensibility = data.getDistSensibility();
        workSumPeriod = data.getWorkSumPeriod();
        workSumDuration = data.getWorkSumDuration();
        zoneColors = data.getZoneColors();
        cardDistribution = data.getCardDistribution();
        timeDiff = data.getTimeDiff();
        lockAppForDevice = data.isLockAppForDevice();
        siteId = data.getSiteId();
        locationId = data.getLocationId();
        clubName = data.getClubName();
        clubEmail = data.getClubEmail();
        clubPw = data.getClubPw();
    }

    public static boolean isJSONValid(String jsonString){

        boolean isValid = false;

        try {

            JSONObject jsonObject = new JSONObject(jsonString);
            if(jsonObject.has("simulationPersonCount")
                    && jsonObject.has("checkProfileUpdates")
                    && jsonObject.has("checkUnknownProfile")
                    && jsonObject.has("checkTempBelts")
                    && jsonObject.has("tempBeltsExp")
                    && jsonObject.has("uploadMinAgg")
                    && jsonObject.has("maxUploadData")
                    && jsonObject.has("uploadLogs")
                    && jsonObject.has("distSensibility")
                    && jsonObject.has("workSumPeriod")
                    && jsonObject.has("workSumDuration")
                    && jsonObject.has("zoneColors")
                    && jsonObject.has("cardDistribution")
                    && jsonObject.has("timeDiff")
                    && jsonObject.has("lockAppForDevice")
                    && jsonObject.has("siteId")
                    && jsonObject.has("locationId")
                    && jsonObject.has("clubName")
                    && jsonObject.has("clubEmail")
                    && jsonObject.has("clubPw")
               ){

                isValid = true;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isValid;
    }

    public static void fromJSONData(String jsonString){

        try {

            JSONObject jsonObject = new JSONObject(jsonString);
            simulationPersonCount = jsonObject.getInt("simulationPersonCount");
            checkProfileUpdates = jsonObject.getInt("checkProfileUpdates");
            checkUnknownProfile = jsonObject.getInt("checkUnknownProfile");
            checkTempBelts = jsonObject.getInt("checkTempBelts");
            tempBeltsExp = jsonObject.getInt("tempBeltsExp");
            uploadMinAgg = jsonObject.getInt("uploadMinAgg");
            maxUploadData = jsonObject.getInt("maxUploadData");
            uploadLogs = jsonObject.getInt("uploadLogs");
            distSensibility = jsonObject.getInt("distSensibility");
            workSumPeriod = jsonObject.getInt("workSumPeriod");
            workSumDuration = jsonObject.getInt("workSumDuration");
            zoneColors = jsonObject.getInt("zoneColors");
            cardDistribution = jsonObject.getString("cardDistribution");
            timeDiff = jsonObject.getInt("timeDiff");
            lockAppForDevice = jsonObject.getBoolean("lockAppForDevice");
            siteId = jsonObject.getInt("siteId");
            locationId = jsonObject.getInt("locationId");
            clubName = jsonObject.getString("clubName");
            clubEmail = jsonObject.getString("clubEmail");
            clubPw = jsonObject.getString("clubPw");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static String toJSONString(){

        String jsonString =
                "{"
                        +"\"simulationPersonCount\":" + simulationPersonCount
                        + ",\"checkProfileUpdates\":" + checkProfileUpdates
                        + ",\"checkUnknownProfile\":" + checkUnknownProfile
                        + ",\"checkTempBelts\":" + checkTempBelts
                        + ",\"tempBeltsExp\":" + tempBeltsExp
                        + ",\"uploadMinAgg\":" + uploadMinAgg
                        + ",\"maxUploadData\":" + maxUploadData
                        + ",\"uploadLogs\":" + uploadLogs
                        + ",\"distSensibility\":" + distSensibility
                        + ",\"workSumPeriod\":" + workSumPeriod
                        + ",\"workSumDuration\":" + workSumDuration
                        + ",\"zoneColors\":" + zoneColors
                        + ",\"cardDistribution\":\"" + cardDistribution + "\""
                        + ",\"timeDiff\":" + timeDiff
                        + ",\"lockAppForDevice\":" + lockAppForDevice
                        + ",\"siteId\":" + siteId
                        + ",\"locationId\":" + locationId
                        + ",\"clubName\":\"" + clubName + "\""
                        + ",\"clubEmail\":\"" + clubEmail + "\""
                        + ",\"clubPw\":\"" + clubPw + "\""
                        + "}";

        return jsonString;
    }

}
