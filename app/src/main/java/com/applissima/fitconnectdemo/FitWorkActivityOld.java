package com.applissima.fitconnectdemo;
/*

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class FitWorkActivityOld extends AppCompatActivity {

    private final String clsName = "FitWorkActivity";

    //private RealmResults<FitWorkPerson> workPersons;

    private DatabaseService dbService;
    private boolean mBoundDbService;
    //private UDPService udpService;
    //private boolean mBoundUdpService;
    private GridLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    //FwTest
    //private FitWorkPersonAdapter mAdapter;
    private List<FitWorkPersonCard> fwPersonCardList;
    private FwPersonAdapter mAdapter;

    private Button addPersonButton;
    private Button removePersonButton;
    private MenuItem wifiStatusIconFw;
    private MenuItem hubStatusIconFw;
    private MenuItem mi_showTestButtons;
    private MenuItem mi_hideTestButtons;

    private RelativeLayout progressLayoutFw;

    private FitWork fitWork;

    private Thread mainBgThread;
    private Thread uiThread;
    private Thread hourlyResetThread;
    private Thread memoryStateThread;

    private TextView timeTextView;

    // Charts
    private SummaryWorkPerson summaryWorkPerson;
    private CircularImageView personImageView;
    private TextView sumPersonNickname;
    private TextView sumDurationVal;
    private TextView sumTotalCalVal;
    private TextView sumCalMinVal;
    private TextView sumMaxPerfVal;
    private TextView sumAvgPerfVal;
    private TextView sumMaxHrVal;
    private TextView sumAvgHrVal;
    private HorizontalBarChart chartCalZone;
    private LineChart chartHrMin;
    private BarData barData;
    private LineData lineData;

    // ChartTest
    private FrameLayout chartFrame;

    private int networkCheckCount = 0;

    private boolean scheduledTaskInProcess = false;

    private RequestQueue queue;

    private String versionId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("PROCESS ID", "PROCESS ID: " + String.valueOf(android.os.Process.myPid()));

    }

    @Override
    protected void onResume() {
        Log.i("ACT RESUMED", "ACTIVITY RESUMED.");
        super.onResume();

        //Fabric.with(this, new Crashlytics());

        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler("/mnt/sdcard/"));

        setContentView(R.layout.activity_fit_work);

        initActionBar();

        // Init Realm
        Realm.init(FitWorkActivityOld.this);

        */
/*Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this)
                .databaseNamePattern(Pattern.compile("fitConnect.realm")).build())
                .build());*//*


        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name("fitConnect.realm")
                .build();

        Realm.compactRealm(config);

        Realm.setDefaultConfiguration(config);

        // TODO: 04/03/17 title, Fitwork ile güncellenecek
        // setTitle("Kardiyo");

        initAppUtils();

        // init Services
        initServices();

        // Manage App Settings
        manageAppSettings();

        //initWorkPersonListener();

        initViews();

        //initFirebase();

        (new RealmClearTask()).execute();

        // Start timers after wifi is connected
        */
/*setTimerForClockText();
        setTimerForHour();
        setTimerForMainBgThread();
        setTimerForUpdateUI();
        setTimerForMemoryState();*//*


        new Thread(new Runnable() {
            @Override
            public void run() {

                while (!(AppUtils.isWifiConnected() && AppUtils.isInternetOn())){
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(FitWorkActivityOld.this, "Wifi bağlantısı bekleniyor...", Toast.LENGTH_LONG);

                            }
                        });
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                CheckNetworksTask networksTask = new CheckNetworksTask();
                networksTask.execute();

                // Start timers after wifi is connected
                setTimerForClockText();
                setTimerForHour();
                setTimerForMainBgThread();
                setTimerForUpdateUI();
                setTimerForMemoryState();

            }
        }).start();


    }

    */
/*private void initWorkPersonListener(){

        Realm mRealm = Realm.getDefaultInstance();


        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    workPersons = realm.where(FitWorkPerson.class)
                            .isNotNull("fitPerson")
                            //.greaterThan("hrLastDataUpdateDate", timeOut.getTime())
                            .findAll();

                }
            });


            workPersons.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<FitWorkPerson>>() {
                @Override
                public void onChange(RealmResults<FitWorkPerson> collection, OrderedCollectionChangeSet changeSet) {
                    if(changeSet.getInsertions().length>0
                            || changeSet.getDeletions().length>0){

                        //updateUI();

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } *//*
*/
/*finally {
            mRealm.closeDialog();
        }*//*
*/
/*

    }*//*


    */
/*private void initFirebase(){

        DatabaseReference versionRef = FirebaseDatabase.getInstance().getReference("appVersion");
        versionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                versionId = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }*//*


    private void initAppUtils(){

        AppUtils.setStaticContext(FitWorkActivityOld.this);

    }

    private void initViews(){

        //refreshGridSize();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Realm mRealm = Realm.getDefaultInstance();

                try {

                    Calendar timeOut = Calendar.getInstance();
                    timeOut.add(Calendar.SECOND, -60);

                    // View initials
                    mRecyclerView = (RecyclerView) findViewById(R.id.fitWorkRecyclerView);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new GridLayoutManager(FitWorkActivityOld.this, 2);
                    mRecyclerView.setLayoutManager(mLayoutManager);


                    // FwTest
                    */
