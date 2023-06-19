package com.applissima.fitconnectdemo;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Process;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;

/**
 * Created by ilkerkuscan on 13/02/17.
 */

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final String clsName = "UncaughtExceptionHandler";
    private Activity activity;
    //private final Context mContext;
    //private final Class<?> myActivityClass;
    private SharedPreferences prefs;
    private WeakReference<FitWorkActivity> mActivity;

    //private Thread.UncaughtExceptionHandler defaultUEH;
    //private String localPath;

    /*public CustomExceptionHandler(Activity act, String localPath) {

        this.activity = act;
        this.localPath = localPath;
        //Getting the the default exception handler
        //that's executed when uncaught exception terminates a thread
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }*/

    public CustomExceptionHandler(FitWorkActivity activity, Class<?> c) {
        mActivity = new WeakReference<FitWorkActivity>(activity);
        //myActivityClass = c;
        if(mActivity.get()!=null) {
            prefs = mActivity.get().getSharedPreferences(AppDefaults.PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public void uncaughtException(Thread t, Throwable e) {

        Log.i("UncException", e.getStackTrace().toString());

        LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                + ":" + e.getStackTrace()[0].getLineNumber());

        //Write a printable representation of this Throwable
        //The StringWriter gives the lock used to synchronize access to this writer.
        final Writer stringBuffSync = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringBuffSync);
        e.printStackTrace(printWriter);
        final String stacktrace = stringBuffSync.toString();
        printWriter.close();

        //if (localPath != null) {
        String crashFileName = AppUtils.writeToFile(stacktrace, "UncaughtException");
        //}

        prefs.edit().putBoolean("isCrashed", true).apply();
        prefs.edit().putString("crashType", "UncaughtException").apply();
        prefs.edit().putString("crashFileName", crashFileName).apply();

        if(mActivity.get()!=null){
            mActivity.get().finish();
        }
        Process.killProcess(Process.myPid());
        System.exit(0);

        // Intent intent = new Intent(activity, FitWorkActivity.class);
        /*Intent intent = new Intent(mContext, myActivityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isCrashed", true);
        intent.putExtra("crashType", AppDefaults.CRASH_UNCAUGHT);
        //intent.putExtra("stackTrace", stacktrace);
        intent.putExtra("crashFileName", crashFileName);
        mContext.startActivity(intent);*/

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(2000);



                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();*/


        /*Intent crashIntent = new Intent(activity, CrashService.class);
        crashIntent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        crashIntent.putExtra("stackTrace", stacktrace);
        crashIntent.putExtra("crashType", AppDefaults.CRASH_ANR);*/
        //activity.startService(crashIntent);

        /*PendingIntent pendingIntent =
                PendingIntent.getActivity(FitConnectApplication.getInstance().getBaseContext(),
                        0, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager mgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, pendingIntent);
        System.exit(2);*/


        //saveLog(stacktrace);


        //Used only to prevent from any code getting executed.
        // Not needed in this example
        //defaultUEH.uncaughtException(t, e);
    }

    /*private void saveLog(final String stackTrace){

        // LOG for Display
        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    final long maxLogIndex =
                            realm.where(LogFitConnect.class).max("logIndex") == null?0
                                    :realm.where(LogFitConnect.class).max("logIndex").longValue();
                    LogFitConnect logFitConnect = realm.createObject(LogFitConnect.class, UUID.randomUUID().toString());
                    logFitConnect.setLogDate(new Date());
                    logFitConnect.setLogText(stackTrace);
                    logFitConnect.setLogIndex(maxLogIndex + 1);
                    logFitConnect.setLogType(0);    // :AUTO LOGGING

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if(mRealm!=null) {
                mRealm.closeDialog();
            }
        }

        // LOG for LocalDB
        LoggerService.insertLog(clsName, stackTrace, null);

    }*/

    /*private void writeToFile(String currentStacktrace) {

        try {

            //Gets the Android external storage directory & Create new folder Crash_Reports
            File dir = new File(Environment.getExternalStorageDirectory(),
                    AppDefaults.CRASH_REPORTS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            Date date = new Date();
            String filename = dateFormat.format(date) + ".STACKTRACE";

            // Write the file into the folder
            File reportFile = new File(dir, filename);
            FileWriter fileWriter = new FileWriter(reportFile);
            fileWriter.append(currentStacktrace);
            fileWriter.flush();
            fileWriter.closeDialog();
        } catch (Exception e) {
            Log.e("ExceptionHandler", e.getMessage());
        }
    }*/


}