package com.applissima.fitconnectdemo;

import android.os.AsyncTask;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by ilkerkuscan on 06/03/17.
 */

public class LoggerService {

    private static final String clsName = "LoggerService";
    private static final String TAG = "-- LOGGER --";



    public static void insertLogStatusMessage(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                //String networkStatus = StaticVariables.cntWifiOn > 0 ? "Wifi is connected." :"Wifi is not connected.";
                String networkStatusText = "Wifi connected: '%" + StaticVariables.getNetworkOnPerc("wifi")
                        + "' Internet connected: '%" + StaticVariables.getNetworkOnPerc("internet")
                        + "' Hub connected: '%" + StaticVariables.getNetworkOnPerc("hub") + "'";
                insertLog(clsName, "Network Info -->  " + networkStatusText, null);

                StaticVariables.initNetworkStatus();

                String fullMessage = buildStatusMessage();
                insertLog(clsName, fullMessage, null);

                Log.i(TAG, "Status Log Message: " + fullMessage);

            }
        }).start();



    }

    public static void insertLog(final String className, final String message, final String errorDesc){

        final String nullMessage = "N/A";

        RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                //.directory(Environment.getExternalStorageDirectory())
                //.modules(MainActivity.FitConLogModule.class)
                .build();

        Realm logRealm = null;

        try {

            logRealm = Realm.getInstance(logConfig);

            logRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    LogData logData = realm.createObject(LogData.class);
                    logData.setInsertDate(Calendar.getInstance().getTime());
                    logData.setSourceType(AppDefaults.LOG_SRC_TYPE);
                    logData.setSourceId("COLL" + Settings.siteId + "001");
                    logData.setLocationId(String.valueOf(Settings.locationId));
                    logData.setInsertedByClass(className);
                    if(message!=null) {
                        if (message.length() > 250) {
                            logData.setMessage(message.substring(0, 249));
                        } else {
                            logData.setMessage(message);
                        }
                    } else {
                        logData.setMessage(nullMessage);
                    }
                    if (errorDesc != null) {
                        if (errorDesc.length() > 250) {
                            logData.setErrorDesc(errorDesc.substring(0, 249));
                        } else {
                            logData.setErrorDesc(errorDesc);
                        }
                    } else {
                        logData.setErrorDesc(nullMessage);
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

        // Remote LogCat
        new RemoteLogCat().log("FCSite_" + Settings.locationId + "_" + Settings.siteId + ": " + className,
                (message==null?nullMessage:message) + " " + (errorDesc==null?nullMessage:errorDesc),
                AppDefaults.REMOTE_LOGCAT_APIKEY);

    }

    private static String buildStatusMessage(){

        final StatusData statusData = new StatusData();

        final List<String> todaysPersons = new ArrayList<String>();

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    statusData.setPersonCount((int) realm.where(FitPerson.class).count());

                    RealmResults<FitWorkDataMinAgg> minAggResults =
                            realm.where(FitWorkDataMinAgg.class).findAll();

                    for (FitWorkDataMinAgg minAgg :minAggResults){
                        if(minAgg.getFitPerson()!=null && !todaysPersons.contains(minAgg.getFitPerson().getHrSensorId())){
                            todaysPersons.add(minAgg.getFitPerson().getHrSensorId());
                        }
                    }

                    statusData.setActivityPersonCount(todaysPersons.size());

                    statusData.setActivityDataMinAggCount(minAggResults.size());

                    statusData.setActivityDataNotUploadedCount((int) realm.where(FitWorkDataMinAgg.class)
                            .equalTo("uploadSuccessful", false)
                            .count());

                    statusData.setFitConRealmSize((new File(realm.getPath())).length());

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(mRealm!=null) {
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

            logRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    statusData.setLogDbTotalCount((int) realm.where(LogData.class).count());

                    statusData.setLogDbNotUploadedCount((int) realm.where(LogData.class)
                            .equalTo("uploadSuccessful", false)
                            .count());

                    statusData.setLogDbRealmSize((new File(realm.getPath())).length());

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(logRealm!=null) {
                logRealm.close();
            }
        }


        Log.i("RealmFileSize", "RealmFileSize: " + statusData.getFitConRealmSize() + " bytes");
        Log.i("RealmLogFileSize", "RealmLogFileSize: " + statusData.getLogDbRealmSize() + " bytes");

        return statusData.toString();
    }

}