/*mAdapter = new FitWorkPersonAdapter(FitWorkActivity.this,
                            mRealm.where(FitWorkPerson.class)
                                    .isNotNull("fitPerson")
                                    //.greaterThan("hrLastDataUpdateDate", timeOut.getTime())
                                    .findAll());
                    mRecyclerView.setAdapter(mAdapter);*//*

                    fwPersonCardList = new ArrayList<FitWorkPersonCard>();
                    mAdapter = new FwPersonAdapter(FitWorkActivityOld.this, fwPersonCardList);
                    mRecyclerView.setAdapter(mAdapter);


                    addPersonButton = (Button) findViewById(R.id.addPerson);
                    removePersonButton = (Button) findViewById(R.id.removePerson);

                    timeTextView = (TextView) findViewById(R.id.timeTextView);

                    // ChartTest
                    chartFrame = (FrameLayout) findViewById(R.id.chart_frame);
                    chartFrame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            view.setVisibility(View.GONE);
                        }
                    });

                    // Charts
                    // UI Elements
                    personImageView = (CircularImageView) findViewById(R.id.ch_personImage);
                    sumPersonNickname = (TextView) findViewById(R.id.ch_nicknameTextView);
                    sumDurationVal = (TextView) findViewById(R.id.ch_sum_duration_value);
                    sumTotalCalVal = (TextView) findViewById(R.id.ch_sum_totCal_value);
                    sumCalMinVal = (TextView) findViewById(R.id.ch_sum_calMin_value);
                    sumMaxPerfVal = (TextView) findViewById(R.id.ch_sum_maxPerf_value);
                    sumAvgPerfVal = (TextView) findViewById(R.id.ch_sum_avgPerf_value);
                    sumMaxHrVal = (TextView) findViewById(R.id.ch_sum_maxHr_value);
                    sumAvgHrVal = (TextView) findViewById(R.id.ch_sum_avgHr_value);

                    chartCalZone = (HorizontalBarChart) findViewById(R.id.ch_graph_calZone);

                    chartCalZone.setDrawGridBackground(false);
                    chartCalZone.getLegend().setEnabled(false);
                    chartCalZone.getDescription().setEnabled(false);
                    chartCalZone.setFitBars(true);
                    chartCalZone.setHardwareAccelerationEnabled(true);
                    final String[] zoneNames = new String[] { "Zone 1", "Zone 2", "Zone 3", "Zone 4", "Zone 5" };
                    IAxisValueFormatter formatter = new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return zoneNames[(int) value];
                        }
                    };
                    XAxis xAxis = chartCalZone.getXAxis();
                    xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                    xAxis.setValueFormatter(formatter);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                    xAxis.setXOffset(48.0f);
                    xAxis.setDrawGridLines(false);
                    xAxis.setDrawAxisLine(false);
                    xAxis.setTextColor(ContextCompat.getColor(FitWorkActivityOld.this, R.color.userWhite));

                    YAxis yAxisLeft = chartCalZone.getAxisLeft();
                    yAxisLeft.setDrawZeroLine(false);
                    yAxisLeft.setDrawAxisLine(false);
                    yAxisLeft.setDrawGridLines(false);
                    yAxisLeft.setDrawLabels(false);

                    YAxis yAxisRight = chartCalZone.getAxisRight();
                    yAxisRight.setDrawZeroLine(false);
                    yAxisRight.setDrawAxisLine(false);
                    yAxisRight.setDrawGridLines(false);
                    yAxisRight.setDrawLabels(false);


                    // Setup ChartHrMin

                    chartHrMin = (LineChart) findViewById(R.id.ch_graph_hrMin);
                    chartHrMin.setDrawGridBackground(false);
                    chartHrMin.getDescription().setEnabled(false);
                    chartHrMin.getLegend().setEnabled(false);
                    chartHrMin.setHardwareAccelerationEnabled(true);

                    XAxis xAxis2 = chartHrMin.getXAxis();
                    xAxis2.setGranularityEnabled(true);
                    xAxis2.setGranularity(1f);
                    xAxis2.setAxisMinimum(0f);
                    xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis2.setDrawGridLines(false);
                    xAxis2.setDrawAxisLine(false);
                    xAxis2.setTextColor(ContextCompat.getColor(FitWorkActivityOld.this, R.color.userWhite));

                    YAxis yAxisLeft2 = chartHrMin.getAxisLeft();
                    yAxisLeft2.setGranularityEnabled(true);
                    yAxisLeft2.setGranularity(1f);
                    yAxisLeft2.setDrawZeroLine(false);
                    yAxisLeft2.setDrawAxisLine(false);
                    yAxisLeft2.setDrawGridLines(false);
                    yAxisLeft2.setTextColor(ContextCompat.getColor(FitWorkActivityOld.this, R.color.userWhite));

                    YAxis yAxisRight2 = chartHrMin.getAxisRight();
                    yAxisRight2.setEnabled(false);

                } catch (Exception e) {
                    if(mRealm!=null) {
                        mRealm.closeDialog();
                    }
                    e.printStackTrace();
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                }

            }
        });

    }

    private void initServices(){


        // Bind DbService
        Intent dbIntent = new Intent(this, DatabaseService.class);
        startService(dbIntent);
        bindService(dbIntent, mDbConnection, Context.BIND_AUTO_CREATE);

        // Start UDPService
        Intent udpIntent = new Intent(getBaseContext(), UDPService.class);
        startService(udpIntent);
        */
/*startService(new Intent(getBaseContext(), DatabaseService.class));
        startService(new Intent(getBaseContext(), UDPService.class));*//*


        // ServiceTest
        //DatabaseService.init(FitWorkActivity.this);
        //UDPService.init();

        queue = Volley.newRequestQueue(FitWorkActivityOld.this);
        //dbService = new DatabaseService(this);
        //udpService = new UDPService();

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

    */
/*private ServiceConnection mUdpConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            UDPService.UDPServiceBinder binder = (UDPService.UDPServiceBinder) service;
            udpService = binder.getService();
            mBoundUdpService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBoundUdpService = false;
        }
    };*//*


    */
