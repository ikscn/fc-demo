package com.applissima.fitconnectdemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by ilkerkuscan on 22/02/17.
 */

public class DatabaseService extends Service {

    private final String clsName = "DatabaseService";
    //private Realm mRealm;
    //private Realm logRealm;
    private List<String> personsToUpdate;
    private List<String> summaryDisplayedList;
    //private Context mContext;
    private final IBinder mBinder = new DatabaseServiceBinder();
    private WeakReference<FitWorkActivity> mActivity;
    //private static FitWorkActivity fwActivity;

    //private List<int[]> uploadedDataIndices;
    //private int uploadPartsResponseCount;

    private void initService(){

        //fwActivity = activity;

        //mRealm = Realm.getDefaultInstance();

        summaryDisplayedList = new ArrayList<String>();

        LocalBroadcastManager.getInstance(this).registerReceiver(antReceiver,
                new IntentFilter(AppDefaults.DATA_HR));

    }


    /*public void createInitWork(final String hrSensorId){

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

    }*/

    /*public void updateWork(final String hrSensorId){

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

    }*/

    public void compactRealms(){

        try {

            RealmConfiguration config = new RealmConfiguration
                    .Builder()
                    .deleteRealmIfMigrationNeeded()
                    .name(AppDefaults.REALM_FILENAME)
                    //.directory(Environment.getExternalStorageDirectory())
                    .build();

            Realm.compactRealm(config);

            RealmConfiguration logConfig = new RealmConfiguration
                    .Builder()
                    .deleteRealmIfMigrationNeeded()
                    //.directory(Environment.getExternalStorageDirectory())
                    .name(AppDefaults.REALMLOG_FILENAME)
                    .build();

            Realm.compactRealm(logConfig);

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }

    }


    /*public void refreshRealtimeData(){

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
                            *//*if (workPerson.getActHrSensorId().equalsIgnoreCase("90002")) {
                                testDataList.add(rawData);
                            }*//*
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

    }*/


    /*public void checkBelts(){

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

    }*/



    private int calcZone(int heartRate, int maxHr){

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

    private int calcPerf(int heartRate, int maxHr){
        return (int) Math.round((double)heartRate / (double) maxHr * 100);
    }

    /*public RawData generateRandomRawData(){

        Random rand = new Random();
        int heartRate = rand.nextInt(101) + 60; // between 60 & 160
        int deviceType = 1;
        int rssi = rand.nextInt(100) - 50; // between -50 & 50

        RawData data = new RawData(heartRate, null, deviceType, rssi);

        return data;
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

    }*/


    public void updateOrAddLocalUserData(final String hrSensorId, final boolean isTestUser, final boolean isInFitWork, final String hexStr) {

        ProcessController.addToUserUpdatingList(hrSensorId);

        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();

        String url = AppDefaults.API_RETRIEVE_URL;
        String encryptedString = AppUtils.getEncryptedString(APIRequestType.DOWNLOAD_PROFILE);

        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("collectorEmail", AppDefaults.COLLECTOR_EMAIL)
                .addFormDataPart("email", hrSensorId)
                .addFormDataPart("sessionid", encryptedString)
                .build();

        okhttp3.Request okHttprequest = new okhttp3.Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(okHttprequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                String errorStr = "";
                if(e.getMessage()!=null){
                    errorStr = e.getMessage();
                } else if(e.getLocalizedMessage()!=null){
                    errorStr = e.getLocalizedMessage();
                } else if(e.getStackTrace()!=null && e.getStackTrace().length>0){
                    errorStr = e.getStackTrace()[0].toString();
                } else {
                    errorStr = "Unknown Error.";
                }

                LoggerService.insertLog(clsName, "Error on Update Person", errorStr);
                Log.e("OKHTTP", errorStr);

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                    try {

                        String responseString = response.body().string();

                        JSONObject responseJSON = new JSONObject(responseString);
                        if (responseJSON.getJSONObject("status").getBoolean("successful")) {

                            Log.i("OKHTTP", "SUCCESS on Update Person!!!");

                            try {

                                updatePerson(hrSensorId, isTestUser, responseJSON);

                            } catch (Exception e) {
                                ProcessController.removeFromUserUpdatingList(hrSensorId);
                                e.printStackTrace();
                                LoggerService.insertLog(clsName, e.getLocalizedMessage(), e.getLocalizedMessage());
                            }


                        } else {

                            Log.i("User data", "User data checked with web, no user found - 1: HrSensorId: " + hrSensorId);
                            LoggerService.insertLog(clsName, "User data checked with web, no user found - 1",
                                    "HrSensorId: " + hrSensorId + " HexString: " + hexStr);

                            if (!isInFitWork) {
                                StaticVariables.addPersonToWaitingList(hrSensorId);
                                sendSnackbarBroadcast(hrSensorId);
                            }

                            ProcessController.removeFromUserUpdatingList(hrSensorId);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        ProcessController.removeFromUserUpdatingList(hrSensorId);
                        LoggerService.insertLog(clsName, "JSONException on Retrieve Profile", e.getStackTrace()[0].getClassName()
                                + ":" + e.getStackTrace()[0].getLineNumber());
                    }

            }
        });

    }

    private void sendSnackbarBroadcast(String beltId){

        Intent snackBarEvent = new Intent(AppDefaults.ACTION_BRDC_SNACKBAR);
        snackBarEvent.putExtra("beltId", beltId);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(snackBarEvent);

    }


    /*public void addTempBeltLocal(final Member member){

        new Thread(new Runnable() {
            @Override
            public void run() {

                Realm mRealm = null;

                try {

                    mRealm = Realm.getDefaultInstance();

                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            realm.where(FitWorkPerson.class)
                                    .equalTo("actHrSensorId", member.getTempBeltId())
                                    .findAll()
                                    .deleteAllFromRealm();

                            RealmResults<FitPerson> personsWithSameBeltId
                                    = realm.where(FitPerson.class)
                                    .equalTo("hrSensorId", member.getTempBeltId())
                                    .findAll();

                            if(personsWithSameBeltId!=null && personsWithSameBeltId.size()>0){
                                for(FitPerson person :personsWithSameBeltId){
                                    person.setHrSensorId("");
                                }
                            }

                            FitPerson person = realm.where(FitPerson.class)
                                    .equalTo("userName", member.getEmail())
                                    .findFirst();

                            if(person!=null){
                                person.setHrTempSensorId(member.getTempBeltId());
                                person.setLastUpdateDate(Calendar.getInstance().getTime());
                            } else {
                                person = realm.createObject(FitPerson.class, UUID.randomUUID().toString());
                                person.fromMember(member);
                            }
                        }
                    });




                } catch (Exception e){
                    e.printStackTrace();
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                }

            }
        }).start();

    }*/

