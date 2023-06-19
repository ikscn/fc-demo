package com.applissima.fitconnectdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class DisplayLogActivity extends AppCompatActivity {

    private final String clsName = "DisplayLogActivity";
    private StringBuilder log = new StringBuilder();
    public ListView logListView;
    public List<Map<String, String>> logList;
    public SimpleAdapter logListAdapter;
    public Button saveCurrentLogButton;
    public Button clearLogsButton;
    public TextView logTextView;
    public boolean listMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme_ActionBar_Transparent);
        setContentView(R.layout.activity_display_log);

        initUI();

        populateLogList();
        switchToListMode();

        super.onCreate(savedInstanceState);

    }

    private void initUI(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                    logTextView = (TextView) findViewById(R.id.logcatText);
                    logTextView.setMovementMethod(new ScrollingMovementMethod());


                    saveCurrentLogButton = (Button) findViewById(R.id.saveCurrentLogButton);
                    saveCurrentLogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveLog();
                        }
                    });

                    clearLogsButton = (Button) findViewById(R.id.clearLogsButton);
                    clearLogsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Realm bgRealm = null;
                            try {

                                bgRealm = Realm.getDefaultInstance();

                                bgRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.delete(LogFitConnect.class);
                                    }
                                });

                            } catch (Exception e){
                                e.printStackTrace();
                            } finally {
                                if(bgRealm!=null) {
                                    bgRealm.close();
                                }
                                populateLogList();
                                switchToListMode();
                            }
                        }
                    });

                    logListView = (ListView) findViewById(R.id.logListView);
                    logListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {

                            Realm bgRealm = null;

                            try {

                                bgRealm = Realm.getDefaultInstance();

                                bgRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        String text = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
                                        long logIndex = Long.valueOf(text.substring(4, text.indexOf(" [")));
                                        LogFitConnect logFitConnect = realm.where(LogFitConnect.class).equalTo("logIndex", logIndex).findFirst();
                                        logTextView.setText(logFitConnect.getLogText());
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if(bgRealm!=null) {
                                    bgRealm.close();
                                }
                                switchToDetailMode();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
                }
            }
        });


    }

    private void populateLogList(){

        logList = new ArrayList<Map<String, String>>();

        Realm bgRealm = null;

        try {

            bgRealm = Realm.getDefaultInstance();

            RealmResults<LogFitConnect> logResults = bgRealm.where(LogFitConnect.class).findAllSorted("logIndex", Sort.DESCENDING);
            for (LogFitConnect logInfo : logResults) {
                addToLogList(logInfo.getLogType(), logInfo.getLogIndex(), logInfo.getLogDate());
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logListAdapter = new SimpleAdapter(DisplayLogActivity.this, logList, android.R.layout.simple_list_item_2,
                            new String[]{"logInfo", "logDate"}, new int[]{android.R.id.text1, android.R.id.text2});

                    logListView.setAdapter(logListAdapter);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(bgRealm!=null) {
                bgRealm.close();
            }
        }

    }

    private void saveLog(){

        try {

            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                log.append(line.substring(line.indexOf(": ") + 2)).append("\n");

            }

            this.log.append(log.toString().replace(this.log.toString(), ""));
            logTextView.setText(this.log.toString());

            final String logText = this.log.toString();

            Realm bgRealm = null;

            try {

                bgRealm = Realm.getDefaultInstance();

                Number result = bgRealm.where(LogFitConnect.class).max("logIndex");
                final long maxLogIndex = result == null ? 0 : result.longValue();

                bgRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        LogFitConnect newLog = realm.createObject(LogFitConnect.class, UUID.randomUUID().toString());
                        newLog.setLogDate(new Date());
                        newLog.setLogIndex(maxLogIndex + 1);
                        newLog.setLogText(logText);
                        newLog.setLogType(1);   // :MANUAL LOGGING
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(bgRealm!=null) {
                    bgRealm.close();
                }
                populateLogList();
            }

        } catch (IOException e) {
            Log.e("wifiDirectHandler", "Failure reading logcat");
        }

    }

    private void addToLogList(int logType, long logIndex, Date logDate){
        SimpleDateFormat sdf = new SimpleDateFormat(AppDefaults.TIME_FORMAT_DEFAULT);
        Map<String, String> map = new HashMap<String, String>();
        String logTypeStr = logType==0?"[AUTO]":"[MANUAL]";
        map.put("logInfo", "Log " + String.valueOf(logIndex) + " " + logTypeStr);
        map.put("logDate", sdf.format(logDate));
        logList.add(map);
    }

    private void switchToListMode(){

        listMode = true;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logTextView.setVisibility(View.GONE);
                logListView.setVisibility(View.VISIBLE);
            }
        });

    }

    private void switchToDetailMode(){

        listMode = false;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logListView.setVisibility(View.GONE);
                logTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(listMode){
            super.onBackPressed();
        } else {
            populateLogList();
            switchToListMode();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                if(listMode){
                    return super.onOptionsItemSelected(item);
                }
                else {
                    populateLogList();
                    switchToListMode();
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
