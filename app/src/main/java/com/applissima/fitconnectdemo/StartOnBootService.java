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
    }
}