/*private void generateDummyFitWorkPersons() {

        Realm bgRealm = null;

        try {

            bgRealm = Realm.getDefaultInstance();

            bgRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    if (Settings.simulationPersonCount >= 1) {

                        //1
                        FitWorkPerson fitWorkPerson1 = realm.createObject(FitWorkPerson.class);
                        fitWorkPerson1.setFitPerson(realm.where(FitPerson.class).equalTo("userName", "nikola").findFirst());
                        fitWorkPerson1.setAddedDate(new Date());
                        fitWorkPerson1.setCurrentHr(generateRandomRawData().getHeartRate());
                        fitWorkPerson1.setCurrentPerf(calcPerf(fitWorkPerson1.getCurrentHr(), fitWorkPerson1.getFitPerson()));
                        fitWorkPerson1.setTotalCal(100);
                        fitWorkPerson1.setCurrentZone(calcZone(fitWorkPerson1.getCurrentHr(), fitWorkPerson1.getFitPerson()));
                        fitWorkPerson1.setActHrSensorId(fitWorkPerson1.getFitPerson().getHrSensorId());
                        fitWorkPerson1.setHrLastDataUpdateDate(Calendar.getInstance().getTime());

                        if (Settings.simulationPersonCount >= 2) {

                            //2
                            FitWorkPerson fitWorkPerson2 = realm.createObject(FitWorkPerson.class);
                            fitWorkPerson2.setFitPerson(realm.where(FitPerson.class).equalTo("userName", "ilker").findFirst());
                            fitWorkPerson2.setAddedDate(new Date());
                            fitWorkPerson2.setCurrentHr(generateRandomRawData().getHeartRate());
                            fitWorkPerson2.setCurrentPerf(calcPerf(fitWorkPerson2.getCurrentHr(), fitWorkPerson2.getFitPerson()));
                            fitWorkPerson2.setTotalCal(200);
                            fitWorkPerson2.setCurrentZone(calcZone(fitWorkPerson2.getCurrentHr(), fitWorkPerson2.getFitPerson()));
                            fitWorkPerson2.setActHrSensorId(fitWorkPerson2.getFitPerson().getHrSensorId());
                            fitWorkPerson2.setHrLastDataUpdateDate(Calendar.getInstance().getTime());

                            if (Settings.simulationPersonCount >= 3) {

                                //3
                                FitWorkPerson fitWorkPerson3 = realm.createObject(FitWorkPerson.class);
                                fitWorkPerson3.setFitPerson(realm.where(FitPerson.class).equalTo("userName", "dila1").findFirst());
                                fitWorkPerson3.setAddedDate(new Date());
                                fitWorkPerson3.setCurrentHr(generateRandomRawData().getHeartRate());
                                fitWorkPerson3.setCurrentPerf(calcPerf(fitWorkPerson3.getCurrentHr(), fitWorkPerson3.getFitPerson()));
                                fitWorkPerson3.setTotalCal(255);
                                fitWorkPerson3.setCurrentZone(calcZone(fitWorkPerson3.getCurrentHr(), fitWorkPerson3.getFitPerson()));
                                fitWorkPerson3.setActHrSensorId(fitWorkPerson3.getFitPerson().getHrSensorId());
                                fitWorkPerson3.setHrLastDataUpdateDate(Calendar.getInstance().getTime());

                                if (Settings.simulationPersonCount >= 4) {

                                    //4
                                    FitWorkPerson fitWorkPerson4 = realm.createObject(FitWorkPerson.class);
                                    fitWorkPerson4.setFitPerson(realm.where(FitPerson.class).equalTo("userName", "mert").findFirst());
                                    fitWorkPerson4.setAddedDate(new Date());
                                    fitWorkPerson4.setCurrentHr(generateRandomRawData().getHeartRate());
                                    fitWorkPerson4.setCurrentPerf(calcPerf(fitWorkPerson4.getCurrentHr(), fitWorkPerson4.getFitPerson()));
                                    fitWorkPerson4.setTotalCal(230);
                                    fitWorkPerson4.setCurrentZone(calcZone(fitWorkPerson4.getCurrentHr(), fitWorkPerson4.getFitPerson()));
                                    fitWorkPerson4.setActHrSensorId(fitWorkPerson4.getFitPerson().getHrSensorId());
                                    fitWorkPerson4.setHrLastDataUpdateDate(Calendar.getInstance().getTime());

                                }

                            }

                    *//*
*/
/*FitWorkPerson testAPIWorkPerson = realm.createObject(FitWorkPerson.class);
                    testAPIWorkPerson.setFitPerson(realm.where(FitPerson.class).equalTo("hrSensorId", "00024").findFirst()); // Özgür Test
                    if (testAPIWorkPerson.getFitPerson() != null) {
                        testAPIWorkPerson.setAddedDate(new Date());
                        testAPIWorkPerson.setCurrentHr(generateRandomRawData().getHeartRate());
                        testAPIWorkPerson.setCurrentPerf(calcPerf(testAPIWorkPerson.getCurrentHr(), testAPIWorkPerson.getFitPerson()));
                        testAPIWorkPerson.setTotalCal(210);
                        testAPIWorkPerson.setCurrentZone(calcZone(testAPIWorkPerson.getCurrentHr(), testAPIWorkPerson.getFitPerson()));
                        testAPIWorkPerson.setActHrSensorId(testAPIWorkPerson.getFitPerson().getHrSensorId());
                        testAPIWorkPerson.setHrLastDataUpdateDate(Calendar.getInstance().getTime());
                    }*//*
*/
/*

                        }

                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getLocalizedMessage());
        } finally {
            if(bgRealm!=null) {
                bgRealm.closeDialog();
                Realm.compactRealm(bgRealm.getConfiguration());
            }
        }


    }*//*




    // Adds a new random simulation person
    public void addPerson(View view){

        Settings.simulationPersonCount++;

        */
/*if(mLayoutManager!=null && mRecyclerView!=null) {
            updateUI();
        }*//*


    }

    // removes last added simulation person
    public void removePerson(View view){

        if(Settings.simulationPersonCount>0) {
            Settings.simulationPersonCount--;
        }

        */
/*if(mLayoutManager!=null && mRecyclerView!=null) {
            updateUI();
        }*//*


    }

    public int getGridSize(){

        int listSize = fwPersonCardList.size();

        if (listSize <= 4) {
            return 2;
        } else if (listSize <= 9) {
            return 3;
        } else if (listSize <= 16) {
            return 4;
        } else {
            return 5;
        }

    }

    public void refreshGridSize(){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            Calendar timeOut = Calendar.getInstance();
            timeOut.add(Calendar.SECOND, -60);

            long workPersonCount = mRealm.where(FitWorkPerson.class)
                    .isNotNull("fitPerson")
                    //.greaterThan("hrLastDataUpdateDate", timeOut.getTime())
                    .count();

            if (workPersonCount <= 4) {
                StaticVariables.gridSize = 2;
            } else if (workPersonCount <= 9) {
                StaticVariables.gridSize = 3;
            } else if (workPersonCount <= 16) {
                StaticVariables.gridSize = 4;
            } else {
                StaticVariables.gridSize = 5;
            }


        } catch (Exception e) {
            mRealm.closeDialog();
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        }
    }

    public void updateUI(){

        //DatabaseService.deleteExpiredData();

        //refreshGridSize();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Realm mRealm = Realm.getDefaultInstance();

                try {

                    //mLayoutManager.setSpanCount(StaticVariables.gridSize);

                    //FwTest
                    */
