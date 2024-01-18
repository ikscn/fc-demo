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
    private SharedPreferences prefs;
    private WeakReference<FitWorkActivity> mActivity;

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

        String crashFileName = AppUtils.writeToFile(stacktrace, "UncaughtException");

        prefs.edit().putBoolean("isCrashed", true).apply();
        prefs.edit().putString("crashType", "UncaughtException").apply();
        prefs.edit().putString("crashFileName", crashFileName).apply();

        if(mActivity.get()!=null){
            mActivity.get().finish();
        }
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

}
