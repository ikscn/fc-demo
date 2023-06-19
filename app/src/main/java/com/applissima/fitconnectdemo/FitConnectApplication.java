package com.applissima.fitconnectdemo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;
import android.support.multidex.MultiDex;
import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.leakcanary.LeakCanary;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by ilkerkuscan on 14/06/17.
 */

public class FitConnectApplication extends Application {

    public static FitConnectApplication instance;
    private SharedPreferences prefs;

    /*private RefWatcher refWatcher;
    public static RefWatcher getRefWatcher(Context context) {
        FitConnectApplication application = (FitConnectApplication) context.getApplicationContext();
        return application.refWatcher;
    }*/

    @Override
    public void onCreate() {
        super.onCreate();

        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/

        instance = this;

        prefs = getSharedPreferences(AppDefaults.PREFS_NAME, MODE_PRIVATE);

        /*Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(instance,
                Environment.getExternalStorageDirectory().getPath()));*/

        //startAnrListener();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Init Realm
        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALM_FILENAME)
                //.directory(Environment.getExternalStorageDirectory())
                .build();

        Realm.setDefaultConfiguration(config);

    }

    private void startAnrListener(){

        new ANRWatchDog(10000)
                .setIgnoreDebugger(true)
                .setANRListener(new ANRWatchDog.ANRListener() {
                    @Override
                    public void onAppNotResponding(ANRError error) {
                        // Handle the error. For example, log it to HockeyApp:
                        Writer writer = new StringWriter();
                        error.printStackTrace(new PrintWriter(writer));
                        String errorStr = writer.toString();

                        error.printStackTrace();
                        //LoggerService.insertLog("ANRWatchDog", error.getMessage(), errorStr);
                /*new RemoteLogCat().log("FCSite_" + Settings.locationId + ": " + "ANRWatchDog",
                        errorStr,
                        AppDefaults.REMOTE_LOGCAT_APIKEY);*/

                        String crashFileName = AppUtils.writeToFile(errorStr, AppDefaults.CRASH_ANR);

                        prefs.edit().putBoolean("isCrashed", true).apply();
                        prefs.edit().putString("crashType", AppDefaults.CRASH_ANR).apply();
                        prefs.edit().putString("crashFileName", crashFileName).apply();

                        try {
                            Thread.sleep(2000);
                            Process.killProcess(Process.myPid());
                            System.exit(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static FitConnectApplication getInstance() {
        return instance;
    }

    public SharedPreferences getPrefs(){
        return prefs;
    }

}