/*mAdapter = new FitWorkPersonAdapter(FitWorkActivity.this,
                            mRealm.where(FitWorkPerson.class)
                                    .isNotNull("fitPerson")
                                    //.greaterThan("hrLastDataUpdateDate", timeOut.getTime())
                                    .findAll());*//*


                    fwPersonCardList = dbService.getCurrentCardList();
                    mAdapter = new FwPersonAdapter(FitWorkActivityOld.this, fwPersonCardList);
                    mLayoutManager.setSpanCount(getGridSize());

                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                } finally {
                    mRealm.closeDialog();
                }

            }
        });

    }

    public void setTimerForMainBgThread(){

        mainBgThread = new Thread() {

            @Override
            public void run() {
                try {

                    // Initial wait
                    //Thread.sleep(ThreadController.tInitialDelay * 1000);

                    int timer = 0;

                    while (!isInterrupted()) {
                        Thread.sleep(1000);

                        if(timer > 0 && timer%ThreadController.tCheckNetworks==0){
                            CheckNetworksTask networksTask = new CheckNetworksTask();
                            networksTask.execute();
                        }
                        if(timer > 0 && timer%ThreadController.tCheckProfileUpdates()==0){
                            dbService.updatePersonInfo();
                        }
                        if(timer > 0 && timer%ThreadController.tUploadLogs==0){

                            if(AppUtils.isWifiConnected() && AppUtils.isInternetOn()) {
                                UploadLogsTask uploadLogsTask = new UploadLogsTask();
                                uploadLogsTask.execute();
                            }
                        }
                        if(timer > 0 && timer%ThreadController.tLogStatus==0){
                            LoggerService.insertLogStatusMessage();
                        }
                        if(timer > 0 && timer%ThreadController.tMinAggUpload==0){

                            if(AppUtils.isWifiConnected() && AppUtils.isInternetOn()) {
                                //DatabaseService.uploadMinDataAgg();
                                UploadDataMinAggTask uploadMinAggTask = new UploadDataMinAggTask();
                                uploadMinAggTask.execute();
                            } else {
                                LoggerService.insertLog(clsName, "ERROR: DB-SendDataToServer: Not connected to internet", null);
                            }
                        }
                        */
/*if(timer > 0 && timer%ThreadController.tDeleteExpiredMinAgg==0){
                            DatabaseService.deleteExpiredData();
                        }*//*


                        if(timer > 0 && timer%ThreadController.tWorkSummary()==0){

                            final String personToDisplayBeltId = dbService.nextPersonToDisplay();

                            if(!"".equals(personToDisplayBeltId)) {

                                showWorkSummary(personToDisplayBeltId);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            Thread.sleep(Settings.workSumDuration * 1000);
                                            hideWorkSummary();

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        }
                        */
/*if(timer > (5 * Settings.simulationPersonCount)
                                && timer%ThreadController.tCheckBelts==0){

                            DatabaseService.checkBelts();

                        }*//*

                        if(timer > 0 && timer%600==0){
                            dbService.compactRealms();
                        }

                        timer++;
                        if(timer==2000000){
                            timer = 0;
                        }

                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mainBgThread.start();

    }


    public void setTimerForUpdateUI(){

        uiThread = new Thread() {

            @Override
            public void run() {
                try {

                    // Initial wait
                    Thread.sleep(3 * 1000);

                    // Update UI every second
                    while (!isInterrupted()) {
                        Thread.sleep(1000);

                        updateUI();

                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        uiThread.start();

    }




    public void setTimerForHour(){

        hourlyResetThread = new Thread() {

            @Override
            public void run() {
                try {

                    while (!isInterrupted()) {
                        Thread.sleep(10 * 1000);
                        //Thread.sleep(1 * 60 * 1000);
                        //resetActivity();
                        //interruptThreads();

                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        hourlyResetThread.start();

    }

    private void setTimerForMemoryState(){


        memoryStateThread = new Thread() {

            @Override
            public void run() {
                try {

                    while (!isInterrupted()) {
                        Thread.sleep(20 * 60 * 1000);
                        //Thread.sleep(1 * 60 * 1000);
                        logMemoryState();

                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        memoryStateThread.start();

    }

    private void setTimerForClockText(){

        int initialDelay = 5000; //first update in miliseconds
        int period = 5000;      //nexts updates in miliseconds

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Message msg = new Message();
                mHandler.sendMessage(msg);
            }
        };
        timer.scheduleAtFixedRate(task, initialDelay, period);

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String dateStr = AppUtils.getDateString(Calendar.getInstance().getTime(), AppDefaults.TIME_FORMAT_CLOCK);

            if(timeTextView!=null) {
                timeTextView.setText(dateStr);
            } else {
                return;
            }

            // End of day timer
            try {

                if(AppDefaults.STR_END_OF_DAY.equalsIgnoreCase(dateStr)
                        && !scheduledTaskInProcess){

                    scheduledTaskInProcess = true;

                    dbService.backupRealm();
                    dbService.backupLogRealm();

                    GmailSender gmailSender = new GmailSender();
                    gmailSender.sendMailEndOfDay();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Wait for 2 minutes to by-pass the current minute
                                Thread.sleep(120 * 1000);
                                scheduledTaskInProcess = false;

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }

                // Midnight timer
                if((AppDefaults.STR_MIDNIGHT.equalsIgnoreCase(dateStr)
                        || AppDefaults.STR_MORNING.equalsIgnoreCase(dateStr))
                        && !scheduledTaskInProcess){

                    scheduledTaskInProcess = true;

                    dbService.deleteExpiredData();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Wait for 2 minutes to by-pass the current minute
                                Thread.sleep(120 * 1000);
                                scheduledTaskInProcess = false;

                                try {

                                    StaticVariables.isReboot = true;

                                    onStop();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }

            } catch (Exception e) {
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                e.printStackTrace();
            }

        }
    };


    */
