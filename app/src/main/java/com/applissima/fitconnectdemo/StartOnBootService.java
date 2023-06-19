package com.applissima.fitconnectdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by ilkerkuscan on 05/03/17.
 */

public class StartOnBootService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("START ON BOOT", "Started after boot...");
        /*if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                || Intent.ACTION_INSTALL_PACKAGE.equals(intent.getAction())) {
            if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
                Log.i("START ON BOOT", "Type: ACTION_BOOT_COMPLETED.");
            } else {
                Log.i("START ON BOOT", "Type: " + intent.getAction());
            }
            Intent i = new Intent(context, FitWorkActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        }*/
    }
}