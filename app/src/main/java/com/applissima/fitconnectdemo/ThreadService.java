package com.applissima.fitconnectdemo;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by ilkerkuscan on 16/05/17.
 */

public class ThreadService extends Service {

    private static final String clsName = "ThreadService";
    private static HandlerThread mHandlerThread;
    private static Handler mHandler;
    private static Messenger messageHandler;

    private static DatabaseService dbService;
    private boolean mBoundDbService;

    private static int countForCheckNetwork;
    private boolean uploadInProgress = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        countForCheckNetwork = 0;

        messageHandler = (Messenger) intent.getExtras().get("MESSENGER");

        Intent dbIntent = new Intent(this, DatabaseService.class);
        bindService(dbIntent, mDbConnection, Context.BIND_AUTO_CREATE);

        Log.i("ThreadService", "ThreadService bounded to DBService");
        Log.i("ThreadService", "ThreadService Started");

        mHandlerThread = new HandlerThread("HandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mHandler.postDelayed(runTimerClock, 2 * 1000);
        mHandler.postDelayed(runUI, 3 * 1000);
        mHandler.postDelayed(runCheckNetworks, 5 * 1000);
        mHandler.postDelayed(runUpdateNetworkSigns, 6 * 1000);
        mHandler.postDelayed(runProfileUpdates, Settings.checkProfileUpdates * 60 * 1000);
        mHandler.postDelayed(runLogStatus, AppUtils.getNextQuarterDiff());
        mHandler.postDelayed(runUploadLogs, Settings.uploadLogs * 60 * 1000);
        mHandler.postDelayed(runUploadMinAgg, Settings.uploadMinAgg * 60 * 1000);
        mHandler.postDelayed(runWorkSummary, Settings.workSumPeriod * 60 * 1000 + 5000);
        mHandler.postDelayed(runRefreshWaitingList, Settings.checkUnknownProfile * 60 * 1000);
        mHandler.postDelayed(runGenDataSimUsers, 2000);
        mHandler.postDelayed(runMinute, 60 * 1000);
        mHandler.postDelayed(runClearData, 20 * 1000);
        mHandler.postDelayed(runEndOfDay, AppUtils.getEndOfDayTimeDiff());
        mHandler.postDelayed(runHandleTempBelts, 10 * 1000);

        return START_NOT_STICKY;
    }

