package com.applissima.fitconnectdemo;
/*
import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

*//**
 * Created by ilkerkuscan on 22/02/17.
 *//*

public class DatabaseServiceOld {

    private static final String clsName = "DatabaseService";
    //private Realm mRealm;
    //private Realm logRealm;
    public static RequestQueue queue;
    private static List<String> personsToUpdate;
    private static List<String> summaryDisplayedList;
    private static Context mContext;
    private static FitWorkActivity fwActivity;

    public DatabaseService(FitWorkActivity activity){

        fwActivity = activity;
        mContext = fwActivity.getApplicationContext();

        //mRealm = Realm.getDefaultInstance();
        queue = Volley.newRequestQueue(mContext);

        summaryDisplayedList = new ArrayList<String>();

    }

    public static void init(FitWorkActivity activity){

        fwActivity = activity;
        mContext = fwActivity.getApplicationContext();

        //mRealm = Realm.getDefaultInstance();
        queue = Volley.newRequestQueue(mContext);

        summaryDisplayedList = new ArrayList<String>();

    }


    public void createInitWork(final String hrSensorId){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Log.i("FitWorkPerson Adding", "FitWorkPerson is adding...");

                    FitWorkPerson fitWorkPerson = realm.createObject(FitWorkPerson.class);
                    fitWorkPerson.setFitPerson(realm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).findFirst());
                    fitWorkPerson.setAddedDate(Calendar.getInstance().getTime());
                    fitWorkPerson.setCurrentHr(0);
                    fitWorkPerson.setCurrentPerf(0);
                    fitWorkPerson.setCurrentZone(0);
                    fitWorkPerson.setCurrentRssi(0);
                    fitWorkPerson.setTotalCal(0);
                    fitWorkPerson.setActHrSensorId(hrSensorId);
                    fitWorkPerson.setHrLastDataUpdateDate(Calendar.getInstance().getTime());
                    Log.i("FitWorkPerson ADDED", "FitWorkPerson ADDED!!");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

    }

    public void updateWork(final String hrSensorId){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            final FitPerson fitPerson = mRealm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).findFirst();
            final FitWorkPerson fitWorkPerson = mRealm.where(FitWorkPerson.class).equalTo("actHrSensorId", hrSensorId).findFirst();

            final RawData lastData = StaticVariables.getLastRawData(hrSensorId);

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    if(lastData.getHeartRate() > fitPerson.getMaxHr()){
                        fitPerson.setMaxHr(lastData.getHeartRate());
                    }

                    fitWorkPerson.setCurrentHr(lastData.getHeartRate());
                    fitWorkPerson.setCurrentPerf(calcPerf(lastData.getHeartRate(), fitPerson));
                    fitWorkPerson.setCurrentZone(calcZone(lastData.getHeartRate(), fitPerson));
                    fitWorkPerson.setCurrentRssi(lastData.getRssi());
                    fitWorkPerson.setHrLastDataUpdateDate(Calendar.getInstance().getTime());

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

    }

    public static void compactRealms(){

        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name("fitConnect.realm")
                .build();

        Realm.compactRealm(config);

        RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                .build();

        Realm.compactRealm(logConfig);

    }


    public void refreshRealtimeData(){

        final FitPerson[] userInLocal = new FitPerson[1];
        final FitWorkPerson[] userInActivity = new FitWorkPerson[1];

        Realm mRealm = Realm.getDefaultInstance();

        try {

            List<String> activeBeltList = StaticVariables.getCurUDPList();

            for (int i=0; i < activeBeltList.size(); i++) {

                final String activeBeltId = activeBeltList.get(i);

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        userInLocal[0] = realm.where(FitPerson.class).equalTo("hrSensorId", activeBeltId).findFirst();
                    }
                });

                // Is user in local DB?
                if(userInLocal[0]==null){
                    updateOrAddLocalUserData(activeBeltId, false);
                } else {

                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            userInActivity[0] = realm.where(FitWorkPerson.class).equalTo("actHrSensorId", activeBeltId).findFirst();
                        }
                    });

                    if(userInActivity[0] == null) {
                        createInitWork(activeBeltId);
                    } else {
                        updateWork(activeBeltId);
                    }
                }

            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            mRealm.closeDialog();
        }

    }

    public void generateData() {

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {

                    for (FitWorkPerson workPerson : bgRealm.where(FitWorkPerson.class).isNotNull("fitPerson").findAll()) {

                        RawData rawData = null;

                        // If a real person with a belt (=not test user)
                        if (!workPerson.getFitPerson().isTestUser() || workPerson.getActHrSensorId().startsWith("1")) {

                            rawData = StaticVariables.getLastRawData(workPerson.getActHrSensorId());

                            if (rawData != null) {
                                workPerson.setCurrentHr(rawData.getHeartRate());
                                workPerson.setCurrentPerf(calcPerf(rawData.getHeartRate(), workPerson.getFitPerson()));
                                workPerson.setCurrentZone(calcZone(rawData.getHeartRate(), workPerson.getFitPerson()));
                                workPerson.setCurrentRssi(rawData.getRssi());
                                workPerson.setHrLastDataUpdateDate(Calendar.getInstance().getTime());
                                //workPerson.setTotalCal(workPerson.getTotalCal()+1);
                                if (workPerson.getCurrentHr() > workPerson.getFitPerson().getMaxHr()) {
                                    workPerson.getFitPerson().setMaxHr(workPerson.getCurrentHr());
                                }
                                //Log.i("RAWDATA", "RAWDATA>> " + rawData.getHeartRate());
                                //Log.i("RAWDATA", "RAWDATA Last Update Date>> " + AppUtils.getDateString(workPerson.getHrLastDataUpdateDate()));
                            } else {
                                //Log.i("RAWDATA", "RAWDATA IS NULL>> ");
                                //Log.i("RAWDATA", "RAWDATA Last Update Date>> " + AppUtils.getDateString(workPerson.getHrLastDataUpdateDate()));
                            }

                        } else {
                            // Generate data for test persons (=simulate)
                            rawData = generateRandomRawData();
                            rawData.setDeviceNo(workPerson.getActHrSensorId());
                            workPerson.setCurrentHr(rawData.getHeartRate()); // btw 80-160
                            workPerson.setCurrentRssi(rawData.getRssi());
                            workPerson.setCurrentPerf(calcPerf(workPerson.getCurrentHr(), workPerson.getFitPerson()));
                            workPerson.setTotalCal(workPerson.getTotalCal() + 1);
                            if (workPerson.getCurrentHr() > workPerson.getFitPerson().getMaxHr()) {
                                workPerson.getFitPerson().setMaxHr(workPerson.getCurrentHr());
                            }
                            workPerson.setCurrentZone(calcZone(workPerson.getCurrentHr(), workPerson.getFitPerson()));
                            workPerson.setHrLastDataUpdateDate(Calendar.getInstance().getTime());

                            // ChartTest
                            *//**//*if (workPerson.getActHrSensorId().equalsIgnoreCase("90002")) {
                                testDataList.add(rawData);
                            }*//**//*
                        }

                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

        //updateUI();

    }


    public void checkBelts(){

        // Checks if a new person attended to the activity
        try {

            List<String> beltList = new ArrayList<>(StaticVariables.dataMap.keySet());

            if (beltList == null || beltList.size() == 0) {
                //Log.i("Check Belts", "Check Belts List NULL");
            } else {

                final String beltIds[] = new String[1];

                //Log.i("Check Belts", "Check Belts List: " + beltList.get(0));
                for (String beltId : beltList) {

                    beltIds[0] = beltId;
                    //final RawData lastRawData = udpService.getLastRawData(bid);
                    final FitPerson[] fitPersonArray = new FitPerson[1];
                    final FitWorkPerson[] fitWorkPersonArray = new FitWorkPerson[1];

                    Realm mRealm = Realm.getDefaultInstance();

                    try {

                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                fitPersonArray[0] = realm.where(FitPerson.class).equalTo("hrSensorId", beltIds[0]).findFirst();
                                fitWorkPersonArray[0] = realm.where(FitWorkPerson.class).equalTo("actHrSensorId", beltIds[0]).findFirst();

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerService.insertLog(clsName, e.getMessage(),
                                e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
                    } finally {
                        mRealm.closeDialog();
                    }

                    if (fitPersonArray[0] == null) {
                        updateOrAddLocalUserData(beltIds[0], false);
                    } else {
                        if (fitWorkPersonArray[0] == null) {
                            createInitWork(beltIds[0]);
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        }

    }



    private static int calcZone(int heartRate, int maxHr){

        int zone1 = (int) ((double) maxHr * 0.5);
        int zone2 = (int) ((double) maxHr * 0.6);
        int zone3 = (int) ((double) maxHr * 0.7);
        int zone4 = (int) ((double) maxHr * 0.8);
        int zone5 = (int) ((double) maxHr * 0.9);

        if(heartRate < zone2){
            return 1;
        } else if(heartRate >= zone2 && heartRate < zone3){
            return 2;
        } else if(heartRate >= zone3 && heartRate < zone4){
            return 3;
        } else if(heartRate >= zone4 && heartRate < zone5){
            return 4;
        } else if(heartRate >= zone5){
            return 5;
        } else {
            return 0;
        }

    }

    private static int calcPerf(int heartRate, int maxHr){
        return (int) Math.round((double)heartRate / (double) maxHr * 100);
    }

    public static RawData generateRandomRawData(){

        Random rand = new Random();
        int heartRate = rand.nextInt(101) + 60; // between 60 & 160
        int deviceType = 1;
        int rssi = rand.nextInt(100) - 50; // between -50 & 50

        RawData data = new RawData(heartRate, null, deviceType, rssi);

        return data;
    }

    public static void addNewTestPerson(){

        Settings.simulationPersonCount++;

    }

    public static void removeLastTestPerson(){

        if(Settings.simulationPersonCount>0) {
            Settings.simulationPersonCount--;
        }

    }

    public void addNewTestPersonOld(){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    long listSize = realm.where(FitWorkPerson.class).isNotNull("fitPerson").count();

                    Random rand = new Random();
                    RawData randomData = generateRandomRawData();

                    // Person Data
                    FitPerson newPerson = realm.createObject(FitPerson.class, UUID.randomUUID().toString());
                    newPerson.setEnableBelt(true);
                    newPerson.setHrSensorId("1XXX" + String.valueOf(listSize + 1));
                    newPerson.setGender(rand.nextInt(1) + 1);
                    newPerson.setNickName("Person " + String.valueOf(listSize + 1));
                    newPerson.setHeight(rand.nextInt(196 - 150) + 150); // btw 150-195
                    newPerson.setAge(rand.nextInt(66 - 18) + 18);     // btw 18-65
                    newPerson.setTestUser(true);
                    newPerson.setLastUpdateDate(new Date());
                    newPerson.setMaxHr(randomData.getHeartRate());

                    // Work Data
                    FitWorkPerson newWorkPerson = realm.createObject(FitWorkPerson.class);
                    newWorkPerson.setFitPerson(newPerson);
                    newWorkPerson.setActHrSensorId(newPerson.getHrSensorId());
                    newWorkPerson.setAddedDate(Calendar.getInstance().getTime());
                    newWorkPerson.setCurrentHr(randomData.getHeartRate());
                    newWorkPerson.setCurrentPerf(calcPerf(randomData.getHeartRate(), newPerson.getMaxHr()));
                    newWorkPerson.setTotalCal(0);
                    newWorkPerson.setCurrentZone(calcZone(randomData.getHeartRate(), newPerson.getMaxHr()));
                    newWorkPerson.setHrLastDataUpdateDate(Calendar.getInstance().getTime());

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

    }

    public void removeLastTestPersonOld(){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            // remove a single object
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    FitWorkPerson deletingWork =
                            realm.where(FitWorkPerson.class)
                                    .isNotNull("fitPerson")
                                    .equalTo("fitPerson.testUser", true)
                                    .like("actHrSensorId", "XX")
                                    .findAllSorted("addedDate", Sort.DESCENDING)
                                    .first();

                    if (deletingWork != null) {

                        FitPerson deletingPerson =
                                realm.where(FitPerson.class)
                                        .equalTo("hrSensorId", deletingWork.getActHrSensorId())
                                        .findFirst();

                        //fitWorkPersons.remove(deletingWork);
                        deletingWork.deleteFromRealm();
                        deletingPerson.deleteFromRealm();

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

    }


    public static void updateOrAddLocalUserData(final String hrSensorId, final boolean isTestUser, final String hexStr) {

        //StaticVariables.addPersonToProcess(hrSensorId);

        String encryptedString = "";
        String requestString = "";

        String url = AppDefaults.API_RETRIEVE_URL;

        SimpleDateFormat df = new SimpleDateFormat(AppDefaults.TIME_FORMAT_DEFAULT);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        Log.i("UTCTime", df.format(calendar.getTime()));

        requestString = AppDefaults.SCRT_KEY + df.format(calendar.getTime()).substring(0, 15) + AppDefaults.API_RETRIEVE_KEY + AppDefaults.API_PASS;

        encryptedString = AppUtils.md5(requestString);
        Log.i("Encrypted SessionId", "Encrypted SessionId: " + encryptedString);

        Map<String, String> params = new HashMap<>();
        params.put("collectorEmail", AppDefaults.COLLECTOR_EMAIL);
        params.put("email", hrSensorId);
        params.put("sessionid", encryptedString);

        APIRequest request = new APIRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if(response!=null) {

                    // Display the first 500 characters of the response string.
                    Log.i("Response", response.toString());
                    try {

                        if (response.getJSONObject("status").getBoolean("successful")) {

                            final JSONObject jsonResponse = response;

                            try {

                                updatePerson(hrSensorId, isTestUser, jsonResponse);

                            } catch (Exception e){
                                e.printStackTrace();
                                LoggerService.insertLog(clsName, e.getLocalizedMessage(), e.getLocalizedMessage());
                            }


                        } else {
                            // User data could not found, add to waitinglist
                            //if (!isTestUser) {
                                StaticVariables.addPersonToWaitingList(hrSensorId);
                                Log.i("User data", "User data checked with web, no user found - 1: HrSensorId: " + hrSensorId);
                                LoggerService.insertLog(clsName, "User data checked with web, no user found - 1",
                                        "HrSensorId: " + hrSensorId
                                        + " HexString: " + hexStr);
                            //}
                        }

                    } catch (JSONException e) {
                        LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                                + ":" + e.getStackTrace()[0].getLineNumber());
                        e.printStackTrace();
                    } catch (Exception e) {
                        LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                                + ":" + e.getStackTrace()[0].getLineNumber());
                        e.printStackTrace();
                    }
                } else {
                    // Response is null
                    Log.i("API Response Error", "API Response is null.");
                }

                //StaticVariables.removePersonFromProcess(hrSensorId);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorStr = AppUtils.getVolleyErrorMessage(error);

                if(error.getMessage()!=null) {
                    Log.i("Response Error", error.getMessage());
                } else if(error.getLocalizedMessage()!=null){
                    Log.i("Response Error", error.getLocalizedMessage());
                } else {
                    Log.i("Response Error", "Volley API Response Error");
                }
                LoggerService.insertLog(clsName, "Volley API Response Error" , errorStr);
                // User data could not found, add to waitinglist
                //if(!isTestUser) {
                    if(!StaticVariables.isPersonInWaitingList(hrSensorId)){
                        Log.i("User data", "User data checked with web, no user found - 1: HrSensorId: " + hrSensorId);
                        LoggerService.insertLog(clsName, "User data checked with web, no user found - 1",
                                "HrSensorId: " + hrSensorId
                                        + " HexString: " + hexStr);
                        StaticVariables.addPersonToWaitingList(hrSensorId);
                    }

                //}

                //StaticVariables.removePersonFromProcess(hrSensorId);
            }

        });

        request.setShouldCache(false);

        queue.add(request);

    }

    private static void updatePerson(final String hrSensorId, final boolean isTestUser, final JSONObject jsonResponse){

        Realm mRealm = Realm.getDefaultInstance();

        //final String[] photoUrls = new String[1];

        try {

            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    FitPerson personToUpdate = realm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).findFirst();

                    //Log.i("PersonToUpdate", "PersonToUpdate: " + (personToUpdate == null ? "Null Person" : personToUpdate.getFullName()));

                    if (personToUpdate == null) {
                        personToUpdate = realm.createObject(FitPerson.class, UUID.randomUUID().toString());
                        Log.i("Person Added", "Person Added to Local, beltId: " + hrSensorId);
                        LoggerService.insertLog(clsName,
                                "New Person added to Local, beltId: " + hrSensorId, null);
                    } else {
                        Log.i("Person Updated", "Person Updated in Local, beltId: " + personToUpdate.getHrSensorId());
                    }
                    personToUpdate.setTestUser(isTestUser);
                    personToUpdate.fromJSON(jsonResponse);
                    //photoUrls[0] = personToUpdate.getPhotoURL();

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {

                    //updatePersonPhoto(hrSensorId);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(),
                    e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
            //StaticVariables.removePersonFromProcess(hrSensorId);
        }

        //return photoUrls.length==1?photoUrls[0] :null;

    }

    private void updatePersonPhoto(final String hrSensorId){

        final Realm mRealm = Realm.getDefaultInstance();

        try {

            FitPerson personToUpdate = mRealm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).findFirst();
            String photoUrl = personToUpdate.getPhotoURL();

            if(photoUrl!=null && !"".equalsIgnoreCase(photoUrl)) {

                ImageRequest ir = new ImageRequest(photoUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(final Bitmap response) {

                        if (response != null) {

                            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            response.compress(Bitmap.CompressFormat.JPEG, 50, stream);

                            Log.i("Person Image OK", "Downloaded " + String.valueOf(stream.size()) + " bytes.");

                            try {

                                mRealm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        FitPerson personToUpdate = realm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).findFirst();

                                        personToUpdate.setPhoto(stream.toByteArray());
                                        personToUpdate.setLastUpdateDate(Calendar.getInstance().getTime());
                                        //realm.copyToRealmOrUpdate(personToUpdate);
                                        //Log.i("Person Image OK", "Downloaded " + String.valueOf(personToUpdate.getPhoto().length) + " bytes.");
                                        //stream.reset();
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                                        + ":" + e.getStackTrace()[0].getLineNumber());
                            } finally {
                                try {
                                    stream.closeDialog();
                                } catch (IOException e) {
                                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                                            + ":" + e.getStackTrace()[0].getLineNumber());
                                    e.printStackTrace();
                                }
                            }


                        }

                    }
                }, 0, 0, ImageView.ScaleType.FIT_CENTER, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.getMessage() != null) {
                            Log.i("Image Download Error", error.getMessage());
                        } else if (error.getLocalizedMessage() != null) {
                            Log.i("Image Download Error", error.getLocalizedMessage());
                        } else {
                            Log.i("Image Download Error", "Volley API Image Download Error");
                        }
                        LoggerService.insertLog(clsName, "Volley API Image Download Error", "Volley API Image Download Error");
                    }
                });
                queue.add(ir);

            }

        } catch (Exception e) {
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        }

        *//**//*try {

            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    FitPerson personToUpdate = realm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).findFirst();

                    personToUpdate.setPhoto(stream.toByteArray());
                    personToUpdate.setLastUpdateDate(Calendar.getInstance().getTime());
                    //realm.copyToRealmOrUpdate(personToUpdate);
                    //Log.i("Person Image OK", "Downloaded " + String.valueOf(personToUpdate.getPhoto().length) + " bytes.");
                    //stream.reset();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(),
                    e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }*//**//*
    }

    public static void updatePersonInfo(){

        personsToUpdate = new ArrayList<String>();

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkDataMinDetail> minDetails = realm.where(FitWorkDataMinDetail.class).findAll();

                    if (minDetails != null && minDetails.size() > 0) {

                        for (FitWorkDataMinDetail minDetail : minDetails) {

                            String hrSensorId = minDetail.getFitPerson().getHrSensorId();
                            if(!personsToUpdate.contains(hrSensorId)){
                                personsToUpdate.add(hrSensorId);
                                Log.i("PersonsToUpdate", "PersonsToUpdate: " + hrSensorId);
                            }

                        }

                    } else {
                        Log.i("PersonsToUpdate", "PersonsToUpdate: List is null.");
                    }
                }
            });

            for (String hrSensorId : personsToUpdate) {
                Log.i("PersonInfo Update", "PersonInfo Update requested... BeltId: " + hrSensorId);
                updateOrAddLocalUserData(hrSensorId, false, "");
            }

            personsToUpdate = null;

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            personsToUpdate = null;
        } finally {
            mRealm.closeDialog();
        }

    }

    public void updatePersonInfo(){

        Realm mRealm = Realm.getDefaultInstance();

        personsToUpdate = new ArrayList<String>();

        try {

            final Calendar timeOut = Calendar.getInstance();
            timeOut.add(Calendar.SECOND, -60 * 2);

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkPerson> results = realm.where(FitWorkPerson.class)
                            .isNotNull("fitPerson")
                            .beginGroup()
                            .equalTo("fitPerson.testUser", false)
                            .or()
                            .beginsWith("actHrSensorId", "1")
                            .endGroup()
                            .greaterThan("hrLastDataUpdateDate", timeOut.getTime())
                            .findAll();

                    if (results != null && results.size() > 0) {
                        for (FitWorkPerson workPerson : results) {
                            personsToUpdate.add(workPerson.getActHrSensorId());
                            Log.i("PersonsToUpdate", "PersonsToUpdate: " + workPerson.getActHrSensorId());
                        }

                        for (String hrSensorId : personsToUpdate) {
                            Log.i("PersonInfo Update", "PersonInfo Update requested... BeltId: " + hrSensorId);
                            updateOrAddLocalUserData(hrSensorId, false);
                        }

                    } else {
                        Log.i("PersonsToUpdate", "PersonsToUpdate: List is null.");
                    }
                }
            });

            personsToUpdate = null;

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
            personsToUpdate = null;
        } finally {
            mRealm.closeDialog();
        }

    }

    public static void uploadLogs(){

        final RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                .build();

        Realm logRealm = Realm.getInstance(logConfig);

        try {

            logRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<LogData> logResults =
                            realm.where(LogData.class).equalTo("uploadSuccessful", false).findAll();

                    if (logResults != null && logResults.size() > 0) {

                        for (LogData logData : logResults) {
                            logData.setUploadTryCount(logData.getUploadTryCount() + 1);
                            logData.setUploadTryDate(Calendar.getInstance().getTime());
                        }

                        String jsonString = "";

                        for (int i = 0; i < logResults.size(); i++) {

                            LogData logData = logResults.get(i);
                            if (i == 0) {
                                jsonString += "[" + logData.toJSONString();
                                ;
                            } else {
                                jsonString += "," + logData.toJSONString();
                            }
                        }
                        jsonString += "]";

                        Log.i("UPLOADING LOG", "UPLOADING LOG JSON: " + jsonString);

                        String encryptedString = "";
                        String requestString = "";

                        SimpleDateFormat df = new SimpleDateFormat(AppDefaults.TIME_FORMAT_DEFAULT);
                        df.setTimeZone(TimeZone.getTimeZone("UTC"));
                        TimeZone timeZone = TimeZone.getTimeZone("UTC");
                        Calendar calendar = Calendar.getInstance(timeZone);
                        Log.i("UTCTime", df.format(calendar.getTime()));

                        requestString = AppDefaults.SCRT_KEY + df.format(calendar.getTime()).substring(0, 15)
                                + AppDefaults.API_INSERTLOGDATA_KEY + AppDefaults.API_PASS;

                        encryptedString = AppUtils.md5(requestString);
                        Log.i("Encrypted SessionId", encryptedString);

                        // Instantiate the RequestQueue.
                        String url = AppDefaults.API_INSERTLOGDATA_URL;

                        Map<String, String> params = new HashMap<>();
                        params.put("email", "etezel@gmail.com");
                        params.put("sessionid", encryptedString);
                        params.put("logData", jsonString);

                        APIRequest request = new APIRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                // Display the first 500 characters of the response string.
                                try {

                                    if (response.getJSONObject("status").getBoolean("successful")) {

                                        saveLogUpdates();

                                    } else {
                                        Log.i("UPLOAD LOG Response", "UPLOAD Response Failed" + response.toString());
                                    }

                                } catch (JSONException e) {
                                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                                            + ":" + e.getStackTrace()[0].getLineNumber());
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.getMessage() != null) {
                                    Log.i("UPLOAD Response Error", error.getMessage());
                                } else if (error.getLocalizedMessage() != null) {
                                    Log.i("UPLOAD Response Error", error.getLocalizedMessage());
                                } else {
                                    Log.i("UPLOAD Response Error", "Volley API UPLOAD Response Error");
                                }
                                LoggerService.insertLog(clsName, "Volley API UPLOAD Response Error", "Volley API UPLOAD Response Error");

                            }
                        });

                        queue.add(request);

                    } else {
                        Log.i("LogData", "LOGDATA results is null.");
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            logRealm.closeDialog();
        }

    }


    public static String getLogDataToUpload(){

        final RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                .build();

        Realm logRealm = Realm.getInstance(logConfig);

        final String[] jsonString = new String[1];
        jsonString[0] = "";


        try {

            logRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<LogData> logResults =
                            realm.where(LogData.class).equalTo("uploadSuccessful", false).findAll();

                    if (logResults != null && logResults.size() > 0) {

                        for (LogData logData : logResults) {
                            logData.setUploadTryCount(logData.getUploadTryCount() + 1);
                            logData.setUploadTryDate(Calendar.getInstance().getTime());
                        }

                        for (int i = 0; i < logResults.size(); i++) {

                            LogData logData = logResults.get(i);
                            if (i == 0) {
                                jsonString[0] += "[" + logData.toJSONString();
                                ;
                            } else {
                                jsonString[0] += "," + logData.toJSONString();
                            }
                        }
                        jsonString[0] += "]";

                    } else {
                        Log.i("LogData", "LOGDATA results is null.");
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            logRealm.closeDialog();
        }

        return jsonString[0];

    }

    public static void saveLogUpdates(){

        final RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                .build();

        Realm logRealm = Realm.getInstance(logConfig);

        try {

            logRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<LogData> uploadedLogResults = realm.where(LogData.class)
                            .equalTo("uploadSuccessful", false)
                            .greaterThan("uploadTryCount", 0)
                            .findAll();

                    for (LogData uploadedLogData : uploadedLogResults) {

                        uploadedLogData.setUploadSuccessful(true);
                        uploadedLogData.setUploadDate(uploadedLogData.getUploadTryDate());

                    }
                }
            });

            Log.i("UPLOAD LOG Response", "UPLOAD LOG Response Successful");

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            logRealm.closeDialog();
        }

    }

    public static String getMinAggDataToUpload(){

        final String[] jsonString = new String[1];
        jsonString[0] = "";

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkDataMinAgg> results =
                            realm.where(FitWorkDataMinAgg.class).equalTo("uploadSuccessful", false).findAll();

                    if (results != null && results.size() > 0) {

                        for (FitWorkDataMinAgg minAgg : results) {
                            minAgg.setUploadTryCount(minAgg.getUploadTryCount() + 1);
                            minAgg.setUploadTryDate(Calendar.getInstance().getTime());
                        }

                        for (int i = 0; i < results.size(); i++) {

                            FitWorkDataMinAgg dataMinAgg = results.get(i);
                            if (i == 0) {
                                jsonString[0] += "[" + dataMinAgg.toJSONString();
                                ;
                            } else {
                                jsonString[0] += "," + dataMinAgg.toJSONString();
                            }
                        }
                        jsonString[0] += "]";

                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

        return jsonString[0];

    }

    public static void uploadMinDataAgg(){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkDataMinAgg> results =
                            realm.where(FitWorkDataMinAgg.class).equalTo("uploadSuccessful", false).findAll();

                    if (results != null && results.size() > 0) {

                        for (FitWorkDataMinAgg minAgg : results) {
                            minAgg.setUploadTryCount(minAgg.getUploadTryCount() + 1);
                            minAgg.setUploadTryDate(Calendar.getInstance().getTime());
                        }

                        String jsonString = "";

                        for (int i = 0; i < results.size(); i++) {

                            FitWorkDataMinAgg dataMinAgg = results.get(i);
                            if (i == 0) {
                                jsonString += "[" + dataMinAgg.toJSONString();
                                ;
                            } else {
                                jsonString += "," + dataMinAgg.toJSONString();
                            }
                        }
                        jsonString += "]";

                        Log.i("UPLOADING", "UPLOADING JSON: " + jsonString);

                        String encryptedString = "";
                        String requestString = "";

                        SimpleDateFormat df = new SimpleDateFormat(AppDefaults.TIME_FORMAT_DEFAULT);
                        df.setTimeZone(TimeZone.getTimeZone("UTC"));
                        TimeZone timeZone = TimeZone.getTimeZone("UTC");
                        Calendar calendar = Calendar.getInstance(timeZone);
                        Log.i("UTCTime", df.format(calendar.getTime()));

                        requestString = AppDefaults.SCRT_KEY + df.format(calendar.getTime()).substring(0, 15)
                                + AppDefaults.API_INSERTDATA_KEY + AppDefaults.API_PASS;

                        encryptedString = AppUtils.md5(requestString);
                        Log.i("Encrypted SessionId", encryptedString);

                        // Instantiate the RequestQueue.
                        String url = AppDefaults.API_INSERTDATA_URL;

                        Map<String, String> params = new HashMap<>();
                        params.put("email", "etezel@gmail.com");
                        params.put("sessionid", encryptedString);
                        params.put("activityData", jsonString);

                        APIRequest request = new APIRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                // Display the first 500 characters of the response string.
                                try {
                                    if (response.getJSONObject("status").getBoolean("successful")) {

                                        saveMinAggUploads();

                                    } else {
                                        Log.i("UPLOAD Response", "UPLOAD Response Failed" + response.toString());
                                        LoggerService.insertLog(clsName, "Uploading MinAggData Response Failed", null);
                                    }
                                } catch (JSONException e) {
                                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                                            + ":" + e.getStackTrace()[0].getLineNumber());
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                String errorStr = AppUtils.getVolleyErrorMessage(error);

                                if (error.getMessage() != null) {
                                    Log.i("Response Error", error.getMessage());
                                } else if (error.getLocalizedMessage() != null) {
                                    Log.i("Response Error", error.getLocalizedMessage());
                                } else {
                                    Log.i("Response Error", "Volley API Uploading MinAggData Response Error");
                                }
                                LoggerService.insertLog(clsName, "Volley API Uploading MinAggData Response Error",
                                        errorStr);

                            }
                        });

                        queue.add(request);

                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

    }

    public static void saveMinAggUploads(){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkDataMinAgg> uploadedResults = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("uploadSuccessful", false)
                            .greaterThan("uploadTryCount", 0)
                            .findAll();

                    for (FitWorkDataMinAgg uploadedMinAgg : uploadedResults) {

                        uploadedMinAgg.setUploadSuccessful(true);
                        uploadedMinAgg.setUploadDate(uploadedMinAgg.getUploadTryDate());

                    }
                }
            });

            Log.i("UPLOAD Response", "UPLOAD Response Successful");

        } catch(Exception e){
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

    }

    public static void deleteExpiredData(){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            final Calendar midNight = Calendar.getInstance();
            midNight.set(Calendar.HOUR_OF_DAY, 0);

            // Delete expired MinAggData
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkDataMinAgg> results =
                            realm.where(FitWorkDataMinAgg.class)
                            .lessThan("insertDate", midNight.getTime())
                            .equalTo("uploadSuccessful", true)
                            .findAll();

                    if(results!=null){
                            results.deleteAllFromRealm();
                    }
                }
            });

            // Delete expired FitWorkPersons
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkPerson> results =
                            realm.where(FitWorkPerson.class)
                            .lessThan("addedDate", midNight.getTime())
                            .findAll();

                    if(results!=null){
                        results.deleteAllFromRealm();
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

    }

    public static SummaryWorkPerson prepareSummaryPerson(final String hrSensorId){

        Realm mRealm = Realm.getDefaultInstance();

        final SummaryWorkPerson summaryWorkPerson = new SummaryWorkPerson();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    // Profil Fotoğrafı & Nickname
                    FitPerson person = realm.where(FitPerson.class)
                            .equalTo("hrSensorId", hrSensorId)
                            .findFirst();

                    byte[] imageData = person.getPhoto();
                    if (imageData != null && imageData.length > 0) {
                        summaryWorkPerson.setmImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
                    } else {
                        summaryWorkPerson.setmImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.profiledefault));
                    }
                    summaryWorkPerson.setmNickname(person.getNickName());

                    summaryWorkPerson.setmGender(person.getGender());

                    summaryWorkPerson.setmImageUrl(person.getPhotoURL());

                    // Süre
                    long duration = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.hrSensorId", hrSensorId)
                            .count();
                    summaryWorkPerson.setmDuration(String.valueOf(duration));

                    // Top Kalori
                    Number totalCal = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.hrSensorId", hrSensorId)
                            .sum("totalCal");

                    summaryWorkPerson.setmTotalCal(String.valueOf((int) Math.round(totalCal == null ? 0 : totalCal.doubleValue())));

                    // Kalori / dk
                    summaryWorkPerson.setmCalMin(String.valueOf(String.format("%.2f", totalCal == null ? 0 : (totalCal.doubleValue() / duration))));

                    // Maks Perf
                    Number maxPerf = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.hrSensorId", hrSensorId)
                            .max("avgPerf");

                    summaryWorkPerson.setmMaxPerf(String.valueOf(maxPerf == null ? 0 : maxPerf.intValue()));

                    // Ort Perf
                    double avgPerf = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.hrSensorId", hrSensorId)
                            .average("avgPerf");

                    summaryWorkPerson.setmAvgPerf(String.valueOf((int) Math.round(avgPerf)));

                    // Maks Nabız
                    Number maxHr = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.hrSensorId", hrSensorId)
                            .max("maxHr");

                    summaryWorkPerson.setmMaxHr(String.valueOf(maxHr == null ? 0 : maxHr.intValue()));

                    // Ort Nabız
                    double avgHr = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.hrSensorId", hrSensorId)
                            .average("avgHr");

                    summaryWorkPerson.setmAvgHr(String.valueOf((int) Math.round(avgHr)));

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

        return summaryWorkPerson;

    }

    public static BarData getBarData(final String hrSensorId){

        Realm mRealm = Realm.getDefaultInstance();

        final BarData[] barDataArray = new BarData[1];

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    // Init Chart Dataset
                    List<BarEntry> barEntries = new ArrayList<>();
                    float zone1Cal = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.hrSensorId", hrSensorId)
                            .equalTo("avgZone", 1)
                            .sum("totalCal")
                            .floatValue();

                    float zone2Cal = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.hrSensorId", hrSensorId)
                            .equalTo("avgZone", 2)
                            .sum("totalCal")
                            .floatValue();

                    float zone3Cal = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.hrSensorId", hrSensorId)
                            .equalTo("avgZone", 3)
                            .sum("totalCal")
                            .floatValue();

                    float zone4Cal = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.hrSensorId", hrSensorId)
                            .equalTo("avgZone", 4)
                            .sum("totalCal")
                            .floatValue();

                    float zone5Cal = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.hrSensorId", hrSensorId)
                            .equalTo("avgZone", 5)
                            .sum("totalCal")
                            .floatValue();


                    barEntries.add(new BarEntry(0, zone1Cal));
                    barEntries.add(new BarEntry(1, zone2Cal));
                    barEntries.add(new BarEntry(2, zone3Cal));
                    barEntries.add(new BarEntry(3, zone4Cal));
                    barEntries.add(new BarEntry(4, zone5Cal));

                    BarDataSet dataSet = new BarDataSet(barEntries, "BarDataSet");
                    int[] zoneColors = new int[5];
                    for (int i = 0; i < 5; i++) {
                        zoneColors[i] = getZoneColor(i + 1);
                    }
                    dataSet.setColors(zoneColors, mContext);
                    dataSet.setDrawValues(false);
                    barDataArray[0] = new BarData(dataSet);
                    barDataArray[0].setBarWidth(0.75f);


                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

        return barDataArray.length==1?barDataArray[0] :null;

    }

    public static LineData getLineData(final String hrSensorId) {

        Realm mRealm = Realm.getDefaultInstance();

        final LineData[] lineDataArray = new LineData[1];

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkDataMinAgg> results =
                            realm.where(FitWorkDataMinAgg.class)
                                    .equalTo("fitPerson.hrSensorId", hrSensorId)
                                    .findAllSorted("insertDate");

                    List<Entry> lineEntries = new ArrayList<>();
                    for (int i = 0; i < results.size(); i++) {
                        lineEntries.add(new Entry(i, results.get(i).getAvgHr()));
                    }

                    LineDataSet dataSet2 = new LineDataSet(lineEntries, "LineDataSet");
                    dataSet2.setColor(ContextCompat.getColor(mContext, R.color.userBlue));
                    dataSet2.setDrawValues(false);
                    dataSet2.setDrawCircles(false);
                    dataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSet2.setDrawFilled(true);
                    dataSet2.setFillColor(ContextCompat.getColor(mContext, R.color.userSkyBlue));
                    lineDataArray[0] = new LineData(dataSet2);
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

        return lineDataArray.length==1?lineDataArray[0] :null;

    }

    private static int getZoneColor(int zone){
        switch (zone){
            case 1 : return R.color.userGray;
            case 2 : return R.color.userBlue;
            case 3 : return R.color.userGreen;
            case 4 : return R.color.userOrange;
            case 5 : return R.color.userRed;
            default: return R.color.userWhite;
        }
    }


    public static String nextPersonToDisplay(){

        Realm mRealm = Realm.getDefaultInstance();

        final String[] nextPersonBeltId = new String[]{""};
        final boolean[] nextPersonSelected = new boolean[]{false};

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Calendar timeOut = Calendar.getInstance();
                    timeOut.add(Calendar.SECOND, -60);

                    RealmResults<FitWorkPerson> workPersons = realm.where(FitWorkPerson.class)
                            .isNotNull("fitPerson")
                            .greaterThan("hrLastDataUpdateDate", timeOut.getTime())
                            .findAll();


                    // Persons with minimum 2 minAgg data rows
                    int sizeOfDisplayables = 0;

                    // Pick a person who is not displayed recently
                    for (FitWorkPerson fitWorkPerson : workPersons) {

                        String sensorId = fitWorkPerson.getActHrSensorId();

                        if (!"".equals(sensorId) && realm.where(FitWorkDataMinAgg.class)
                                .equalTo("fitPerson.hrSensorId", sensorId)
                                .count() >= 2) {

                            sizeOfDisplayables++;
                            if (!summaryDisplayedList.contains(sensorId) && !nextPersonSelected[0]) {
                                nextPersonBeltId[0] = fitWorkPerson.getActHrSensorId();
                                summaryDisplayedList.add(nextPersonBeltId[0]);
                                nextPersonSelected[0] = true;
                            }
                        }
                    }

                    if (summaryDisplayedList.size() >= sizeOfDisplayables) {
                        summaryDisplayedList = new ArrayList<String>();
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

        return nextPersonBeltId[0];

    }

    // UDP Functions

    public List<String> getSimUsers(){

        final List<String> simUsers = new ArrayList<String>();

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitPerson> realmResults = realm.where(FitPerson.class)
                            .beginsWith("hrSensorId", "1")
                            .findAll();

                    for(FitPerson fitPerson :realmResults){
                        simUsers.add(fitPerson.getHrSensorId());
                    }

                }
            });

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            mRealm.closeDialog();
        }

        return simUsers;
    }

    public void addDataToMinAgg(final Map<String, List<RawData>> curDataMap, final List<String> curUDPList){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // TODO: 27/02/17 FitWork'ler ayrılacak
                    FitWork fitWork = realm.where(FitWork.class).findFirst();

                    for (String hrSensorId : curUDPList) {

                        FitPerson fitPerson = realm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).findFirst();

                        if (fitPerson != null) {

                            int maxHr = getMaxHr(curDataMap.get(hrSensorId));
                            int avgHr = calcAvgHr(curDataMap.get(hrSensorId));
                            int avgPerf = calcPerf(avgHr, fitPerson);
                            int avgZone = calcZone(avgHr, fitPerson);
                            double calBurned = calcCalBurned(avgHr, fitPerson);

                            Log.i("Yakilan Kalori: ", "Yakilan Kalori: " + calBurned);

                            FitWorkPerson fitWorkPerson = realm.where(FitWorkPerson.class).equalTo("actHrSensorId", hrSensorId).findFirst();
                            if (fitWorkPerson != null) {
                                fitWorkPerson.setAvgHr(avgHr);
                                fitWorkPerson.setMaxHr(maxHr);
                                fitWorkPerson.setAvgPerf(avgPerf);
                                fitWorkPerson.setTotalCal(fitWorkPerson.getTotalCal() + calBurned);
                            }

                            FitWorkDataMinAgg minAgg = realm.createObject(FitWorkDataMinAgg.class);
                            minAgg.setFitWork(fitWork);
                            minAgg.setFitPerson(fitPerson);
                            minAgg.setHrDataCount(curDataMap.get(hrSensorId).size());
                            minAgg.setMaxHr(maxHr);
                            minAgg.setAvgHr(avgHr);
                            minAgg.setAvgPerf(avgPerf);
                            minAgg.setAvgZone(avgZone);
                            minAgg.setTotalCal(calBurned);
                            minAgg.setInsertDate(Calendar.getInstance().getTime());

                        }

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }


    }

    private int getMaxHr(List<RawData> rawDataList){
        int maxHr = 0;
        for(RawData rawData :rawDataList){
            if(rawData.getHeartRate()>maxHr){
                maxHr = rawData.getHeartRate();
            }
        }
        return maxHr;
    }

    private int calcAvgHr(List<RawData> rawDataList){

        if(rawDataList==null || rawDataList.size() == 0){
            return 0;
        }

        int hrSum = 0;
        int avgHr = 0;
        for(RawData rawData :rawDataList){
            hrSum += rawData.getHeartRate();
        }

        avgHr = hrSum / rawDataList.size();

        return avgHr;
    }

    private static double calcCalBurned(int calcAvgHr, FitPerson person){

        double calBurned = 0.0;
        int gender = person.getGender();
        int age = person.getAge();
        int weight = person.getWeight();
        if(gender==1){ // male
            calBurned = (age * 0.2017 + weight * 0.1988 + calcAvgHr * 0.6309 - 55.0969) * (1/4.184);
        } else if(gender == 2) { // female
            calBurned = (age * 0.074 + weight * 0.1263 + calcAvgHr * 0.4472 - 20.4022) * (1/4.184);
        }

        if(calBurned>0){
            return calBurned;
        } else {
            return 0;
        }
    }


    public static void backupRealm() {

        Realm mRealm = Realm.getDefaultInstance();

        try {

            File dir = new File(Environment.getExternalStorageDirectory(),
                    "RealmBackupFiles");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            SimpleDateFormat df = new SimpleDateFormat(AppDefaults.TIME_FORMAT_BACKUP);
            String backupFileName = "RealmBackup_" + df.format(Calendar.getInstance().getTime()) + ".realm";

            // create a backup file
            File exportRealmFile;
            exportRealmFile = new File(dir, backupFileName);

            // if backup file already exists, delete it
            exportRealmFile.delete();

            // copy current realm to backup file
            mRealm.writeCopyTo(exportRealmFile);

        } catch (Exception e){
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        } finally {
            mRealm.closeDialog();
        }
    }

    public static void backupLogRealm() {

        RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                .build();

        Realm logRealm = Realm.getInstance(logConfig);

        try {

            File dir = new File(Environment.getExternalStorageDirectory(),
                    "RealmBackupFiles");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            SimpleDateFormat df = new SimpleDateFormat(AppDefaults.TIME_FORMAT_BACKUP);
            String backupFileName = "RealmLogBackup_" + df.format(Calendar.getInstance().getTime()) + ".realm";

            // create a backup file
            File exportRealmFile;
            exportRealmFile = new File(dir, backupFileName);

            // if backup file already exists, delete it
            exportRealmFile.delete();

            // copy current realm to backup file
            logRealm.writeCopyTo(exportRealmFile);

        } catch (Exception e){
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        } finally {
            logRealm.closeDialog();
        }
    }


    public static void processRawData(final RawData rawData, final boolean isTestUser){

        if(StaticVariables.isPersonInWaitingList(rawData.getDeviceNo())
                //|| StaticVariables.isPersonInProcess(rawData.getDeviceNo())
                ){
            return;
        }

        Realm mRealm = Realm.getDefaultInstance();

        try {

            final String hrSensorId = rawData.getDeviceNo();

            FitWorkPerson fitWorkPerson = mRealm.where(FitWorkPerson.class)
                            .equalTo("actHrSensorId", hrSensorId).findFirst();


            // Check if person is in activity
            if(fitWorkPerson!=null){

                addActivityMinDetailData(rawData);

            } else {
                // Check if person in local
                FitPerson fitPerson = mRealm.where(FitPerson.class)
                        .equalTo("hrSensorId", hrSensorId).findFirst();

                if(fitPerson!=null){

                    addPersonToActivity(rawData);

                } else {

                    updateOrAddLocalUserData(rawData.getDeviceNo(), isTestUser, rawData.getHexString());

                }

            }

        } catch (Exception e){
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        } finally {
            mRealm.closeDialog();
        }

    }

    private static void addActivityMinDetailData(final RawData rawData){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            //StaticVariables.addPersonToProcess(rawData.getDeviceNo());

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {


                    FitWork fitWork = realm.where(FitWork.class).findFirst();
                    FitPerson fitPerson = realm.where(FitPerson.class).equalTo("hrSensorId", rawData.getDeviceNo()).findFirst();

                    FitWorkDataMinDetail minDetail = new FitWorkDataMinDetail();
                    minDetail.setFitWork(fitWork);
                    minDetail.setFitPerson(fitPerson);
                    minDetail.setInsertDate(Calendar.getInstance().getTime());
                    minDetail.setCurrentHr(rawData.getHeartRate());
                    minDetail.setCurrentRssi(rawData.getRssi());
                    minDetail.setCurrentZone(calcZone(rawData.getHeartRate(), fitPerson.getMaxHr()));
                    minDetail.setCurrentPerf(calcPerf(rawData.getHeartRate(), fitPerson.getMaxHr()));
                    minDetail.setCurrentSpeed(0);
                    minDetail.setCurrentCadence(0);

                    realm.copyToRealm(minDetail);

                }
            });


            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    FitPerson fitPerson = realm.where(FitPerson.class).equalTo("hrSensorId", rawData.getDeviceNo()).findFirst();

                    FitWorkPerson fitWorkPerson = realm.where(FitWorkPerson.class)
                            .equalTo("actHrSensorId", rawData.getDeviceNo()).findFirst();

                    if(fitWorkPerson!=null) {
                        fitWorkPerson.setCurrentHr(rawData.getHeartRate());
                        fitWorkPerson.setCurrentPerf(calcPerf(rawData.getHeartRate(), fitPerson.getMaxHr()));
                        fitWorkPerson.setCurrentZone(calcZone(rawData.getHeartRate(), fitPerson.getMaxHr()));
                        fitWorkPerson.setCurrentSpeed(0);
                        fitWorkPerson.setCurrentCadence(0);
                        fitWorkPerson.setCurrentRssi(rawData.getRssi());
                        fitWorkPerson.setHrLastDataUpdateDate(Calendar.getInstance().getTime());
                    }
                }
            });

        } catch (Exception e) {
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        } finally {
            mRealm.closeDialog();
            //StaticVariables.removePersonFromProcess(rawData.getDeviceNo());
        }

    }


    private static void addPersonToActivity(final RawData rawData){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            //StaticVariables.addPersonToProcess(rawData.getDeviceNo());

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {


                    FitWork fitWork = realm.where(FitWork.class).findFirst();
                    FitPerson fitPerson = realm.where(FitPerson.class).equalTo("hrSensorId", rawData.getDeviceNo()).findFirst();
                    double prevTotalCal = 0;
                    Number prevTotalCalNum = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.hrSensorId", rawData.getDeviceNo())
                            .sum("totalCal");
                    if(prevTotalCalNum!=null){
                        prevTotalCal = prevTotalCalNum.doubleValue();
                    }

                    FitWorkPerson fitWorkPerson = new FitWorkPerson();
                    fitWorkPerson.setFitWork(fitWork);
                    fitWorkPerson.setFitPerson(fitPerson);
                    fitWorkPerson.setActHrSensorId(fitPerson.getHrSensorId());
                    fitWorkPerson.setActScSensorId("");
                    fitWorkPerson.setStatus(1);
                    fitWorkPerson.setAddedDate(Calendar.getInstance().getTime());
                    fitWorkPerson.setTotalCal(prevTotalCal);
                    fitWorkPerson.setHrLastDataUpdateDate(Calendar.getInstance().getTime());

                    realm.copyToRealm(fitWorkPerson);

                }
            });

        } catch (Exception e) {
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        } finally {
            mRealm.closeDialog();
            //StaticVariables.removePersonFromProcess(rawData.getDeviceNo());
            addActivityMinDetailData(rawData);
            //fwActivity.updateUI();
        }

    }

    public static void removeExpiredPersonsFromActivity(){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            final Calendar timeOut = Calendar.getInstance();
            timeOut.add(Calendar.SECOND, -60);

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(FitWorkPerson.class)
                            .lessThan("hrLastDataUpdateDate", timeOut.getTime())
                            .findAll().deleteAllFromRealm();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

    }

    public static void addDataToMinAgg(){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Calendar timeOut = Calendar.getInstance();
                    timeOut.add(Calendar.SECOND, -60);

                    RealmResults<FitWorkPerson> fitWorkPersons = realm.where(FitWorkPerson.class)
                            .isNotNull("fitPerson")
                            .greaterThan("hrLastDataUpdateDate", timeOut.getTime())
                            .findAll();

                    for (FitWorkPerson fitWorkPerson : fitWorkPersons) {

                        FitPerson fitPerson = realm.where(FitPerson.class).equalTo("hrSensorId", fitWorkPerson.getActHrSensorId()).findFirst();
                        RealmResults<FitWorkDataMinDetail> minDetails = realm.where(FitWorkDataMinDetail.class)
                                .equalTo("fitPerson.hrSensorId", fitWorkPerson.getActHrSensorId()).findAll();

                        if (fitPerson != null) {

                            int maxHr = minDetails.max("currentHr")!=null ? minDetails.max("currentHr").intValue() :0;
                            int avgHr = (int) minDetails.average("currentHr");
                            int avgPerf = calcPerf(avgHr, fitPerson.getMaxHr());
                            int avgZone = calcZone(avgHr, fitPerson.getMaxHr());
                            double calBurned = calcCalBurned(avgHr, fitPerson);

                            //Log.i("Yakilan Kalori: ", "Yakilan Kalori: " + calBurned);
                            if (fitWorkPerson != null) {
                                fitWorkPerson.setAvgHr(avgHr);
                                fitWorkPerson.setMaxHr(maxHr);
                                fitWorkPerson.setAvgPerf(avgPerf);
                                fitWorkPerson.setTotalCal(fitWorkPerson.getTotalCal() + calBurned);
                            }

                            FitWorkDataMinAgg minAgg = new FitWorkDataMinAgg();
                            minAgg.setFitWork(fitWorkPerson.getFitWork());
                            minAgg.setFitPerson(fitPerson);
                            minAgg.setHrDataCount(minDetails.size());
                            minAgg.setMaxHr(maxHr);
                            minAgg.setAvgHr(avgHr);
                            minAgg.setAvgPerf(avgPerf);
                            minAgg.setAvgZone(avgZone);
                            minAgg.setTotalCal(calBurned);
                            minAgg.setInsertDate(Calendar.getInstance().getTime());

                            realm.copyToRealm(minAgg);

                        }

                    }

                    // Delete minDetail data
                    realm.where(FitWorkDataMinDetail.class).findAll().deleteAllFromRealm();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }


    }

    public static List<FitWorkPersonCard> getCurrentCardList(){

        final List<FitWorkPersonCard> cards = new ArrayList<FitWorkPersonCard>();

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkPerson> results = realm.where(FitWorkPerson.class)
                            .isNotNull("fitPerson")
                            .findAll();

                    for (FitWorkPerson person :results){

                        FitWorkPersonCard card = new FitWorkPersonCard();
                        card.setNickName(person.getFitPerson().getNickName());
                        card.setGender(person.getFitPerson().getGender());
                        card.setCurrentHr(person.getCurrentHr());
                        card.setCaloriesBurned((int) person.getTotalCal());
                        card.setCurrentZone(person.getCurrentZone());
                        card.setCurrentPerf(person.getCurrentPerf());
                        card.setImageUrl(person.getFitPerson().getPhotoURL());

                        cards.add(card);
                    }

                }
            });

        } catch (Exception e){

        } finally {
            mRealm.closeDialog();
        }

        return cards;
    }


}*/