/*public void setTimerForUpdateUI(){

        updateUIThread = new Thread() {

            @Override
            public void run() {
                try {

                    while (!isInterrupted()) {
                        Thread.sleep(2000);

                        // Get new data from belt or generate new data for simulations
                        DatabaseService.generateData();
                        updateUI();

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        updateUIThread.start();

    }


    private void setTimerForCheckNetworks(){

        checkNetworksThread = new Thread() {

            @Override
            public void run() {
                try {

                    // Check network status every 15 seconds
                    while (!isInterrupted()) {
                        Thread.sleep(15 * 1000);

                        CheckNetworksTask networksTask = new CheckNetworksTask();
                        networksTask.execute();

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        checkNetworksThread.start();

    }



    private void setTimerForCheckBelts(){

        checkBeltsThread = new Thread() {

            @Override
            public void run() {
                try {

                    Thread.sleep(5 * Settings.simulationPersonCount * 1000);

                    // Check new belts every 3 seconds
                    while (!isInterrupted()) {
                        Thread.sleep(3000);

                        DatabaseService.checkBelts();

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        checkBeltsThread.start();

    }

    private void setTimerForMinAggUpload(){

        uploadAggMinDataThread = new Thread() {

            @Override
            public void run() {
                try {

                    // Initial delay for 15 seconds
                    Thread.sleep(15 * 1000);

                    // Upload every 2 minutes
                    while (!isInterrupted()) {
                        Thread.sleep(2 * 60 * 1000);
                        //Thread.sleep(30 * 1000);

                        if(AppUtils.isWifiConnected()) {
                            DatabaseService.uploadMinDataAgg();
                        } else {
                            LoggerService.insertLog(clsName, "ERROR: DB-SendDataToServer: Not connected to internet", null);
                        }

                        *//*
*/
/*UploadDataMinAggTask uploadTask = new UploadDataMinAggTask();
                        uploadTask.execute();*//*
*/
/*

                        Thread.sleep(5000);

                        DatabaseService.deleteExpiredData();

                        *//*
*/
/*DeleteExpiredDataMinAggTask delExpTask = new DeleteExpiredDataMinAggTask();
                        delExpTask.execute();*//*
*/
/*

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        uploadAggMinDataThread.start();

    }

    private void setTimerForProfileUpdates(){

        checkProfileUpdThread = new Thread() {

            @Override
            public void run() {
                try {

                    // Upload every x minutes (arrangeable by Settings)
                    while (!isInterrupted()) {
                        Thread.sleep(Settings.checkProfileUpdates * 60 * 1000);

                        DatabaseService.updatePersonInfo();

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        checkProfileUpdThread.start();

    }

    private void setTimerForLogStatus(){

        logStatusThread = new Thread() {

            @Override
            public void run() {
                try {

                    // Log status every 90 seconds
                    while (!isInterrupted()) {
                        Thread.sleep(90 * 1000);

                        LoggerService.insertLogStatusMessage();

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        logStatusThread.start();

    }

    private void setTimerForUploadLogs(){

        logUploadThread = new Thread() {

            @Override
            public void run() {
                try {

                    // Upload logs on batch from local logDb, every 30 minutes
                    while (!isInterrupted()) {
                        // TODO: 07/03/17 change period
                        //Thread.sleep(30 * 60 * 1000);
                        Thread.sleep(110 * 1000);

                        UploadLogsTask uploadLogsTask = new UploadLogsTask();
                        uploadLogsTask.execute();

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        logUploadThread.start();

    }


    private void setTimerForWorkSummary(){

        workSummaryThread = new Thread() {

            @Override
            public void run() {
                try {
                    // Initial wait
                    Thread.sleep(20 * 1000);

                    // Show work summary every x minutes
                    while (!isInterrupted()) {
                        Thread.sleep(Settings.workSumPeriod * 60 * 1000);

                        String personToDisplayBeltId = DatabaseService.nextPersonToDisplay();

                        if(!"".equals(personToDisplayBeltId)) {

                            showWorkSummary(personToDisplayBeltId);

                            Thread.sleep(Settings.workSumDuration * 1000);

                            hideWorkSummary();
                        }


                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        workSummaryThread.start();

    }*//*


    */
/*private String nextPersonToDisplay(){

        Realm bgRealm = null;
        final String[] nextPersonBeltId = new String[]{""};
        final boolean[] nextPersonSelected = new boolean[]{false};

        try {

            bgRealm = Realm.getDefaultInstance();

            bgRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Calendar timeOut = Calendar.getInstance();
                    timeOut.add(Calendar.SECOND, -60);

                    RealmResults<FitWorkPerson> workPersons = realm.where(FitWorkPerson.class)
                            .isNotNull("fitPerson")
                            .greaterThan("hrLastDataUpdateDate", timeOut.getTime())
                            .findAll();


                    // Persons with minimum 2 minAgg data rows
                    int sizeOfDisplayables = 0;

                    // Pick a person who is not displayed recently
                    for (FitWorkPerson fitWorkPerson : workPersons) {

                        String sensorId = fitWorkPerson.getActHrSensorId();

                        if (!"".equals(sensorId) && realm.where(FitWorkDataMinAgg.class)
                                .equalTo("fitPerson.hrSensorId", sensorId)
                                .count() >= 2) {

                            sizeOfDisplayables++;
                            if (!summaryDisplayedList.contains(sensorId) && !nextPersonSelected[0]) {
                                nextPersonBeltId[0] = fitWorkPerson.getActHrSensorId();
                                summaryDisplayedList.add(nextPersonBeltId[0]);
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
            LoggerService.insertLog(clsName, e.getMessage(), e.getLocalizedMessage());
        } finally {
            if(bgRealm!=null) {
                bgRealm.closeDialog();
                Realm.compactRealm(bgRealm.getConfiguration());
            }
        }


        return nextPersonBeltId[0];

    }*//*


    public class UploadDataMinAggTask extends AsyncTask<Void, Void, Void> {

        // Uploads aggregated data to the server


        @Override
        protected Void doInBackground(Void... voids) {

            String jsonString = dbService.getMinAggDataToUpload();
            if(jsonString==null || "".equalsIgnoreCase(jsonString)){
                return null;
            }

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

                            dbService.saveMinAggUploads();

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

            request.setShouldCache(false);

            queue.add(request);

            return null;
        }
    }

    public class UploadLogsTask extends AsyncTask<Void, Void, Void> {

        // Uploads aggregated data to the server


        @Override
        protected Void doInBackground(Void... voids) {

            String jsonString = dbService.getLogDataToUpload();
            if(jsonString==null || "".equalsIgnoreCase(jsonString)){
                return null;
            }

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

                            dbService.saveLogUpdates();

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

            request.setShouldCache(false);

            queue.add(request);

            return null;
        }
    }

    */