    // *******  ThreadService RUNNABLES   ********* //
    // Runnable 1
    private Runnable runUI = new Runnable() {
        @Override
        public void run() {

            if(dbService!=null) {

                try {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Message msg = new Message();
                            msg.what = AppDefaults.EVENT_CARDS;
                            msg.obj = new EventCards(dbService.getCurrentCardList());
                            try {
                                messageHandler.send(msg);
                            } catch (RemoteException e) {
                                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                                        + ":" + e.getStackTrace()[0].getLineNumber());
                                e.printStackTrace();
                            }

                        }
                    }).start();

                } catch (Exception e) {
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                    e.printStackTrace();
                }

            }

            mHandler.postDelayed(runUI, 1000);

        }
    };


    // Runnable 2
    private Runnable runProfileUpdates = new Runnable() {
        @Override
        public void run() {

            if(AppUtils.isInternetConnected()) {

                try {

                    dbService.updatePersonInfo();

                } catch (Exception e) {
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                    e.printStackTrace();
                }

            }

            mHandler.postDelayed(runProfileUpdates, Settings.checkProfileUpdates * 60 * 1000);

        }
    };


    // Runnable 3
    private Runnable runUploadLogs = new Runnable() {
        @Override
        public void run() {

            if(AppUtils.isInternetConnected()) {

                try {
                    UploadLogsTask uploadLogsTask = new UploadLogsTask();
                    uploadLogsTask.execute();
                } catch (Exception e) {
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                    e.printStackTrace();
                }

            }
            mHandler.postDelayed(runUploadLogs, Settings.uploadLogs * 60 * 1000);

        }
    };

    // Runnable 4
    private Runnable runUploadMinAgg = new Runnable() {
        @Override
        public void run() {

            if(AppUtils.isInternetConnected()) {

                try {

                    UploadDataMinAggTask uploadMinAggTask = new UploadDataMinAggTask();
                    uploadMinAggTask.execute();

                } catch (Exception e) {
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                    e.printStackTrace();
                }
            }

            mHandler.postDelayed(runUploadMinAgg, Settings.uploadMinAgg * 60 * 1000);

        }
    };

    // Runnable 5
    private Runnable runWorkSummary = new Runnable() {
        @Override
        public void run() {

            try {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            showWorkSummary();
                            Thread.sleep(Settings.workSumDuration * 1000);
                            hideWorkSummary();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();



            } catch (Exception e) {
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                e.printStackTrace();
            }

            mHandler.postDelayed(runWorkSummary, Settings.workSumPeriod * 60 * 1000);

        }
    };


    // Runnable 6
    private Runnable runCheckNetworks = new Runnable() {
        @Override
        public void run() {

            try {

                countForCheckNetwork ++;

                AppUtils.checkNetworks(new WeakReference<Context>(ThreadService.this));

            } catch (Exception e){
                e.printStackTrace();
            }

            // 1 minute passed -> Update signs
            if(countForCheckNetwork == 4){

                mHandler.postDelayed(runUpdateNetworkSigns, 1000);

                countForCheckNetwork = 0;
            }

            // Run every 15 seconds
            mHandler.postDelayed(runCheckNetworks, 15 * 1000);

        }
    };

    // Runnable 7
    private Runnable runUpdateNetworkSigns = new Runnable() {
        @Override
        public void run() {

            try {

                // Reset hublist
                Settings.hubList = new ArrayList<String>();

                UpdateNetworkSigns updateSignsTask = new UpdateNetworkSigns();
                updateSignsTask.execute();

            } catch (Exception e) {
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                e.printStackTrace();
            }

        }
    };


    // Runnable 8
    private Runnable runLogStatus = new Runnable() {
        @Override
        public void run() {

            try {

                LoggerService.insertLogStatusMessage();

            } catch (Exception e) {
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                e.printStackTrace();
            }

            mHandler.postDelayed(runLogStatus, 15 * 60 * 1000);

        }
    };

    // Runnable 9
    private Runnable runRefreshWaitingList = new Runnable() {
        @Override
        public void run() {

            try {

                refreshWaitingList();

            } catch (Exception e) {
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                e.printStackTrace();
            }

            mHandler.postDelayed(runRefreshWaitingList, Settings.checkUnknownProfile * 60 * 1000);

        }
    };

    // Runnable 10
    private Runnable runGenDataSimUsers = new Runnable() {
        @Override
        public void run() {

            try {
                if (Settings.simulationPersonCount > 0){
                    generateRandomDataForSimUsers();
                }
            } catch (Exception e) {
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                e.printStackTrace();
            }

            mHandler.postDelayed(runGenDataSimUsers, 500);

        }
    };

    // Runnable 11
    private Runnable runMinute = new Runnable() {
        @Override
        public void run() {

            try {

                dbService.compactRealms();
                dbService.addDataToMinAgg();

            } catch (Exception e){
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                e.printStackTrace();
            }

            mHandler.postDelayed(runMinute, 60 * 1000);

        }
    };


    //Runnable 12
    private Runnable runClearData = new Runnable() {
        @Override
        public void run() {

            try {
                checkCrashes();

                // Replicate Realm to SDCard from rootFile
                dbService.replicateRealms();

                // Clear expired data
                dbService.removeExpiredPersonsFromActivity();
                dbService.deleteExpiredData();
                dbService.clearExpiredLogs();

                // Clear backup files - older than 10 days
                AppUtils.clearExpiredFiles(AppDefaults.REALM_BKP_DIR, 10);

                // Clear crash files - older than 15 days
                AppUtils.clearExpiredFiles(AppDefaults.CRASH_REPORTS_DIR, 15);


            } catch (Exception e) {
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                e.printStackTrace();
            }

            mHandler.postDelayed(runClearData, 60 * 1000);

        }
    };


    //Runnable 13
    private Runnable runEndOfDay = new Runnable() {
        @Override
        public void run() {

            if (AppUtils.isInternetConnected()) {
                try {

                    dbService.backupRealm();
                    dbService.backupLogRealm();

                    sendMailEndOfDay();

                } catch (Exception e) {
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                    e.printStackTrace();
                }

                mHandler.postDelayed(runEndOfDay, 24 * 60 * 60 * 1000);

            } else {
                // Retry in 30 minutes
                mHandler.postDelayed(runEndOfDay, 30 * 60 * 1000);
            }
        }
    };


    //Runnable 14
    private Runnable runTimerClock = new Runnable() {
        @Override
        public void run() {

            try {

                Message msg = new Message();
                msg.what = AppDefaults.EVENT_TIMER;
                messageHandler.send(msg);

            } catch (Exception e) {
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                e.printStackTrace();
            }

            mHandler.postDelayed(runTimerClock, 5 * 1000);

        }
    };


    //Runnable 15
    private Runnable runHandleTempBelts = new Runnable() {
        @Override
        public void run() {

            try {

                Message msg = new Message();
                msg.what = AppDefaults.EVENT_TEMP_BELTS;
                try {
                    messageHandler.send(msg);
                } catch (RemoteException e) {
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                    e.printStackTrace();
                }

                // Delete expired temp belts
                Message msgDelete = new Message();
                msgDelete.what = AppDefaults.EVENT_EXP_TEMP_BELTS;
                try {
                    messageHandler.send(msgDelete);
                } catch (RemoteException e) {
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                    e.printStackTrace();
                }

            } catch (Exception e) {
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                e.printStackTrace();
            }

            mHandler.postDelayed(runHandleTempBelts, Settings.checkTempBelts * 30 * 1000);

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(runTimerClock);
        mHandler.removeCallbacks(runUI);
        mHandler.removeCallbacks(runCheckNetworks);
        mHandler.removeCallbacks(runUpdateNetworkSigns);
        mHandler.removeCallbacks(runProfileUpdates);
        mHandler.removeCallbacks(runWorkSummary);
        mHandler.removeCallbacks(runUploadMinAgg);
        mHandler.removeCallbacks(runUploadLogs);
        mHandler.removeCallbacks(runLogStatus);
        mHandler.removeCallbacks(runRefreshWaitingList);
        mHandler.removeCallbacks(runGenDataSimUsers);
        mHandler.removeCallbacks(runMinute);
        mHandler.removeCallbacks(runClearData);
        mHandler.removeCallbacks(runEndOfDay);
        mHandler.removeCallbacks(runHandleTempBelts);

        unbindService(mDbConnection);

        Log.i("ThreadService", "ThreadService Destroyed");
    }

    private ServiceConnection mDbConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            DatabaseService.DatabaseServiceBinder binder = (DatabaseService.DatabaseServiceBinder) service;
            dbService = binder.getService();
            mBoundDbService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBoundDbService = false;
        }
    };

    private static class UpdateNetworkSigns extends AsyncTask<Void, Void, Void> {

        boolean isWifiOn = false;
        boolean isInternetOn = false;
        boolean[] networkStatus = new boolean[3];

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                if(StaticVariables.cntWifiOn > 0){
                    Log.i("Wifi Check", "Wifi is connected!!!");
                    isWifiOn = true;
                    if(StaticVariables.cntInternetOn > 0){
                        Log.i("Internet Check", "Internet is available!!!");
                        isInternetOn = true;
                    } else {
                        Log.i("Internet Check", "Internet is not available!!!");
                        isInternetOn = false;
                    }

                } else {
                    Log.i("Wifi Check", "Wifi is not connected.");
                    isWifiOn = false;
                }

            } catch (Exception e){
                e.printStackTrace();
                LoggerService.insertLog(clsName, "Error while network connection checking!", e.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            networkStatus[0] = isWifiOn;
            networkStatus[1] = isInternetOn;

            Message msg = new Message();
            msg.what = AppDefaults.EVENT_NETWORKS;
            msg.obj = new EventNetworks(networkStatus);
            try {
                messageHandler.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Reset counts
            StaticVariables.initNetworkCounts();

            super.onPostExecute(aVoid);
        }
    }

    private static class UploadLogsTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            final OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            final String url = AppDefaults.API_INSERTLOGDATA_URL;

            try {

                String jsonStr = dbService.getLogsForUpdate();

                String encryptedString = AppUtils.getEncryptedString(APIRequestType.UPLOAD_LOG);

                RequestBody formBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("email", "etezel@gmail.com")
                        .addFormDataPart("sessionid", encryptedString)
                        .addFormDataPart("logData", jsonStr)
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

                        LoggerService.insertLog(clsName, "Error on Logs Upload", errorStr);
                        Log.e("OKHTTP", errorStr);

                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {

                        String responseString = response.body().string();

                        try {

                            JSONObject responseJSON = new JSONObject(responseString);
                            if(responseJSON.getJSONObject("status").getBoolean("successful")){

                                Log.i("OKHTTP", "SUCCESS on Logs Upload!!!");
                                LoggerService.insertLog(clsName, "Upload Logs DONE.",
                                        "SUCCESS on Logs Upload!!!");

                                dbService.saveLogUpdates();

                            } else {
                                String responseMessage = responseJSON.getJSONObject("status").getString("message");
                                Log.i("OKHTTP", "FAILURE on Logs Upload!!!");
                                Log.i("OKHTTP", "FAILURE RESPONSE: " + responseMessage);
                                LoggerService.insertLog(clsName, "FAILURE on Logs Upload!!!",
                                        responseMessage);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            LoggerService.insertLog(clsName, "JSONException on Logs Upload Response",
                                    null);
                        }
                    }
                });


            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

    private static class UploadDataMinAggTask extends AsyncTask<Void, Void, Void> {

        // Uploads aggregated data to the server

        @Override
        protected Void doInBackground(Void... voids) {

            final int LIMIT = Settings.maxUploadData;
            final JSONArray jsonArray = new JSONArray();

            // Prepare data
            Realm mRealm = null;

            try {

                mRealm = Realm.getDefaultInstance();

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(final Realm realm) {

                        RealmResults<FitWorkDataMinAgg> results =
                                realm.where(FitWorkDataMinAgg.class)
                                        .equalTo("uploadSuccessful", false)
                                        .findAllSorted("insertDate");

                        if (results != null && results.size() > 0) {

                            List<FitWorkDataMinAgg> limitedResults;

                            if(results.size()>LIMIT){
                                limitedResults = results.subList(0, LIMIT);
                            } else {
                                limitedResults = results.subList(0, results.size());
                            }

                            Log.i("Upload MinAgg", "UPLOAD MINAGG Total Count: " + results.size());

                            for (FitWorkDataMinAgg minAgg : limitedResults) {

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
                if(mRealm!=null) {
                    mRealm.close();
                }
            }

            if(jsonArray.length()>0) {

                // Send data
                try {

                    final OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .build();

                    String url = AppDefaults.API_INSERTDATA_URL;
                    String jsonStr = String.valueOf(jsonArray);

                    Log.i("ActivityData", jsonStr);

                    String encryptedString = AppUtils.getEncryptedString(APIRequestType.UPLOAD_MINAGG);

                    RequestBody formBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("email", "etezel@gmail.com")
                            .addFormDataPart("sessionid", encryptedString)
                            .addFormDataPart("activityData", jsonStr)
                            .build();

                    okhttp3.Request okHttprequest = new okhttp3.Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();

                    client.newCall(okHttprequest).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                            String errorStr = "";
                            if (e.getMessage() != null) {
                                errorStr = e.getMessage();
                            } else if (e.getLocalizedMessage() != null) {
                                errorStr = e.getLocalizedMessage();
                            } else if (e.getStackTrace() != null && e.getStackTrace().length > 0) {
                                errorStr = e.getStackTrace()[0].toString();
                            } else {
                                errorStr = "Unknown Error.";
                            }

                            LoggerService.insertLog(clsName, "Error on MinAgg Upload", errorStr);
                            Log.e("OKHTTP", errorStr);

                        }

                        @Override
                        public void onResponse(Call call, okhttp3.Response response) throws IOException {

                            String responseString = response.body().string();

                            try {

                                JSONObject responseJSON = new JSONObject(responseString);
                                if (responseJSON.getJSONObject("status").getBoolean("successful")) {

                                    Log.i("OKHTTP", "SUCCESS on MinAgg Upload!!!");

                                    dbService.saveMinAggUploads(LIMIT);

                                } else {
                                    String responseMessage = responseJSON.getJSONObject("status").getString("message");
                                    Log.i("OKHTTP", "FAILURE on MinAgg Upload!!!");
                                    Log.i("OKHTTP", "FAILURE RESPONSE: " + responseMessage);
                                    LoggerService.insertLog(clsName, "FAILURE on MinAgg Upload!!!",
                                            responseMessage);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                LoggerService.insertLog(clsName, "JSONException on minAgg Upload Response",
                                        null);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            return null;
        }

    }


    private static void showWorkSummary(){

        try {

            PrepareSummaryPersonTask prepTask = new PrepareSummaryPersonTask();
            prepTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }

    }

    private static void hideWorkSummary(){

        Message msg = new Message();
        msg.what = AppDefaults.EVENT_WORKSUMMARY;
        msg.obj = new EventWorkSummary(null, false);

        try {
            messageHandler.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private static class PrepareSummaryPersonTask extends AsyncTask<Void, Void, SummaryWorkPerson>{

        @Override
        protected SummaryWorkPerson doInBackground(Void... voids) {

            return dbService.prepareSummaryPerson();
        }

        @Override
        protected void onPostExecute(SummaryWorkPerson summaryWorkPerson) {

            if(summaryWorkPerson==null){
                return;
            }

            Message msg = new Message();
            msg.what = AppDefaults.EVENT_WORKSUMMARY;
            msg.obj = new EventWorkSummary(summaryWorkPerson, true);

            try {
                messageHandler.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
            }

            super.onPostExecute(summaryWorkPerson);

        }
    }

    private static void refreshWaitingList(){

        StaticVariables.waitingList = new ArrayList<String>();

    }

    private static void generateRandomDataForSimUsers(){

        if(Settings.simulationPersonCount == 0){
            return;
        }

        try {

            for(int i=0; i<Settings.simulationPersonCount; i++){
                String beltId = SimulationService.simulationBelts[i];
                final RawData rawData = generateRandomRawData(beltId);

                dbService.processRawData(rawData, true);
            }

        } catch (Exception e) {
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        }
    }

    private static RawData generateRandomRawData(String deviceNo){

        Random rand = new Random();
        int heartRate = rand.nextInt(81) + 80; // between 80 & 160
        int deviceType = 1;
        int rssi = rand.nextInt(100) - 50; // between -50 & 50

        RawData data = new RawData(heartRate, deviceNo, deviceType, rssi);

        return data;
    }


    public static void sendMailEndOfDay(){

        try {

            SimpleDateFormat df = new SimpleDateFormat(AppDefaults.DATE_FORMAT_DEFAULT, Locale.getDefault());
            String dateStr = df.format(Calendar.getInstance().getTime());

            List<String> toList = new ArrayList<String>();
            toList.add(AppDefaults.GMAIL_USER);

            List<File> fileList = new ArrayList<File>();
            SimpleDateFormat bdf = new SimpleDateFormat(AppDefaults.TIME_FORMAT_BACKUP, Locale.getDefault());
            String backupFileName = "RealmBackup_" + bdf.format(Calendar.getInstance().getTime()) + ".realm";
            String backupLogFileName = "RealmLogBackup_" + bdf.format(Calendar.getInstance().getTime()) + ".realm";

            File attachmentFile = new File(new File(Environment.getExternalStorageDirectory(),
                    AppDefaults.REALM_BKP_DIR), backupFileName);
            File attachmentFile2 = new File(new File(Environment.getExternalStorageDirectory(),
                    AppDefaults.REALM_BKP_DIR), backupLogFileName);

            fileList.add(attachmentFile);
            fileList.add(attachmentFile2);

            List<File> crashReports = AppUtils.getTodaysCrashReports();
            if(crashReports!=null && crashReports.size()>0){
                for(File report :crashReports){
                    fileList.add(report);
                }
            }

            MailData mailData = new MailData();
            mailData.prepareDefault();
            mailData.setMailType(MailType.ENDOFDAY);
            mailData.setEmailSubject("FitConnect Daily Report - " + dateStr + " - " + Settings.locationId + " | " + Settings.siteId);
            mailData.setEmailBody("");
            mailData.setFileList(fileList);

            new SendMailTask().execute(mailData);

        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }

    }

    private static void checkCrashes(){

        try {

            SharedPreferences prefs = FitConnectApplication.getInstance().getPrefs();

            if(prefs.getBoolean("isCrashed", false)){

                String crashType = prefs.getString("crashType", "");
                String crashFileName = prefs.getString("crashFileName", "");

                Log.i("AFTERCRASH", "App is restarted after a "  + crashType + " with FileName: " + crashFileName);
                LoggerService.insertLog(clsName, "App is restarted after a " + crashType, "FileName: " + crashFileName);

                if(AppUtils.isInternetConnected()) {

                    MailData mailData = new MailData();
                    mailData.prepareDefault();
                    mailData.setMailType(MailType.CRASH);

                    mailData.setEmailSubject(crashType + " on Device " + Settings.locationId + "-" + Settings.siteId);
                    mailData.setEmailBody("");
                    if (!"".equals(crashFileName)) {
                        File crashFile = new File(
                                new File(Environment.getExternalStorageDirectory(), AppDefaults.CRASH_REPORTS_DIR),
                                crashFileName);
                        mailData.attachFile(crashFile);
                        new SendMailTask().execute(mailData);
                    } else {
                        clearPreferences();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void clearPreferences(){
        SharedPreferences prefs = FitConnectApplication.getInstance().getPrefs();
        prefs.edit().clear().apply();
    }


    private static class SendMailTask extends AsyncTask<MailData, Void, Boolean> {

        private final String clsName = "SendMailTask";
        private MailType mailType;

        @Override
        protected Boolean doInBackground(MailData... mailData) {

            try {

                mailType = mailData[0].getMailType();

                Log.i("SendMailTask", "About to instantiate GMail...");
                GmailSender androidEmail = new GmailSender(mailData[0]);
                androidEmail.createEmailMessage();
                androidEmail.sendEmail();
                return true;
            } catch (Exception e) {
                Log.e("SendMailTask", e.getMessage(), e);
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSent) {

            if(isSent && mailType.equals(MailType.CRASH)){
                clearPreferences();
            }

            super.onPostExecute(isSent);
        }
    }


}
