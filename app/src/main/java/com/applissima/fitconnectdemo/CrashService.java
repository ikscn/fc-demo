package com.applissima.fitconnectdemo;

/*import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmConfiguration;

*//**
 * Created by ilkerkuscan on 15/09/17.
 *//*

public class CrashService extends IntentService {

    public CrashService() {
        super("CrashService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i("INTENT", "HANDLE INTENT!!");
        //Toast.makeText(getApplicationContext(),"CrashService started", Toast.LENGTH_LONG).show();

        String stackTrace = intent.getStringExtra("stackTrace");
        String crashType = intent.getStringExtra("crashType");

        //SystemClock.sleep(3000); // 3 seconds

        sendCrashMail(stackTrace, crashType);

    }

    private void sendCrashMail(String currentStacktrace, String crashType){

        try {

            List<String> toList = new ArrayList<String>();
            toList.add(AppDefaults.GMAIL_USER);

            RealmConfiguration config = new RealmConfiguration
                    .Builder()
                    .deleteRealmIfMigrationNeeded()
                    .name(AppDefaults.REALM_FILENAME)
                    //.directory(Environment.getExternalStorageDirectory())
                    .build();

            File currentRealmFile = new File(Realm.getInstance(config).getPath());

            RealmConfiguration logConfig = new RealmConfiguration
                    .Builder()
                    .deleteRealmIfMigrationNeeded()
                    .name(AppDefaults.REALMLOG_FILENAME)
                    //.directory(Environment.getExternalStorageDirectory())
                    .build();

            File currentRealmLogFile = new File(Realm.getInstance(logConfig).getPath());

            List<File> fileList = new ArrayList<File>();
            fileList.add(currentRealmFile);
            fileList.add(currentRealmLogFile);

            new SendMailTask().execute(AppDefaults.GMAIL_USER,
                    AppDefaults.GMAIL_PW,
                    toList,
                    crashType + " on Device " + Settings.locationId + "/" + Settings.siteId,
                    currentStacktrace,
                    fileList);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}*/