/*public class UpdatePersonInfoTask extends AsyncTask<Void, Void, Void> {

        // Updates person info


        @Override
        protected void onPreExecute() {
            personsToUpdate = new ArrayList<String>();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            personsToUpdate = null;
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            dbService.updatePersonInfo();

            *//*
*/
/*Realm bgRealm = null;

            try {

                bgRealm = Realm.getDefaultInstance();

                final Calendar timeOut = Calendar.getInstance();
                timeOut.add(Calendar.SECOND, -60 * 2);

                bgRealm.executeTransaction(new Realm.Transaction() {
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
                                dbService.updateOrAddLocalUserData(hrSensorId, false, udpService);
                            }

                        } else {
                            Log.i("PersonsToUpdate", "PersonsToUpdate: List is null.");
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getLocalizedMessage());
            } finally {
                if(bgRealm!=null) {
                    bgRealm.closeDialog();
                    Realm.compactRealm(bgRealm.getConfiguration());
                }
            }*//*
*/
/*

            return null;
        }
    }*//*


    */
/*public class CheckNetworksTask extends AsyncTask<Void, Void, Void> {

        // Uploads aggregated data to the server

        @Override
        protected Void doInBackground(Void... voids) {

            if(AppUtils.isWifiConnected()) {
                apiService.uploadMinDataAgg();
            } else {
                LoggerService.insertLog(clsName, "ERROR: DB-SendDataToServer: Not connected to internet", null);
            }

            return null;
        }
    }*//*



    public class DeleteExpiredDataMinAggTask extends AsyncTask<Void, Void, Void> {

        // Deletes yesterday's uploaded minAgg data

        @Override
        protected Void doInBackground(Void... voids) {

            Realm bgRealm = null;

            try {

                bgRealm = Realm.getDefaultInstance();

                final Calendar midNight = Calendar.getInstance();
                midNight.set(Calendar.HOUR_OF_DAY, 0);

                bgRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(FitWorkDataMinAgg.class)
                                .lessThan("insertDate", midNight.getTime())
                                .equalTo("uploadSuccessful", true)
                                .findAll()
                                .deleteAllFromRealm();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
            } finally {
                if(bgRealm!=null) {
                    bgRealm.closeDialog();
                }
            }

            return null;
        }
    }

    private class CheckNetworksTask extends AsyncTask<Void, Void, Void>{

        boolean isWifiOn = false;
        boolean isInternetOn = false;
        boolean isHubOn = false;
        boolean saveLog = false;

        @Override
        protected void onPreExecute() {

            networkCheckCount ++;
            if(networkCheckCount == 60){
                saveLog = true;
                networkCheckCount = 0;
            } else {
                saveLog = false;
            }

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                if(AppUtils.isWifiConnected()){
                    Log.i("Wifi Check", "Wifi is connected!!!");
                    if(saveLog){
                        LoggerService.insertLog(clsName, "WiFi is connected.", null);
                    }
                    isWifiOn = true;



                        if(AppUtils.isInternetOn()){
                            if(saveLog){
                                LoggerService.insertLog(clsName, "Internet is available.", null);
                            }
                            Log.i("Internet Check", "Internet is available!!!");
                            isInternetOn = true;
                        } else {
                            if(saveLog){
                                LoggerService.insertLog(clsName, "Internet is not available.", null);
                            }
                            Log.i("Internet Check", "Internet is not available!!!");
                            isInternetOn = false;
                        }


                } else {
                    if(saveLog){
                        LoggerService.insertLog(clsName, "Wifi is not connected.", null);
                    }
                    Log.i("Wifi Check", "Wifi is not connected.");
                    isWifiOn = false;
                }

                // Checking Hub Connection
                if(AppUtils.isHubEnabled()){
                    if(saveLog){
                        LoggerService.insertLog(clsName, "Hub is reachable with macAddress: " + AppUtils.hubMacAddress, null);
                    }
                    Log.i("Hub Check", "Hub is reachable with macAddress: " + AppUtils.hubMacAddress);
                    isHubOn = true;
                } else {
                    if(saveLog){
                        LoggerService.insertLog(clsName, "Hub is not reachable.", null);
                    }
                    Log.i("Hub Check", "Hub is not reachable.");
                    isHubOn = false;
                }

            } catch (Exception e){
                e.printStackTrace();
                LoggerService.insertLog(clsName, "Error while network connection checking!", e.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(isWifiOn) {
                        if(isInternetOn){
                            wifiStatusIconFw.setIcon(R.drawable.internet_on);
                        } else {
                            wifiStatusIconFw.setIcon(R.drawable.wifi_on);
                        }

                    } else {
                        wifiStatusIconFw.setIcon(R.drawable.wifi_off);
                    }
                    if(isHubOn){
                        hubStatusIconFw.setIcon(R.drawable.hub_on);
                    } else {
                        hubStatusIconFw.setIcon(R.drawable.hub_off);
                    }
                }
            });

            super.onPostExecute(aVoid);
        }
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStop() {

        super.onStop();

        Log.i("ACT STOPPED", "ACTIVITY STOPPED.");
        LoggerService.insertLog(clsName, "ACTIVITY STOPPED.", null);

        if(mainBgThread!=null) {
            mainBgThread.interrupt();
        }

        unbindService(mDbConnection);
        stopService(new Intent(getBaseContext(), DatabaseService.class));
        stopService(new Intent(getBaseContext(), UDPService.class));

        //workPersons.removeAllChangeListeners();

        //interruptThreads();
        //udpService = null;
        //dbService = null;

        if(StaticVariables.isReboot){
            try {
                Runtime.getRuntime().exec(new String[]{"/system/xbin/su","-c","reboot now"});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("ACT PAUSED", "ACTIVITY PAUSED.");
        LoggerService.insertLog(clsName, "ACTIVITY PAUSED.", null);
        //unregisterReceiver(broadcastReceiver);
    }

    private void initActionBar(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        actionBar.setCustomView(R.layout.actionbar_img_layout);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fitwork, menu);

        mi_showTestButtons = menu.findItem(R.id.showTestButtonsFw);
        mi_hideTestButtons = menu.findItem(R.id.hideTestButtonsFw);

        wifiStatusIconFw = menu.findItem(R.id.wifiStatusIconFw);
        hubStatusIconFw = menu.findItem(R.id.hubStatusIconFw);

        if(AppUtils.isWifiConnected()){
            Log.i("Wifi Check", "Wifi is connected!!!");

            try {

                if(AppUtils.isInternetOn()){
                    wifiStatusIconFw.setIcon(R.drawable.internet_on);
                    Log.i("Internet Check", "Internet is available!!!");
                } else {
                    wifiStatusIconFw.setIcon(R.drawable.wifi_on);
                    Log.i("Internet Check", "Internet is not available!!!");
                }

            } catch (Exception e){
                wifiStatusIconFw.setIcon(R.drawable.wifi_on);
                e.printStackTrace();
            }


        } else {
            wifiStatusIconFw.setIcon(R.drawable.wifi_off);
            Log.i("Wifi Check", "Wifi is not connected.");
        }

        if(AppUtils.isHubEnabled()){
            Log.i("Hub Check", "Hub is connected!!!");
            hubStatusIconFw.setIcon(R.drawable.hub_on);

        } else {
            Log.i("Wifi Check", "Wifi is not connected.");
            hubStatusIconFw.setIcon(R.drawable.hub_off);
        }

        return true;
    }

    */