    public void deleteTempBeltLocal(final Member member){

        new Thread(new Runnable() {
            @Override
            public void run() {

                Realm mRealm = null;

                try {

                    mRealm = Realm.getDefaultInstance();

                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            realm.where(FitWorkPerson.class).equalTo("actHrSensorId", member.getTempBeltId())
                                    .findAll().deleteAllFromRealm();

                            FitPerson person = realm.where(FitPerson.class)
                                    .equalTo("userName", member.getEmail())
                                    .findFirst();

                            if(person!=null){
                                person.setHrTempSensorId("");
                                person.setLastUpdateDate(Calendar.getInstance().getTime());
                            }
                        }
                    });




                } catch (Exception e){
                    e.printStackTrace();
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                } finally {
                    if(mRealm!=null){
                        mRealm.close();
                    }
                }

            }
        }).start();

    }

    public void addTempBeltLocal(final Member member){

        new Thread(new Runnable() {
            @Override
            public void run() {

                Realm mRealm = null;

                try {

                    mRealm = Realm.getDefaultInstance();

                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            realm.where(FitWorkPerson.class)
                                    .equalTo("actHrSensorId", member.getTempBeltId())
                                    .findAll()
                                    .deleteAllFromRealm();

                            RealmResults<FitPerson> personsWithSameBeltId
                                    = realm.where(FitPerson.class)
                                    .equalTo("hrSensorId", member.getTempBeltId())
                                    .findAll();

                            if(personsWithSameBeltId!=null && personsWithSameBeltId.size()>0){
                                for(FitPerson person :personsWithSameBeltId){
                                    person.setHrSensorId("");
                                }
                            }

                            FitPerson person = realm.where(FitPerson.class)
                                    .equalTo("userName", member.getEmail())
                                    .findFirst();

                            if(person==null){
                                person = realm.createObject(FitPerson.class, UUID.randomUUID().toString());
                            }
                            person.fromMember(member);
                        }
                    });




                } catch (Exception e){
                    e.printStackTrace();
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                } finally {
                    if(mRealm!=null){
                        mRealm.close();
                    }
                }

            }
        }).start();

    }

    public List<String> getExpiredTempBelts(){

        final List<String> expiredBelts = new ArrayList<String>();

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Calendar timeOut = Calendar.getInstance();
                    timeOut.add(Calendar.MINUTE, -1);
                    // TODO change
                    //timeOut.add(Calendar.MINUTE, -60);

                    RealmResults<FitPerson> personsWithTempBelt = realm.where(FitPerson.class)
                            .notEqualTo("hrTempSensorId", "")
                            .findAll();

                    for(FitPerson person :personsWithTempBelt){

                        RealmResults<FitWorkDataMinDetail> details = realm.where(FitWorkDataMinDetail.class)
                                .equalTo("fitPerson.userName", person.getUserName())
                                .findAllSorted("insertDate");

                        RealmResults<FitWorkDataMinAgg> minAggs = realm.where(FitWorkDataMinAgg.class)
                                .equalTo("fitPerson.userName", person.getUserName())
                                .findAllSorted("insertDate");

                        if(details!=null && details.size()>0) {
                            FitWorkDataMinDetail lastDetail = details.last();
                            if(lastDetail.getInsertDate().before(timeOut.getTime())) {
                                expiredBelts.add(lastDetail.getFitPerson().getHrTempSensorId());
                            }
                        } else if(minAggs!=null && minAggs.size()>0){
                            FitWorkDataMinAgg lastMinAgg = minAggs.last();
                            if(lastMinAgg.getInsertDate().before(timeOut.getTime())){
                                expiredBelts.add(lastMinAgg.getFitPerson().getHrTempSensorId());
                            }
                        }
                    }
                }
            });

        } catch (Exception e){
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if(mRealm!=null){
                mRealm.close();
            }
        }

        return expiredBelts;

    }

    public List<String> getActiveTempBelts(){

        final List<String> activeTempBelts = new ArrayList<String>();

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkPerson> activePersons = realm.where(FitWorkPerson.class)
                            .isNotNull("fitPerson")
                            .notEqualTo("fitPerson.hrTempSensorId", "")
                            .findAll();

                    for(FitWorkPerson fitWorkPerson :activePersons){
                        activeTempBelts.add(fitWorkPerson.getFitPerson().getHrTempSensorId());
                    }
                }
            });

        } catch (Exception e){
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if(mRealm!=null){
                mRealm.close();
            }
        }

        return activeTempBelts;

    }


    private void updatePerson(final String hrSensorId, final boolean isTestUser, final JSONObject jsonResponse){

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    FitPerson personToUpdate = realm.where(FitPerson.class)
                            .equalTo("hrSensorId", hrSensorId)
                            .findFirst();

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

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(),
                    e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            ProcessController.removeFromUserUpdatingList(hrSensorId);
            if(mRealm!=null) {
                mRealm.close();
            }
        }


    }




    /*public void updateOrAddLocalUserData(final String hrSensorId, final boolean isTestUser, final boolean isInFitWork, final String hexStr) {

        ProcessController.addToUserUpdatingList(hrSensorId);

        String url = AppDefaults.API_RETRIEVE_URL;
        String encryptedString = AppUtils.getEncryptedString(APIRequestType.DOWNLOAD_PROFILE);

        Map<String, String> params = new HashMap<>();
        params.put("collectorEmail", AppDefaults.COLLECTOR_EMAIL);
        params.put("email", hrSensorId);
        params.put("sessionid", encryptedString);

        APIRequest request = new APIRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if(response!=null) {

                    // Display the first 500 characters of the response string.
                    Log.i("Volley Response", "Volley Response: " + response.toString());
                    try {

                        if (response.getJSONObject("status").getBoolean("successful")) {

                            final JSONObject jsonResponse = response;

                            try {

                                updatePerson(hrSensorId, isTestUser, jsonResponse);

                            } catch (Exception e){
                                ProcessController.removeFromUserUpdatingList(hrSensorId);
                                e.printStackTrace();
                                LoggerService.insertLog(clsName, e.getLocalizedMessage(), e.getLocalizedMessage());
                            }


                        } else {

                            Log.i("User data", "User data checked with web, no user found - 1: HrSensorId: " + hrSensorId);
                            LoggerService.insertLog(clsName, "User data checked with web, no user found - 1",
                                    "HrSensorId: " + hrSensorId
                                            + " HexString: " + hexStr);

                            *//*Toast.makeText(DatabaseService.this,
                                    "NotFound: User data checked with web, no user found. HrSensorId: " + hrSensorId,
                                    Toast.LENGTH_LONG).show();*//*

                            if(!isInFitWork) {
                                StaticVariables.addPersonToWaitingList(hrSensorId);
                            }

                            ProcessController.removeFromUserUpdatingList(hrSensorId);
                        }

                    } catch (JSONException e) {
                        LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                                + ":" + e.getStackTrace()[0].getLineNumber());
                        e.printStackTrace();
                        ProcessController.removeFromUserUpdatingList(hrSensorId);
                    } catch (Exception e) {
                        LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                                + ":" + e.getStackTrace()[0].getLineNumber());
                        e.printStackTrace();
                        ProcessController.removeFromUserUpdatingList(hrSensorId);
                    }
                } else {
                    // Response is null
                    Log.i("API Response Error", "API Response is null.");
                    ProcessController.removeFromUserUpdatingList(hrSensorId);
                }

                //StaticVariables.removePersonFromProcess(hrSensorId);
                //queue.stop();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorStr = AppUtils.getVolleyErrorMessage(error);
                Log.i("Volley API Error" , "Volley API Response Error: " + errorStr);

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
                    if(!isInFitWork){
                        Log.i("User data", "User data checked with web, no user found - 1: HrSensorId: " + hrSensorId);
                        LoggerService.insertLog(clsName, "User data checked with web, no user found - 1",
                                "HrSensorId: " + hrSensorId
                                        + " HexString: " + hexStr);

                        *//*Toast.makeText(DatabaseService.this,
                                "RespErr: User data checked with web, no user found. HrSensorId: " + hrSensorId,
                                Toast.LENGTH_LONG).show();*//*
                        StaticVariables.addPersonToWaitingList(hrSensorId);
                    }

                //}

                ProcessController.removeFromUserUpdatingList(hrSensorId);
                //queue.stop();

            }

        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }*/

    /*private void updatePerson(final String hrSensorId, final boolean isTestUser, final JSONObject jsonResponse){

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    FitPerson personToUpdate = realm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).findFirst();

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

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {

                    ProcessController.removeFromUserUpdatingList(hrSensorId);
                    //updatePersonPhoto(hrSensorId);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(),
                    e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if(mRealm!=null) {
                mRealm.closeDialog();
            }
            //StaticVariables.removePersonFromProcess(hrSensorId);
        }

        //return photoUrls.length==1?photoUrls[0] :null;

    }*/

    /*private void updatePersonPhoto(final String hrSensorId){

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

        *//*try {

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
        }*//*
    }*/

    public void updatePersonInfo(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                personsToUpdate = new ArrayList<String>();

                Realm mRealm = null;

                try {

                    mRealm = Realm.getDefaultInstance();

                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            RealmResults<FitWorkPerson> activePersons = realm.where(FitWorkPerson.class).findAll();

                            if (activePersons != null && activePersons.size() > 0) {

                                for (FitWorkPerson activePerson : activePersons) {

                                    String hrSensorId = activePerson.getActHrSensorId();

                                    if(hrSensorId!=null && !"".equals(hrSensorId)
                                            && hrSensorId.equals(activePerson.getFitPerson().getHrSensorId())) {

                                        personsToUpdate.add(hrSensorId);
                                        Log.i("PersonsToUpdate", "PersonsToUpdate: " + hrSensorId);

                                    }

                                }

                            } else {
                                Log.i("PersonsToUpdate", "PersonsToUpdate: List is null.");
                            }
                        }
                    });

                    //personsToUpdate = null;

                } catch (Exception e) {
                    e.printStackTrace();
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                    //personsToUpdate = null;
                } finally {
                    if(mRealm!=null) {
                        mRealm.close();
                    }
                }

                try {

                    for (String hrSensorId : personsToUpdate) {
                        Log.i("PersonInfo Update", "PersonInfo Update requested... BeltId: " + hrSensorId);
                        updateOrAddLocalUserData(hrSensorId, false, true, "");
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                }

            }
        }).start();

    }

    /*public void updatePersonInfo(){

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

    }*/

    /*public static void uploadLogs(){

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

    }*/


    /*public String getLogDataToUpload(){

        final RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                //.directory(Environment.getExternalStorageDirectory())
                .build();

        Realm logRealm = null;

        final JSONArray jsonArray = new JSONArray();
        String jsonString = "";

        try {

            logRealm = Realm.getInstance(logConfig);

            logRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<LogData> logResults =
                            realm.where(LogData.class)
                                    .equalTo("uploadSuccessful", false)
                                    .findAllSorted("insertDate");


                    if (logResults != null && logResults.size() > 0) {

                        for(LogData logData :logResults){

                            jsonArray.put(logData.toJSON());

                            logData.setUploadTryCount(logData.getUploadTryCount() + 1);
                            logData.setUploadTryDate(Calendar.getInstance().getTime());

                        }

                        *//*List<LogData> uploadLogList;
                        // Limit log data count by 100
                        if(logResults.size()>100){
                            uploadLogList = logResults.subList(0, 100);
                        } else {
                            uploadLogList = logResults.subList(0, logResults.size());
                        }


                        for (int i = 0; i < uploadLogList.size(); i++) {

                            LogData logData = uploadLogList.get(i);

                            logData.setUploadTryCount(logData.getUploadTryCount() + 1);
                            logData.setUploadTryDate(Calendar.getInstance().getTime());

                            if (i == 0) {
                                jsonString[0] += "[" + logData.toJSONString();
                            } else {
                                jsonString[0] += "," + logData.toJSONString();
                            }
                        }
                        jsonString[0] += "]";*//*


                    } else {
                        Log.i("LogData", "LOGDATA results is null.");
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if(logRealm!=null) {
                logRealm.closeDialog();
            }
        }

        try {
            jsonString = jsonArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }

        return jsonString;

    }*/


    public String getLogsForUpdate(){

        final JSONArray jsonArray = new JSONArray();

        final RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                //.directory(Environment.getExternalStorageDirectory())
                .build();

        Realm logRealm = null;

        try {

            logRealm = Realm.getInstance(logConfig);

            logRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<LogData> logResults =
                            realm.where(LogData.class)
                                    .equalTo("uploadSuccessful", false)
                                    .beginGroup()
                                    .not().like("message", "<*")
                                    .or()
                                    .not().like("errorDesc", "<*")
                                    .endGroup()
                                    .findAllSorted("insertDate");

                    if (logResults != null && logResults.size() > 0) {

                        Log.i("Upload Logs", "UPLOAD LOGS Total Count: " + logResults.size());

                        for (LogData logData : logResults) {

                            jsonArray.put(logData.toJSON());

                            logData.setUploadTryCount(logData.getUploadTryCount() + 1);
                            logData.setUploadTryDate(Calendar.getInstance().getTime());
                        }

                    } else {
                        Log.i("LogData", "LOGDATA results is null.");
                        LoggerService.insertLog(clsName, "LogData not found!",
                                "LOGDATA results is null.");
                    }

                }
            });

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(logRealm!=null){
                logRealm.close();
            }
        }

        return String.valueOf(jsonArray);

    }


    public void saveLogUpdates(){

        final RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                //.directory(Environment.getExternalStorageDirectory())
                .build();

        Realm logRealm = null;

        try {

            logRealm = Realm.getInstance(logConfig);

            logRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<LogData> uploadedLogResults = realm.where(LogData.class)
                            .equalTo("uploadSuccessful", false)
                            .greaterThan("uploadTryCount", 0)
                            .findAllSorted("insertDate");

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
            if(logRealm!=null) {
                logRealm.close();
            }
        }

    }

    public void clearExpiredLogs(){

        final RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                //.directory(Environment.getExternalStorageDirectory())
                .build();

        Realm logRealm = null;

        try {

            logRealm = Realm.getInstance(logConfig);

            // Delete expired and uploaded logs
            logRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Calendar twoDaysAgo = Calendar.getInstance();
                    twoDaysAgo.set(Calendar.HOUR_OF_DAY, 0);
                    twoDaysAgo.set(Calendar.MINUTE, 0);
                    twoDaysAgo.set(Calendar.SECOND, 0);
                    twoDaysAgo.add(Calendar.DAY_OF_YEAR, -2);

                    RealmResults<LogData> expiredLogResults = realm.where(LogData.class)
                            .equalTo("uploadSuccessful", true)
                            .lessThan("insertDate", twoDaysAgo.getTime())
                            .findAll();

                    if(expiredLogResults!=null){
                        expiredLogResults.deleteAllFromRealm();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(logRealm!=null) {
                logRealm.close();
            }
        }

    }

    // *************************************
    // Customizable code to clear some data
    // *************************************
    public void clearCustomData(){

        // 1)
        // Log'taki hatali veriyi (doctype ile ilgili) silmek icin kullanildi
        /*final RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                //.directory(Environment.getExternalStorageDirectory())
                .build();

        Realm logRealm = null;

        try {

            logRealm = Realm.getInstance(logConfig);

            // Delete expired and uploaded logs
            logRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<LogData> dataToBeCleared = realm.where(LogData.class)
                            .like("errorDesc", "*org.json.JSON:111*")
                            .or()
                            .like("errorDesc", "*HttpRequestValidationException*")
                            .findAll();

                    if(dataToBeCleared!=null){
                        dataToBeCleared.deleteAllFromRealm();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(logRealm!=null) {
                logRealm.close();
            }
        }*/

        // 2)
        // avgPerf -1 degerinde olan data silindi
        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkDataMinAgg> minAggs =
                            realm.where(FitWorkDataMinAgg.class)
                                    .lessThan("avgPerf", 0)
                                    .findAll();

                    if (minAggs != null) {

                       minAggs.deleteAllFromRealm();

                    }
                }
            });

            //personsToUpdate = null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(mRealm!=null) {
                mRealm.close();
            }
        }

    }



    /*public JSONArray getMinAggDataToUpload(){

        String jsonString = "";
        final JSONArray jsonArray = new JSONArray();


        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkDataMinAgg> results =
                            realm.where(FitWorkDataMinAgg.class)
                                    .equalTo("uploadSuccessful", false)
                                    .findAllSorted("insertDate");


                    if (results != null && results.size() > 0) {

                        Log.i("Upload MinAgg", "UPLOAD MINAGG Data Count: " + results.size());

                        for (FitWorkDataMinAgg minAgg : results) {

                            jsonArray.put(minAgg.toJSON());

                            minAgg.setUploadTryCount(minAgg.getUploadTryCount() + 1);
                            minAgg.setUploadTryDate(Calendar.getInstance().getTime());
                        }

                        // Upload list is limited by 100
                        *//*List<FitWorkDataMinAgg> uploadList;
                        if(results.size()>1000){
                            uploadList = results.subList(0, 1000);
                        } else {
                            uploadList = results.subList(0, results.size());
                        }*//*

                        //Gson gson = new Gson();

                        *//*for (int i = 0; i < uploadList.size(); i++) {

                            FitWorkDataMinAgg minAgg = uploadList.get(i);

                            minAgg.setUploadTryCount(minAgg.getUploadTryCount() + 1);
                            minAgg.setUploadTryDate(Calendar.getInstance().getTime());

                            if(i==0){
                                jsonString[0] += "[" + minAgg.toJSONString();
                            } else {
                                jsonString[0] += "," + minAgg.toJSONString();
                            }
                        }

                        jsonString[0] += "]";*//*



                        *//*for (int i = 0; i < results.size(); i++) {

                            FitWorkDataMinAgg dataMinAgg = results.get(i);
                            if (i == 0) {
                                jsonString[0] += "[" + dataMinAgg.toJSONString();
                                ;
                            } else {
                                jsonString[0] += "," + dataMinAgg.toJSONString();
                            }
                        }
                        jsonString[0] += "]";*//*

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

        try {
            jsonString = jsonArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }

        return jsonArray;

    }


    public JSONArray getMinAggDataToUploadJSONArray(){

        final JSONArray jsonArray = new JSONArray();

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkDataMinAgg> results =
                            realm.where(FitWorkDataMinAgg.class)
                                    .equalTo("uploadSuccessful", false)
                                    .findAllSorted("insertDate");


                    if (results != null && results.size() > 0) {

                        Log.i("Upload MinAgg", "UPLOAD MINAGG Data Count: " + results.size());

                        for(FitWorkDataMinAgg minAgg :results){

                            jsonArray.put(minAgg.toJSON());

                            minAgg.setUploadTryCount(minAgg.getUploadTryCount() + 1);
                            minAgg.setUploadTryDate(Calendar.getInstance().getTime());
                        }

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



        return jsonArray;

    }*/

    /*public static void uploadMinDataAgg(){

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

    }*/

    /*public void saveMinAggUploads(final List<int[]> uploadedIndices){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkDataMinAgg> results = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("uploadSuccessful", false)
                            .greaterThan("uploadTryCount", 0)
                            .findAllSorted("insertDate");

                    for(int[] indices :uploadedIndices){

                        // Update uploaded data
                        List<FitWorkDataMinAgg> subList = results.subList(indices[0], indices[1]);
                        for (FitWorkDataMinAgg uploadedMinAgg : subList) {

                            uploadedMinAgg.setUploadSuccessful(true);
                            uploadedMinAgg.setUploadDate(uploadedMinAgg.getUploadTryDate());

                        }

                    }
                }
            });

            Log.i("UPLOADED", "Uploaded Results updated!");

        } catch(Exception e){
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }

    }*/

    public void saveMinAggUploads(final int LIMIT){

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkDataMinAgg> results = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("uploadSuccessful", false)
                            .greaterThan("uploadTryCount", 0)
                            .findAllSorted("insertDate");

                    // Upload list is limited

                    List<FitWorkDataMinAgg> uploadedResults;
                    if(results.size()>LIMIT){
                        uploadedResults = results.subList(0, LIMIT);
                    } else {
                        uploadedResults = results.subList(0, results.size());
                    }

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
            if(mRealm!=null) {
                mRealm.close();
            }
        }

    }

    public void deleteExpiredData(){

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            final Calendar midNight = Calendar.getInstance();
            midNight.set(Calendar.HOUR_OF_DAY, 0);
            midNight.set(Calendar.MINUTE, 0);
            midNight.set(Calendar.SECOND, 0);

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

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if (mRealm!=null){
                mRealm.close();
            }
        }

    }

    public SummaryWorkPerson prepareSummaryPerson(){

        final String nextPersonUserName = nextPersonToDisplay();
        if("".equals(nextPersonUserName)){
            return null;
        }
        final SummaryWorkPerson summaryWorkPerson = new SummaryWorkPerson();
        summaryWorkPerson.setmUserName(nextPersonUserName);
        //summaryWorkPerson.setHrSensorId(hrSensorId);

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    final Calendar midNight = Calendar.getInstance();
                    midNight.set(Calendar.HOUR_OF_DAY, 0);
                    midNight.set(Calendar.MINUTE, 0);
                    midNight.set(Calendar.SECOND, 0);

                    // Profil Fotoğrafı & Nickname
                    FitPerson person = realm.where(FitPerson.class)
                            .equalTo("userName", nextPersonUserName)
                            .findFirst();

                    /*byte[] imageData = person.getPhoto();
                    if (imageData != null && imageData.length > 0) {
                        summaryWorkPerson.setmImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
                    } else {
                        summaryWorkPerson.setmImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.profiledefault));
                    }*/
                    summaryWorkPerson.setmNickname(person.getNickName());

                    summaryWorkPerson.setmGender(person.getGender());

                    summaryWorkPerson.setmImageUrl(person.getPhotoURL());

                    // Süre
                    long duration = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.userName", nextPersonUserName)
                            .greaterThan("insertDate", midNight.getTime())
                            .count();
                    summaryWorkPerson.setmDuration(String.valueOf(duration));

                    // Top Kalori
                    /*Number totalCal = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.userName", nextPersonUserName)
                            .greaterThan("insertDate", midNight.getTime())
                            .sum("totalCal");*/

                    FitWorkPerson fitWorkPerson = realm.where(FitWorkPerson.class)
                            .equalTo("fitPerson.userName", nextPersonUserName)
                            .findFirst();

                    Number totalCal = 0;

                    if(fitWorkPerson!=null){
                        totalCal = fitWorkPerson.getTotalCal();
                    }

                    summaryWorkPerson.setmTotalCal(String.valueOf((int) Math.round(totalCal.doubleValue())));

                    // Kalori / dk
                    summaryWorkPerson.setmCalMin(String.valueOf(
                                String.format(Locale.getDefault(), "%.2f", (totalCal.doubleValue() / duration))));

                    // Maks Nabız
                    Number maxHr = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.userName", nextPersonUserName)
                            .greaterThan("insertDate", midNight.getTime())
                            .max("maxHr");

                    summaryWorkPerson.setmMaxHr(String.valueOf(maxHr == null ? 0 : maxHr.intValue()));

                    // Ort Nabız
                    double avgHr = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.userName", nextPersonUserName)
                            .greaterThan("insertDate", midNight.getTime())
                            .average("avgHr");

                    summaryWorkPerson.setmAvgHr(String.valueOf((int) Math.round(avgHr)));


                    // Maks Perf
                    Number maxPerf = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.userName", nextPersonUserName)
                            .greaterThan("insertDate", midNight.getTime())
                            .max("avgPerf");

                    summaryWorkPerson.setmMaxPerf(String.valueOf(maxPerf == null ? 0 : maxPerf.intValue()));

                    // Ort Perf
                    double avgPerf = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.userName", nextPersonUserName)
                            .greaterThan("insertDate", midNight.getTime())
                            .average("avgPerf");

                    summaryWorkPerson.setmAvgPerf(String.valueOf((int) Math.round(avgPerf)));


                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if(mRealm!=null) {
                mRealm.close();
            }
        }

        return summaryWorkPerson;

    }

    public BarData getBarData(final String userName){

        final BarData[] barDataArray = new BarData[1];

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    final Calendar midNight = Calendar.getInstance();
                    midNight.set(Calendar.HOUR_OF_DAY, 0);
                    midNight.set(Calendar.MINUTE, 0);
                    midNight.set(Calendar.SECOND, 0);

                    // Init Chart Dataset
                    List<BarEntry> barEntries = new ArrayList<>();
                    float zone1Cal = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.userName", userName)
                            .greaterThan("insertDate", midNight.getTime())
                            .equalTo("avgZone", 1)
                            .sum("totalCal")
                            .floatValue();

                    float zone2Cal = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.userName", userName)
                            .greaterThan("insertDate", midNight.getTime())
                            .equalTo("avgZone", 2)
                            .sum("totalCal")
                            .floatValue();

                    float zone3Cal = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.userName", userName)
                            .greaterThan("insertDate", midNight.getTime())
                            .equalTo("avgZone", 3)
                            .sum("totalCal")
                            .floatValue();

                    float zone4Cal = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.userName", userName)
                            .greaterThan("insertDate", midNight.getTime())
                            .equalTo("avgZone", 4)
                            .sum("totalCal")
                            .floatValue();

                    float zone5Cal = realm.where(FitWorkDataMinAgg.class)
                            .equalTo("fitPerson.userName", userName)
                            .greaterThan("insertDate", midNight.getTime())
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
                    dataSet.setColors(zoneColors, DatabaseService.this);
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
            if(mRealm!=null) {
                mRealm.close();
            }
        }

        return barDataArray.length==1?barDataArray[0] :null;

    }

    public LineData getLineData(final String userName) {

        final LineData[] lineDataArray = new LineData[1];

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    final Calendar midNight = Calendar.getInstance();
                    midNight.set(Calendar.HOUR_OF_DAY, 0);
                    midNight.set(Calendar.MINUTE, 0);
                    midNight.set(Calendar.SECOND, 0);

                    RealmResults<FitWorkDataMinAgg> results =
                            realm.where(FitWorkDataMinAgg.class)
                                    .equalTo("fitPerson.userName", userName)
                                    .greaterThan("insertDate", midNight.getTime())
                                    .findAllSorted("insertDate");

                    List<FitWorkDataMinAgg> lastResults;

                    if(results.size()>120) {
                        lastResults = results.subList(results.size() - 120, results.size());
                    } else {
                        lastResults = results.subList(0, results.size());
                    }


                    List<Entry> lineEntries = new ArrayList<>();
                    for (int i = 0; i < lastResults.size(); i++) {
                        lineEntries.add(new Entry(i, lastResults.get(i).getAvgHr()));
                    }

                    LineDataSet dataSet2 = new LineDataSet(lineEntries, "LineDataSet");
                    dataSet2.setColor(ContextCompat.getColor(DatabaseService.this, R.color.userBlue));
                    dataSet2.setDrawValues(false);
                    dataSet2.setDrawCircles(false);
                    dataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSet2.setDrawFilled(true);
                    dataSet2.setFillColor(ContextCompat.getColor(DatabaseService.this, R.color.userSkyBlue));
                    lineDataArray[0] = new LineData(dataSet2);
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if(mRealm!=null) {
                mRealm.close();
            }
        }

        return lineDataArray.length==1?lineDataArray[0] :null;

    }

    private int getZoneColor(int zone){
        switch (zone){
            case 1 : return R.color.userGrey;
            case 2 : return R.color.userBlue;
            case 3 : return R.color.userGreen;
            case 4 : return R.color.userOrange;
            case 5 : return R.color.userRed;
            default: return R.color.userWhite;
        }
    }


    public String nextPersonToDisplay(){

        final String[] nextPersonUserName = new String[]{""};
        final boolean[] nextPersonSelected = new boolean[]{false};

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Calendar midNight = Calendar.getInstance();
                    midNight.set(Calendar.HOUR_OF_DAY, 0);
                    midNight.set(Calendar.MINUTE, 0);
                    midNight.set(Calendar.SECOND, 0);

                    RealmResults<FitWorkPerson> workPersons = realm.where(FitWorkPerson.class)
                            .isNotNull("fitPerson")
                            .findAll();

                    // Persons with minimum 2 minAgg data rows
                    int sizeOfDisplayables = 0;

                    // Pick a person who is not displayed recently
                    for (FitWorkPerson fitWorkPerson : workPersons) {

                        Log.i("DisplayList", fitWorkPerson.getActHrSensorId());
                        Log.i("DisplayList", fitWorkPerson.getFitPerson().getNickName());

                        String userName = fitWorkPerson.getFitPerson().getUserName();

                        if (!"".equals(userName) && realm.where(FitWorkDataMinAgg.class)
                                .equalTo("fitPerson.userName", fitWorkPerson.getFitPerson().getUserName())
                                .greaterThan("insertDate", midNight.getTime())
                                .count() >= 2) {

                            sizeOfDisplayables++;

                            Log.i("DisplayList", "Size: " + String.valueOf(sizeOfDisplayables));

                            if (!summaryDisplayedList.contains(userName) && !nextPersonSelected[0]) {
                                nextPersonUserName[0] = fitWorkPerson.getFitPerson().getUserName();
                                summaryDisplayedList.add(nextPersonUserName[0]);
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
            if(mRealm!=null) {
                mRealm.close();
            }
        }

        return nextPersonUserName[0];

    }

    // UDP Functions

    /*public List<String> getSimUsers(){

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
    }*/

    /*public void addDataToMinAgg(final Map<String, List<RawData>> curDataMap, final List<String> curUDPList){

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


    }*/

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

    private double calcCalBurned(int calcAvgHr, FitPerson person){

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

    public void replicateRealms(){

        File dir = null;
        try {
            dir = new File(Environment.getExternalStorageDirectory(),
                        AppDefaults.REALM_REP_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }
        if(dir==null){
            return;
        }

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            // create a backup file
            File exportRealmFile;
            exportRealmFile = new File(dir, AppDefaults.REALM_REP_FILENAME);

            // if backup file already exists, delete it
            exportRealmFile.delete();

            // copy current realm to backup file
            mRealm.writeCopyTo(exportRealmFile);

        } catch (Exception e){
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        } finally {
            if(mRealm!=null){
                mRealm.close();
            }
        }

        RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                //.directory(Environment.getExternalStorageDirectory())
                .build();

        Realm logRealm = null;

        try {

            logRealm = Realm.getInstance(logConfig);

            // create a backup file
            File exportLogRealmFile;
            exportLogRealmFile = new File(dir, AppDefaults.REALMLOG_REP_FILENAME);

            // if backup file already exists, delete it
            exportLogRealmFile.delete();

            // copy current realm to backup file
            logRealm.writeCopyTo(exportLogRealmFile);

        } catch (Exception e){
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        } finally {
            if(logRealm!=null) {
                logRealm.close();
            }
        }

    }


    public void backupRealm() {

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            File dir = new File(Environment.getExternalStorageDirectory(),
                    AppDefaults.REALM_BKP_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            SimpleDateFormat df = new SimpleDateFormat(AppDefaults.TIME_FORMAT_BACKUP, Locale.getDefault());
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
            if(mRealm!=null) {
                mRealm.close();
            }
        }
    }

    public void backupLogRealm() {

        RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                //.directory(Environment.getExternalStorageDirectory())
                .build();

        Realm logRealm = null;

        try {

            logRealm = Realm.getInstance(logConfig);

            File dir = new File(Environment.getExternalStorageDirectory(),
                    AppDefaults.REALM_BKP_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            SimpleDateFormat df = new SimpleDateFormat(AppDefaults.TIME_FORMAT_BACKUP, Locale.getDefault());
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
            if(logRealm!=null) {
                logRealm.close();
            }
        }
    }


    public void processRawData(final RawData rawData, final boolean isTestUser){

        //Log.i("PROC_DATA", rawData.getDeviceNo() + ": " + rawData.getHeartRate() + " _RSSI: " + rawData.getRssi());

        final String hrSensorId = rawData.getDeviceNo();
        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            if(mRealm.where(FitWorkPerson.class).equalTo("actHrSensorId", hrSensorId).count() > 0){
                // Person is in activity
                new AddToMinDetailTask().execute(rawData);
                //addActivityMinDetailData(rawData);
            } else if(mRealm.where(FitPerson.class)
                    .equalTo("hrSensorId", hrSensorId)
                    .or()
                    .equalTo("hrTempSensorId", hrSensorId)
                    .count() > 0){
                // Person is in local
                if(!ProcessController.userAddingWorkList.contains(hrSensorId)) {
                    addPersonToActivity(rawData);
                }
            } else {
                if(!StaticVariables.isPersonInWaitingList(hrSensorId)
                        && !ProcessController.userUpdatingList.contains(hrSensorId)){
                    Log.i("PR_ROW_DATA", hrSensorId + ": " + "New person");
                    updateOrAddLocalUserData(hrSensorId, isTestUser, false, rawData.getHexString());
                }
            }

        } catch (Exception e){
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        } finally {
            if(mRealm!=null) {
                mRealm.close();
            }
        }

    }

    private class AddToMinDetailTask extends AsyncTask<RawData, Void, Void>{

        private String taskName = "AddToMinDetailTask";

        @Override
        protected Void doInBackground(RawData... rawDatas) {

            final RawData rawData = rawDatas[0];

            Realm mRealm = null;

            try {

                mRealm = Realm.getDefaultInstance();

                //StaticVariables.addPersonToProcess(rawData.getDeviceNo());

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        FitWork fitWork = realm.where(FitWork.class).findFirst();

                        FitPerson fitPerson = realm.where(FitPerson.class)
                                .equalTo("hrSensorId", rawData.getDeviceNo())
                                .or()
                                .equalTo("hrTempSensorId", rawData.getDeviceNo())
                                .findFirst();

                        FitWorkPerson fitWorkPerson = realm.where(FitWorkPerson.class)
                                .equalTo("actHrSensorId", rawData.getDeviceNo())
                                .findFirst();

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
                LoggerService.insertLog(taskName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                e.printStackTrace();
            } finally {
                if(mRealm!=null){
                    mRealm.close();
                }
            }

            return null;
        }
    }

    public void addDataToMinAgg(){

        new AddToMinAggTask().execute();

    }

    private class AddToMinAggTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            Realm mRealm = null;

            try {

                mRealm = Realm.getDefaultInstance();

                final Calendar timeOut = Calendar.getInstance();
                timeOut.add(Calendar.SECOND, -60);

                final Calendar midNight = Calendar.getInstance();
                midNight.set(Calendar.HOUR_OF_DAY, 0);
                midNight.set(Calendar.MINUTE, 0);
                midNight.set(Calendar.SECOND, 0);

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        RealmResults<FitWorkPerson> fitWorkPersons = realm.where(FitWorkPerson.class)
                                .isNotNull("fitPerson")
                                .greaterThan("hrLastDataUpdateDate", timeOut.getTime())
                                .findAll();

                        for (FitWorkPerson fitWorkPerson : fitWorkPersons) {

                            RealmResults<FitWorkDataMinDetail> minDetails =
                                    realm.where(FitWorkDataMinDetail.class)
                                            .equalTo("fitPerson.hrSensorId", fitWorkPerson.getActHrSensorId())
                                            .or()
                                            .equalTo("fitPerson.hrTempSensorId", fitWorkPerson.getActHrSensorId())
                                            .findAll();

                            if (minDetails.size()>0) {

                                int maxHr = minDetails.max("currentHr") != null ? minDetails.max("currentHr").intValue() : 0;
                                int avgHr = (int) minDetails.average("currentHr");
                                int avgPerf = calcPerf(avgHr, fitWorkPerson.getFitPerson().getMaxHr());
                                int avgZone = calcZone(avgHr, fitWorkPerson.getFitPerson().getMaxHr());
                                double calBurned = calcCalBurned(avgHr, fitWorkPerson.getFitPerson());

                                if(calBurned>0) {

                                    //Log.i("Yakilan Kalori: ", "Yakilan Kalori: " + calBurned);
                                    RealmResults<FitWorkDataMinAgg> minAggs =
                                            realm.where(FitWorkDataMinAgg.class)
                                                    .equalTo("fitPerson.hrSensorId", fitWorkPerson.getActHrSensorId())
                                                    .or()
                                                    .equalTo("fitPerson.hrTempSensorId", fitWorkPerson.getActHrSensorId())
                                                    .findAll();

                                    double calBurnedBefore = minAggs.sum("totalCal") != null ? minAggs.sum("totalCal").doubleValue() : 0;

                                    FitWorkDataMinAgg minAgg = new FitWorkDataMinAgg();
                                    Date insertDate = Calendar.getInstance().getTime();
                                    minAgg.setMinAggId(AppUtils.getMinAggId(insertDate, fitWorkPerson.getFitPerson().getUserName()));
                                    minAgg.setFitWork(fitWorkPerson.getFitWork());
                                    minAgg.setFitPerson(fitWorkPerson.getFitPerson());
                                    minAgg.setHrDataCount(minDetails.size());
                                    minAgg.setMaxHr(maxHr);
                                    minAgg.setAvgHr(avgHr);
                                    minAgg.setAvgPerf(avgPerf);
                                    minAgg.setAvgZone(avgZone);
                                    minAgg.setTotalCal(calBurned);
                                    minAgg.setInsertDate(insertDate);

                                    realm.copyToRealmOrUpdate(minAgg);

                                    fitWorkPerson.setTotalCal(calBurnedBefore + calBurned);

                                }

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
                if(mRealm!=null){
                    mRealm.close();
                }
            }

            return null;
        }
    }

    /*public void addDataToMinAggOld(){

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            final Calendar timeOut = Calendar.getInstance();
            timeOut.add(Calendar.SECOND, -60);

            final Calendar midNight = Calendar.getInstance();
            midNight.set(Calendar.HOUR_OF_DAY, 0);
            midNight.set(Calendar.MINUTE, 0);
            midNight.set(Calendar.SECOND, 0);

            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {


                    RealmResults<FitWorkPerson> fitWorkPersons = realm.where(FitWorkPerson.class)
                            .isNotNull("fitPerson")
                            .greaterThan("hrLastDataUpdateDate", timeOut.getTime())
                            .findAll();

                    for (FitWorkPerson fitWorkPerson : fitWorkPersons) {

                        FitPerson fitPerson = realm.where(FitPerson.class).equalTo("hrSensorId", fitWorkPerson.getActHrSensorId()).findFirst();
                        RealmResults<FitWorkDataMinDetail> minDetails =
                                realm.where(FitWorkDataMinDetail.class)
                                        .equalTo("fitPerson.hrSensorId", fitWorkPerson.getActHrSensorId()).findAll();

                        if (fitPerson != null && minDetails.size()>0) {

                            int maxHr = minDetails.max("currentHr") != null ? minDetails.max("currentHr").intValue() : 0;
                            int avgHr = (int) minDetails.average("currentHr");
                            int avgPerf = calcPerf(avgHr, fitPerson.getMaxHr());
                            int avgZone = calcZone(avgHr, fitPerson.getMaxHr());
                            double calBurned = calcCalBurned(avgHr, fitPerson);

                            //Log.i("Yakilan Kalori: ", "Yakilan Kalori: " + calBurned);

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

                            fitWorkPerson.setTotalCal(fitWorkPerson.getTotalCal() + calBurned);

                        }

                    }

                    // Delete minDetail data
                    realm.where(FitWorkDataMinDetail.class).findAll().deleteAllFromRealm();
                }
            });


            *//*mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkPerson> fitWorkPersons = realm.where(FitWorkPerson.class)
                            .isNotNull("fitPerson")
                            .greaterThan("hrLastDataUpdateDate", timeOut.getTime())
                            .findAll();

                    for(FitWorkPerson fitWorkPerson : fitWorkPersons){

                        Number totalCalNum = realm.where(FitWorkDataMinAgg.class)
                                .equalTo("fitPerson.hrSensorId", fitWorkPerson.getActHrSensorId())
                                .greaterThan("insertDate", midNight.getTime())
                                .sum("totalCal");

                        if (totalCalNum != null) {
                            double totalCal = totalCalNum.doubleValue();
                            fitWorkPerson.setTotalCal(totalCal);
                        }

                    }



                }
            });*//*


        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }


    }*/


    /*private void addActivityMinDetailData(final RawData rawData){

        try {

            Realm mRealm = Realm.getDefaultInstance();

            //StaticVariables.addPersonToProcess(rawData.getDeviceNo());

            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {


                    FitWork fitWork = realm.where(FitWork.class).findFirst();
                    FitPerson fitPerson = realm.where(FitPerson.class).equalTo("hrSensorId", rawData.getDeviceNo()).findFirst();
                    FitWorkPerson fitWorkPerson = realm.where(FitWorkPerson.class)
                            .equalTo("actHrSensorId", rawData.getDeviceNo()).findFirst();

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
        }

    }*/


    /*private void addActivityMinDetailData(final RawData rawData){

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

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
            if(mRealm!=null) {
                mRealm.closeDialog();
            }
            //StaticVariables.removePersonFromProcess(rawData.getDeviceNo());
        }

    }*/


    private void addPersonToActivity(final RawData rawData){

        ProcessController.addToUserAddingWorkList(rawData.getDeviceNo());

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            //StaticVariables.addPersonToProcess(rawData.getDeviceNo());

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    final Calendar midNight = Calendar.getInstance();
                    midNight.set(Calendar.HOUR_OF_DAY, 0);
                    midNight.set(Calendar.MINUTE, 0);
                    midNight.set(Calendar.SECOND, 0);

                    FitWork fitWork = realm.where(FitWork.class).findFirst();
                    FitPerson fitPerson = realm.where(FitPerson.class)
                            .equalTo("hrSensorId", rawData.getDeviceNo())
                            .or()
                            .equalTo("hrTempSensorId", rawData.getDeviceNo())
                            .findFirst();

                    double prevTotalCal = 0;
                    Number prevTotalCalNum = realm.where(FitWorkDataMinAgg.class)
                            .beginGroup()
                            .equalTo("fitPerson.hrSensorId", rawData.getDeviceNo())
                            .or()
                            .equalTo("fitPerson.hrTempSensorId", rawData.getDeviceNo())
                            .endGroup()
                            .greaterThan("insertDate", midNight.getTime())
                            .sum("totalCal");
                    if(prevTotalCalNum!=null){
                        prevTotalCal = prevTotalCalNum.doubleValue();
                    }

                    FitWorkPerson fitWorkPerson = new FitWorkPerson();
                    fitWorkPerson.setFitWork(fitWork);
                    fitWorkPerson.setFitPerson(fitPerson);
                    fitWorkPerson.setActHrSensorId(rawData.getDeviceNo());
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
            if(mRealm!=null) {
                mRealm.close();
            }
            //StaticVariables.removePersonFromProcess(rawData.getDeviceNo());
            new AddToMinDetailTask().execute(rawData);
            //addActivityMinDetailData(rawData);

            ProcessController.removeFromUserAddingWorkList(rawData.getDeviceNo());
            //fwActivity.updateUI();
        }

    }

    public void removeExpiredPersonsFromActivity(){

        Realm mRealm = null;

        try {

            final Calendar midNight = Calendar.getInstance();
            midNight.set(Calendar.HOUR_OF_DAY, 0);
            midNight.set(Calendar.MINUTE, 0);
            midNight.set(Calendar.SECOND, 0);

            mRealm = Realm.getDefaultInstance();

            final Calendar timeOut = Calendar.getInstance();
            timeOut.add(Calendar.SECOND, -60);

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    realm.where(FitWorkPerson.class)
                            .lessThan("hrLastDataUpdateDate", timeOut.getTime())
                            .or()
                            .lessThan("addedDate", midNight.getTime())
                            .findAll().deleteAllFromRealm();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if(mRealm!=null) {
                mRealm.close();
            }
        }

    }



    public List<FitWorkPersonCard> getCurrentCardList(){

        final List<FitWorkPersonCard> cards = new ArrayList<FitWorkPersonCard>();

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitWorkPerson> results = realm.where(FitWorkPerson.class)
                            .isNotNull("fitPerson")
                            .findAllSorted("addedDate");

                    for (FitWorkPerson person :results){

                        FitWorkPersonCard card = new FitWorkPersonCard();
                        card.setNickName(person.getFitPerson().getNickName());
                        card.setGender(person.getFitPerson().getGender());
                        card.setCurrentHr(person.getCurrentHr());
                        card.setCaloriesBurned((int) Math.round(person.getTotalCal()));
                        card.setCurrentZone(person.getCurrentZone());
                        card.setCurrentPerf(person.getCurrentPerf());
                        card.setImageUrl(person.getFitPerson().getPhotoURL());

                        cards.add(card);
                    }

                }
            });

        } catch (Exception e){
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if(mRealm!=null) {
                mRealm.close();
            }
        }

        return cards;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler("/mnt/sdcard/"));

        initService();

        //Toast.makeText(this, "DatabaseService Started", Toast.LENGTH_LONG).show();
        Log.i("DatabaseService", "DatabaseService Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(antReceiver);

        Log.i("DatabaseService", "DatabaseService Destroyed");
        //Toast.makeText(this, "DatabaseService Destroyed", Toast.LENGTH_SHORT).show();
    }

    public class DatabaseServiceBinder extends Binder {
        DatabaseService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DatabaseService.this;
        }
    }


    private BroadcastReceiver antReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                // Get extra data included in the Intent
                RawData rawData = (RawData) intent.getSerializableExtra(AppDefaults.DATA_MSG);

                processRawData(rawData, false);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

}