/*private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION))
            {
                if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false))
                {
                    // wifi is enabled
                    //wifiStatusIconFw.setIcon(R.drawable.wifi_on);
                    LoggerService.insertLog(clsName, "Network reachable via WiFi", null);
                }
                else
                {
                    // wifi is disabled
                    //wifiStatusIconFw.setIcon(R.drawable.wifi_off);
                    LoggerService.insertLog(clsName, "Network not reachable", null);
                }
            }
        }
    };*//*


    public void attemptAdminAccess(MenuItem menuItem){

        String menuProp = "";
        if(menuItem.getItemId()==R.id.showTestButtonsFw){
            menuProp = "SHOW_BUTTONS";
        } else if(menuItem.getItemId()==R.id.hideTestButtonsFw){
            menuProp = "HIDE_BUTTONS";
        } else if(menuItem.getItemId()==R.id.goToAppSettingsFw){
            menuProp = "APP_SETTINGS";
        }

        if("".equalsIgnoreCase(menuProp)){
            return;
        }

        if(AppUtils.adminAccess){
            doMenuAction(menuProp);
        } else {
            showAdminDialog(menuProp);
        }

    }

    private void showAdminDialog(final String menuProp){

        final EditText adminPwText = new EditText(FitWorkActivityOld.this);
        adminPwText.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        adminPwText.setSelection(adminPwText.getText().length());

        final FrameLayout layout = new FrameLayout(FitWorkActivityOld.this);
        layout.addView(adminPwText, new FrameLayout.LayoutParams(
                250,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        final AlertDialog dialog = new AlertDialog.Builder(FitWorkActivityOld.this)
                .setView(layout)
                .setTitle("Admin Girişi")
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                })
                .setNegativeButton("Vazgeç", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean wantToCloseDialog = false;
                if(AppDefaults.SETTINGS_PW.equals(adminPwText.getText().toString())) {
                    AppUtils.adminAccess = true;
                    wantToCloseDialog = true;
                    doMenuAction(menuProp);
                } else {
                    adminPwText.setError("Hatalı Şifre!");
                }

                if(wantToCloseDialog)
                    dialog.dismiss();
            }
        });

    }

    private void toSettingsActivity(){

        Intent i = new Intent(FitWorkActivityOld.this, SettingsActivity.class);
        startActivity(i);

    }

    private void doMenuAction(String menuProp){

        if(menuProp.equals("APP_SETTINGS")){
            toSettingsActivity();
        }
        else if(menuProp.equals("SHOW_BUTTONS")){

            //customWorkPersonButton.setVisibility(View.VISIBLE);
            addPersonButton.setVisibility(View.VISIBLE);
            removePersonButton.setVisibility(View.VISIBLE);
            mi_showTestButtons.setVisible(false);
            mi_hideTestButtons.setVisible(true);

        } else if(menuProp.equals("HIDE_BUTTONS")) {

            //customWorkPersonButton.setVisibility(View.GONE);
            addPersonButton.setVisibility(View.GONE);
            removePersonButton.setVisibility(View.GONE);
            mi_showTestButtons.setVisible(true);
            mi_hideTestButtons.setVisible(false);

        }
    }

    public class RealmClearTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    progressLayoutFw = (RelativeLayout) findViewById(R.id.progressLayoutFw);
                    progressLayoutFw.setVisibility(View.VISIBLE);

                }
            });

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            // Realm Operations
            Realm mRealm = Realm.getDefaultInstance();

            // Clear previous sessions data
            try {

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        Calendar timeOut = Calendar.getInstance();
                        timeOut.add(Calendar.SECOND, -60);

                        //realm.where(FitPerson.class).findAll().deleteAllFromRealm();
                        //realm.where(FitWorkPerson.class).findAll().deleteAllFromRealm();
                        realm.where(FitWorkDataMinDetail.class)
                                .lessThan("insertDate", timeOut.getTime())
                                .findAll().deleteAllFromRealm();
                    }
                });


            // Create FitWork if none
            long fitWorkCount = mRealm.where(FitWork.class).count();
            if(fitWorkCount == 0){

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        FitWorkType fitWorkType = realm.createObject(FitWorkType.class, UUID.randomUUID().toString());
                        fitWorkType.setCreateDate(new Date());
                        fitWorkType.setUpdateDate(fitWorkType.getCreateDate());
                        fitWorkType.setTypeIsValid(true);
                        fitWorkType.setTypeName("Kardiyo");

                        fitWork = realm.createObject(FitWork.class, UUID.randomUUID().toString());
                        fitWork.setCreateDate(new Date());
                        fitWork.setActivityName("Kardiyo");
                        fitWork.setActivityType(fitWorkType);


                    }
                });

            } else {

                fitWork = mRealm.where(FitWork.class).findFirst();

            }

            // Clear FitWorkPerson test objects
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    realm.where(FitWorkPerson.class).equalTo("fitPerson.testUser", true).findAll().deleteAllFromRealm();

                }
            });





            } catch (Exception e) {

                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());

            } finally {
                mRealm.closeDialog();
            }


            */
/*IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);*//*


            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    progressLayoutFw.setVisibility(View.GONE);

                }
            });

            super.onPostExecute(aVoid);
        }
    }

    private void manageAppSettings(){

        Realm bgRealm = null;

        try {

            bgRealm = Realm.getDefaultInstance();

            bgRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<SettingsData> settingsDataResult = realm.where(SettingsData.class).findAll();

                    if (settingsDataResult != null && settingsDataResult.size() > 0) {

                        SettingsData appSettingsData = settingsDataResult.first();
                        Settings.fromRealmDB(appSettingsData);

                        // Save to file
                        FileUtil.saveSettingsToFile(FitWorkActivityOld.this, Settings.toJSONString());

                    } else {

                        // Try reading from file
                        String jsonString = FileUtil.getSettingsFromFile(FitWorkActivityOld.this);
                        if (jsonString != null && !jsonString.equalsIgnoreCase("")) {
                            // Set Settings from File
                            Settings.fromJSONData(jsonString);
                            SettingsData appSettingsData = realm.createObject(SettingsData.class);
                            appSettingsData.updateDBFromSettings();

                        } else {
                            // Set Default Settings
                            SettingsData appSettingsData = realm.createObject(SettingsData.class);
                            appSettingsData.setDefaultSettings();
                            Settings.fromRealmDB(appSettingsData);
                        }

                    }


                }
            });

        } catch (Exception e){
            LoggerService.insertLog(clsName, "Could not create default settings", e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            if(bgRealm!=null) {
                bgRealm.closeDialog();
            }
        }

    }


    public void showWorkSummary(String testHrSensorId){

        PrepareSummaryPersonTask prepTask = new PrepareSummaryPersonTask();
        prepTask.execute(testHrSensorId);

        UpdateChartsTask chartsTask = new UpdateChartsTask();
        chartsTask.execute(testHrSensorId);

    }

    private void hideWorkSummary(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chartFrame.setVisibility(View.GONE);
            }
        });
    }

    private class UpdateChartsTask extends AsyncTask<String, Void, Void>{


        @Override
        protected Void doInBackground(String... strings) {

            final String hrSensorId = strings[0];

            try {

                barData = dbService.getBarData(hrSensorId);
                lineData = dbService.getLineData(hrSensorId);

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    chartFrame.setVisibility(View.VISIBLE);

                    chartCalZone.setData(barData);
                    chartHrMin.setData(lineData);

                    chartCalZone.animateY(3000);
                    chartHrMin.animateY(3000);
                    //chartHrMin.invalidate();

                }
            });

            super.onPostExecute(aVoid);
        }
    }

    private class PrepareSummaryPersonTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {

            final String hrSensorId = strings[0];

            summaryWorkPerson = dbService.prepareSummaryPerson(hrSensorId);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    sumPersonNickname.setText(summaryWorkPerson.getmNickname());
                    Picasso.with(FitWorkActivityOld.this)
                            .load(summaryWorkPerson.getmImageUrl())
                            .placeholder(summaryWorkPerson.getmGender()==1?R.drawable.male:R.drawable.female)
                            //.placeholder(R.drawable)
                            .into(personImageView);
                    */
/*if(summaryWorkPerson.getmImageBitmap()!=null) {
                        personImageView.setImageBitmap(summaryWorkPerson.getmImageBitmap());
                    }*//*

                    sumDurationVal.setText(summaryWorkPerson.getmDuration());
                    sumTotalCalVal.setText(summaryWorkPerson.getmTotalCal());
                    sumCalMinVal.setText(summaryWorkPerson.getmCalMin());
                    sumMaxPerfVal.setText(summaryWorkPerson.getmMaxPerf());
                    sumAvgPerfVal.setText(summaryWorkPerson.getmAvgPerf());
                    sumMaxHrVal.setText(summaryWorkPerson.getmMaxHr());
                    sumAvgHrVal.setText(summaryWorkPerson.getmAvgHr());
                }
            });

            super.onPostExecute(aVoid);
        }
    }

    public void resetActivity(){

        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(i);

    }

    private void logMemoryState(){

        Log.i("MEMORY STATE", AppUtils.getMemoryState());
        LoggerService.insertLog(clsName, "Memory State", AppUtils.getMemoryState());

    }

    */
/*private void interruptThreads(){

        try {

            if(mainBgThread!=null) {
                mainBgThread.interrupt();
            }
            if(uiThread!=null){
                uiThread.interrupt();
            }
            //hourlyResetThread.interrupt();

            // UDP Threads
            udpService.interruptUDPThreads();

        } catch (Exception e) {
            Log.i("Exception", e.getMessage());
        }

    }*//*


}
*/
