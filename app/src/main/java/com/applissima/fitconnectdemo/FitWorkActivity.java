package com.applissima.fitconnectdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

public class FitWorkActivity extends AppCompatActivity {

    private final String clsName = "FitWorkActivity";

    private DatabaseService dbService;
    private boolean mBoundDbService;
    private GridLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private List<FitWorkPersonCard> fwPersonCardList;
    private FwPersonAdapter mAdapter;
    private LinearLayout testButtonsLayout;
    private MenuItem wifiStatusIconFw;
    private MenuItem hubStatusIconFw;
    private MenuItem mi_showTestButtons;
    private MenuItem mi_hideTestButtons;
    private MenuItem mi_downloadNewVersion;
    private TextView timeTextView;

    // Charts
    private CircleImageView personImageView;
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

    // ChartTest
    private FrameLayout chartFrame;

    // Firebase parameters
    private DatabaseReference siteRef;
    private Query query;
    private ValueEventListener fbGetListener;
    private ChildEventListener fbChangeListener;
    private SettingsData appSettingsForVersion;
    private AlertDialog clubLoginDialog;
    private AlertDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore fsdb;
    private DocumentReference versionRef;
    private ListenerRegistration versionListener;
    private CollectionReference membersRef;
    private ListenerRegistration membersListener;

    private View clubLoginLayout;
    private View clubLoginProgressLayout;

    private MainHandler mHandler = new MainHandler();

    private boolean serviceStartInProcess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme_ActionBar_Transparent);
        setContentView(R.layout.activity_fit_work);
        initActionBar();

        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this,
                FitWorkActivity.class));

        super.onCreate(savedInstanceState);

    }


    @Override
    protected void onResume() {

        super.onResume();

        Log.i("PROCESS_INFO", "PROCESS ID: " + String.valueOf(android.os.Process.myPid()));

        Log.i("TASK_INFO", String.valueOf(this.getTaskId()));
        Log.i("CLASS_INFO", this.toString());

        Log.i("ACT RESUMED", "ACTIVITY RESUMED.");
        LoggerService.insertLog(clsName, "ACTIVITY RESUMED.", null);

        broadcastFcServiceForVersionUpdate(false);

        // Manage App Settings
        manageAppSettings();
        initViews();

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppDefaults.PERMISSION_WRITE_STORAGE);

        } else {
            initAuthorization();
        }

    }

    private void continueInit(){

        if(getIntent().getBooleanExtra("startedByService", false)){

            getIntent().removeExtra("startedByService");

            LoggerService.insertLog(clsName, "ACTIVITY RESTARTED BY FCSERVICE!", null);

            MailData mailData = new MailData();
            mailData.prepareDefault();
            mailData.setMailType(MailType.RESTARTEDBYSERVICE);
            mailData.setEmailSubject("FitConnect Restarted by Service - Location: " + Settings.locationId + " | " + Settings.siteId);
            mailData.setEmailBody("");
            mailData.setFileList(null);

            new SendMailTask().execute(mailData);
        }

        (new RealmClearTask()).execute();
        attemptInitServices();

    }

    private void initAuthorization(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser==null){
            if(!"".equals(Settings.clubEmail)){
                clubLogin(Settings.clubEmail, Settings.clubPw, Settings.siteId, false);
            } else {
                createClubLoginDialog();
            }
        } else {
            initFirestore();
            continueInit();
        }
    }

    private void initFirestore(){

        try {

            fsdb = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();

            fsdb.setFirestoreSettings(settings);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initFirestoreListeners(){

        try {

            versionRef = fsdb.collection(AppDefaults.FS_VERSIONS)
                    .document(String.valueOf(Settings.locationId));

            versionListener = versionRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    if(documentSnapshot != null && documentSnapshot.exists()){
                        StaticVariables.availableVersion = documentSnapshot.toObject(FsVersion.class);
                        handleNewVersion();
                    }
                }
            });

            membersRef = fsdb.collection(AppDefaults.FS_CLUB_BELTS)
                    .document(Settings.clubEmail)
                    .collection(AppDefaults.FS_MEMBERS);

            membersListener = membersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if(e==null && documentSnapshots.getDocumentChanges().size()>0){
                        for(DocumentChange dc :documentSnapshots.getDocumentChanges()){
                            Member member = dc.getDocument().toObject(Member.class);
                            Log.i("TEMPBELT", member.toString());
                            Log.i("TEMPBELT", "Type: " + dc.getType().name());
                            switch (dc.getType()){
                                case ADDED:
                                    dbService.addTempBeltLocal(member);
                                    break;
                                case REMOVED:
                                    dbService.deleteTempBeltLocal(member);
                                    break;
                            }
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }

    }


    private void handleNewVersion(){

        try {

            if(mi_downloadNewVersion!=null) {

                if (StaticVariables.availableVersion.getVersionNo() != null
                        && !StaticVariables.availableVersion.getVersionNo().equals(AppDefaults.CURRENT_VERSION)) {

                    mi_downloadNewVersion.setVisible(true);

                } else {

                    mi_downloadNewVersion.setVisible(false);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }



    }

    private void attemptInitServices(){

        if(!serviceStartInProcess) {
            (new InitServicesTask()).execute();
        }

    }

    public void updateVersion(MenuItem menuItem){

        // Stop listeners and services as onPause
        if(query!=null){
            query.removeEventListener(fbGetListener);
            query.removeEventListener(fbChangeListener);
        }

        if(mBoundDbService) {
            unbindService(mDbConnection);
            mBoundDbService = false;
        }

        unregisterSnackbarBroadcast();

        removeFirestoreListeners();

        stopService(new Intent(FitWorkActivity.this, ThreadService.class));
        stopService(new Intent(FitWorkActivity.this, DatabaseService.class));
        stopService(new Intent(getApplicationContext(), AntPlusService.class));

        if(mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }

        // Start versioning...
        Map<String, String> versionMap = new HashMap<String, String>();
        versionMap.put("versionNo", StaticVariables.availableVersion.getVersionNo());
        versionMap.put("versionUrl", StaticVariables.availableVersion.getVersionUrl());

        broadcastFcServiceForVersionUpdate(true);

        UpdateVersionTask updateVersionTask = new UpdateVersionTask();
        updateVersionTask.execute();

    }

    private void broadcastFcServiceForVersionUpdate(boolean versionUpdating){

        try {

            Intent intent = new Intent();
            intent.setAction(AppDefaults.ACTION_BRDC_UPDATE);
            intent.putExtra("versionUpdate", versionUpdating);
            sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void broadcastFcServiceSignal(){

        try {

            Intent intent = new Intent();
            intent.setAction(AppDefaults.ACTION_BRDC_SIGNAL);
            intent.putExtra("signalTime", Calendar.getInstance().getTimeInMillis());
            sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testBroadcastPauseService(View view){

        broadcastFcServiceForVersionUpdate(true);

    }

    // Service Status Changed
    private BroadcastReceiver antInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                (new TaskEventAntPlusInfo()).execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    private class InitServicesTask extends AsyncTask<Void, Void, Void>{

        int count;

        @Override
        protected void onPreExecute() {

            serviceStartInProcess = true;
            count = 0;
            createProgressDialog(getString(R.string.text_waiting_start));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                Thread.sleep(3000);

                int year = Calendar.getInstance().get(Calendar.YEAR);

                while (year<2017) {
                    Log.i("WhileLoop", Calendar.getInstance().toString());
                    count++;
                    Thread.sleep(2000);
                    year = Calendar.getInstance().get(Calendar.YEAR);
                    if(count==5) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FitWorkActivity.this, getString(R.string.text_check_timedate),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        count = 0;
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();

            initServices();

            super.onPostExecute(aVoid);
        }
    }

    private void initViews(){

        try {

            Calendar timeOut = Calendar.getInstance();
            timeOut.add(Calendar.SECOND, -60);

            // View initials
            mRecyclerView = (RecyclerView) findViewById(R.id.fitWorkRecyclerView);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new GridLayoutManager(FitWorkActivity.this, 2);
            mRecyclerView.setLayoutManager(mLayoutManager);


            fwPersonCardList = new ArrayList<FitWorkPersonCard>();
            mAdapter = new FwPersonAdapter(FitWorkActivity.this, fwPersonCardList, 1);
            mRecyclerView.setAdapter(mAdapter);


            testButtonsLayout = (LinearLayout) findViewById(R.id.testButtonsLayout);

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
            personImageView = (CircleImageView) findViewById(R.id.ch_personImage);
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
            xAxis.setTextColor(ContextCompat.getColor(FitWorkActivity.this, R.color.userWhite));

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
            xAxis2.setTextColor(ContextCompat.getColor(FitWorkActivity.this, R.color.userWhite));

            YAxis yAxisLeft2 = chartHrMin.getAxisLeft();
            yAxisLeft2.setGranularityEnabled(true);
            yAxisLeft2.setGranularity(1f);
            yAxisLeft2.setDrawZeroLine(false);
            yAxisLeft2.setDrawAxisLine(false);
            yAxisLeft2.setDrawGridLines(false);
            yAxisLeft2.setTextColor(ContextCompat.getColor(FitWorkActivity.this, R.color.userWhite));

            YAxis yAxisRight2 = chartHrMin.getAxisRight();
            yAxisRight2.setEnabled(false);

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }

    }

    private void initServices(){

        initDbService();

    }

    private void initDbService(){

        try {
            
            // Bind DbService
            Intent dbIntent = new Intent(FitWorkActivity.this, DatabaseService.class);
            startService(dbIntent);
            bindService(dbIntent, mDbConnection, Context.BIND_AUTO_CREATE);

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }

    }

    private void initThreadService(){

        // Start ThreadService
        Intent threadIntent = new Intent(FitWorkActivity.this, ThreadService.class);
        threadIntent.putExtra("MESSENGER", new Messenger(mHandler));
        startService(threadIntent);


    }

    private void initAntPlusService(){

        // Bind AntService
        Intent antServiceIntent = new Intent(getApplicationContext(), AntPlusService.class);
        startService(antServiceIntent);

    }

    private ServiceConnection mDbConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            DatabaseService.DatabaseServiceBinder binder = (DatabaseService.DatabaseServiceBinder) service;
            dbService = binder.getService();
            mBoundDbService = true;

            initFirestoreListeners();
            initThreadService();
            registerAntPlusServiceInfo();
            initAntPlusService();
            registerSnackbarBroadcast();

            serviceStartInProcess = false;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBoundDbService = false;
        }
    };

    // Adds a new random simulation person
    public void addPerson(View view){

        Settings.simulationPersonCount++;

    }

    // removes last added simulation person
    public void removePerson(View view){

        if(Settings.simulationPersonCount>0) {
            Settings.simulationPersonCount--;
        }

    }

    private void registerAntPlusServiceInfo(){

        try {

            LocalBroadcastManager.getInstance(this).registerReceiver(antInfoReceiver,
                    new IntentFilter(AppDefaults.DATA_STATUS));

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void unregisterAntPlusServiceInfo(){

        try {

            LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(antInfoReceiver);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void registerSnackbarBroadcast(){

        try {

            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(snackBarReceiver, new IntentFilter(AppDefaults.ACTION_BRDC_SNACKBAR));

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void unregisterSnackbarBroadcast(){

        try {

            LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(snackBarReceiver);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private class TaskEventCards extends AsyncTask<EventCards, Void, Void>{

        EventCards event = null;

        @Override
        protected Void doInBackground(EventCards... events) {

            broadcastFcServiceSignal();

            event = events[0];

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            try {

                fwPersonCardList = event.getCards();

                int gridSize = getGridSize(fwPersonCardList.size());

                mAdapter = new FwPersonAdapter(FitWorkActivity.this, fwPersonCardList, gridSize);
                mLayoutManager.setSpanCount(gridSize);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
            } catch (Exception e) {
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
            }

            super.onPostExecute(aVoid);
        }

        private int getGridSize(int listSize){

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

    }

    private class TaskEventTimer extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {

            String dateStr = null;
            try {
                dateStr = AppUtils.getDateString(Calendar.getInstance().getTime(), AppDefaults.TIME_FORMAT_CLOCK);
            } catch (Exception e) {
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
            }

            return dateStr;
        }

        @Override
        protected void onPostExecute(String dateStr) {

            if (timeTextView != null) {
                timeTextView.setText(dateStr);
            } else {
                return;
            }

            super.onPostExecute(dateStr);
        }
    }


    private class TaskEventNetworks extends AsyncTask<EventNetworks, Void, Void>{

        EventNetworks event = null;

        @Override
        protected Void doInBackground(EventNetworks... events) {

            event = events[0];

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            try {

                boolean[] networkStatus = event.getNetworkStatus();
                boolean isWifiOn = networkStatus[0];
                boolean isInternetOn = networkStatus[1];
                boolean isAntOn = StaticVariables.isAntOn;

                if(isWifiOn) {
                    if(isInternetOn){
                        wifiStatusIconFw.setIcon(R.drawable.internet_on);
                    } else {
                        wifiStatusIconFw.setIcon(R.drawable.wifi_on);
                    }

                } else {
                    wifiStatusIconFw.setIcon(R.drawable.wifi_off);
                }

                if(isAntOn){
                    hubStatusIconFw.setIcon(R.drawable.hub_on);
                } else {
                    hubStatusIconFw.setIcon(R.drawable.hub_off);
                }

            } catch (Exception e) {
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
            }

            super.onPostExecute(aVoid);
        }
    }

    private class TaskEventWorkSummary extends AsyncTask<EventWorkSummary, Void, Void>{

        EventWorkSummary event = null;

        @Override
        protected Void doInBackground(EventWorkSummary... events) {

            event = events[0];

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            try {

                if(event.isShowWorkSummary()) {

                    // Prepare and show work summary
                    SummaryWorkPerson summaryWorkPerson = event.getSummaryWorkPerson();
                    sumPersonNickname.setText(summaryWorkPerson.getmNickname());
                    Picasso.with(FitWorkActivity.this)
                            .load(summaryWorkPerson.getmImageUrl())
                            .noFade()
                            .placeholder(summaryWorkPerson.getmGender() == 1 ? R.drawable.male : R.drawable.female)
                            .into(personImageView);
                    sumDurationVal.setText(summaryWorkPerson.getmDuration());
                    sumTotalCalVal.setText(summaryWorkPerson.getmTotalCal());
                    sumCalMinVal.setText(summaryWorkPerson.getmCalMin());
                    sumMaxPerfVal.setText(summaryWorkPerson.getmMaxPerf());
                    sumAvgPerfVal.setText(summaryWorkPerson.getmAvgPerf());
                    sumMaxHrVal.setText(summaryWorkPerson.getmMaxHr());
                    sumAvgHrVal.setText(summaryWorkPerson.getmAvgHr());


                    UpdateChartsTask chartsTask = new UpdateChartsTask();
                    chartsTask.execute(summaryWorkPerson.getmUserName());

                } else {

                    // Hide work summary
                    if(chartFrame.getVisibility() == View.VISIBLE) {
                        chartFrame.setVisibility(View.GONE);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
            }

            super.onPostExecute(aVoid);
        }
    }

    private class TaskEventAntPlusInfo extends AsyncTask<Void, Void, Void>{

        boolean antIsOn = false;

        @Override
        protected Void doInBackground(Void... voids) {

            antIsOn = StaticVariables.isAntOn;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            // AntPlus service is on
            try {
                if(antIsOn){
                    hubStatusIconFw.setIcon(R.drawable.hub_on);
                }
                // AntPlus service is off
                else {
                    hubStatusIconFw.setIcon(R.drawable.hub_off);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
            }


            super.onPostExecute(aVoid);
        }
    }

    private class TaskHandleTempBelts extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            try {

                List<String> activeTempBelts = dbService.getActiveTempBelts();

                if(activeTempBelts.size()>0) {

                    WriteBatch updateBatch = fsdb.batch();

                    for (String tempBeltId : activeTempBelts) {
                        updateBatch.update(membersRef.document(tempBeltId), "lastDataUpdateDate", Calendar.getInstance().getTime());
                    }

                    updateBatch.commit()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("UpdateBatch", "Successfully committed!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });

                }

            } catch (Exception e){
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
            }

            return null;
        }
    }

    private class TaskExpiredTempBelts extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            try {

                findExpiredTempBelts();

            } catch (Exception e){
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
            }

            return null;
        }
    }

    private void findExpiredTempBelts(){

        final List<String> expiredTempBelts = new ArrayList<String>();

        try {

            Calendar timeOut = Calendar.getInstance();
            timeOut.add(Calendar.MINUTE, -1 * Settings.tempBeltsExp);

            membersRef.whereLessThan("lastDataUpdateDate", timeOut.getTime())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            if(documentSnapshots.getDocuments()!=null
                                    && documentSnapshots.getDocuments().size()>0){
                                for(DocumentSnapshot snapshot :documentSnapshots.getDocuments()){
                                    expiredTempBelts.add(snapshot.getId());
                                }
                                deleteExpiredTempBelts(expiredTempBelts);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });

        } catch (Exception e){
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }

    }

    private void deleteExpiredTempBelts(List<String> expiredTempBelts){

        if(expiredTempBelts!=null && expiredTempBelts.size()>0){

            WriteBatch deleteBatch = fsdb.batch();

            for(String expiredTempBeltId :expiredTempBelts){
                deleteBatch.delete(membersRef.document(expiredTempBeltId));
            }

            deleteBatch.commit()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("DeleteBatch", "Successfully committed!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });

        }

    }

    private class MainHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case AppDefaults.EVENT_CARDS:
                    EventCards eventCards = (EventCards) msg.obj;
                    (new TaskEventCards()).execute(eventCards);
                    break;
                case AppDefaults.EVENT_TIMER:
                    (new TaskEventTimer()).execute();
                    break;
                case AppDefaults.EVENT_NETWORKS:
                    EventNetworks eventNetworks = (EventNetworks) msg.obj;
                    (new TaskEventNetworks()).execute(eventNetworks);
                    break;
                case AppDefaults.EVENT_WORKSUMMARY:
                    EventWorkSummary eventWorkSummary = (EventWorkSummary) msg.obj;
                    (new TaskEventWorkSummary()).execute(eventWorkSummary);
                    break;
                case AppDefaults.EVENT_RESTART:
                    restartApp();
                    break;
                case AppDefaults.EVENT_TEMP_BELTS:
                    (new TaskHandleTempBelts()).execute();
                    break;
                case AppDefaults.EVENT_EXP_TEMP_BELTS:
                    (new TaskExpiredTempBelts()).execute();
                    break;
            }

        }
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {

        Log.i("ACT STOPPED", "ACTIVITY STOPPED.");
        super.onStop();
    }

    @Override
    protected void onPause() {

        try {

            if(query!=null){
                query.removeEventListener(fbGetListener);
                query.removeEventListener(fbChangeListener);
            }

            if(mBoundDbService) {
                unbindService(mDbConnection);
                mBoundDbService = false;
            }

            unregisterAntPlusServiceInfo();
            unregisterSnackbarBroadcast();

            removeFirestoreListeners();

            stopService(new Intent(FitWorkActivity.this, ThreadService.class));
            stopService(new Intent(FitWorkActivity.this, DatabaseService.class));
            stopService(new Intent(getApplicationContext(), AntPlusService.class));

            if(mHandler!=null){
                mHandler.removeCallbacksAndMessages(null);
            }

            Log.i("ACT PAUSED", "ACTIVITY PAUSED.");
            LoggerService.insertLog(clsName, "ACTIVITY PAUSED.", null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onPause();

    }

    private void removeFirestoreListeners(){

        if(membersListener != null){
            membersListener.remove();
        }
        if(versionListener != null){
            versionListener.remove();
        }

    }


    private void initActionBar(){

        try {

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_fitwork, menu);

            mi_downloadNewVersion = menu.findItem(R.id.newVersionFw);
            mi_downloadNewVersion.setVisible(false);

            mi_showTestButtons = menu.findItem(R.id.showTestButtonsFw);
            mi_hideTestButtons = menu.findItem(R.id.hideTestButtonsFw);

            wifiStatusIconFw = menu.findItem(R.id.wifiStatusIconFw);
            hubStatusIconFw = menu.findItem(R.id.hubStatusIconFw);


            if(StaticVariables.cntWifiOn > 0){

                try {

                    if(StaticVariables.cntInternetOn > 0){
                        wifiStatusIconFw.setIcon(R.drawable.internet_on);
                    } else {
                        wifiStatusIconFw.setIcon(R.drawable.wifi_on);
                    }

                } catch (Exception e){
                    wifiStatusIconFw.setIcon(R.drawable.wifi_on);
                    e.printStackTrace();
                }


            } else {
                wifiStatusIconFw.setIcon(R.drawable.wifi_off);
            }

            if (StaticVariables.availableVersion !=null
                    && StaticVariables.availableVersion.getVersionNo() != null
                    && !StaticVariables.availableVersion.getVersionNo().equals(AppDefaults.CURRENT_VERSION)) {

                mi_downloadNewVersion.setVisible(true);

            } else {

                mi_downloadNewVersion.setVisible(false);

            }

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }

        return true;
    }



    public void attemptAdminAccess(MenuItem menuItem){

        String menuProp = "";
        if(menuItem.getItemId()==R.id.showTestButtonsFw){
            menuProp = "SHOW_BUTTONS";
        } else if(menuItem.getItemId()==R.id.hideTestButtonsFw){
            menuProp = "HIDE_BUTTONS";
        } else if(menuItem.getItemId()==R.id.goToAppSettingsFw){
            menuProp = "APP_SETTINGS";
        } else if(menuItem.getItemId()==R.id.logoutFw){
            menuProp = "APP_LOGOUT";
        }

        if("".equalsIgnoreCase(menuProp)){
            return;
        }

        showAdminDialog(menuProp);

    }

    private void showAdminDialog(final String menuProp){

        final EditText adminPwText = new EditText(FitWorkActivity.this);
        adminPwText.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final FrameLayout layout = new FrameLayout(FitWorkActivity.this);
        layout.addView(adminPwText, new FrameLayout.LayoutParams(
                250,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        final AlertDialog dialog = new AlertDialog.Builder(FitWorkActivity.this)
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




        adminPwText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(AppDefaults.SETTINGS_PW.equals(adminPwText.getText().toString())) {
                    dialog.dismiss();
                    //AppUtils.adminAccess = true;
                    doMenuAction(menuProp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        adminPwText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Boolean wantToCloseDialog = false;
                    if(AppDefaults.SETTINGS_PW.equals(adminPwText.getText().toString())) {
                        wantToCloseDialog = true;
                        doMenuAction(menuProp);
                    } else {
                        adminPwText.setError("Hatalı Şifre!");
                    }
                }
                return false;
            }
        });
        adminPwText.setSelection(adminPwText.getText().length());

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean wantToCloseDialog = false;
                if(AppDefaults.SETTINGS_PW.equals(adminPwText.getText().toString())) {
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

        Intent i = new Intent(FitWorkActivity.this, SettingsActivity.class);
        startActivity(i);

    }

    private void restartApp(){
        finish();
        startActivity(getIntent());
    }

    private void doMenuAction(String menuProp){

        if(menuProp.equals("APP_SETTINGS")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toSettingsActivity();
                }
            }, 1000);
        }
        else if(menuProp.equals("SHOW_BUTTONS")){

            testButtonsLayout.setVisibility(View.VISIBLE);
            mi_showTestButtons.setVisible(false);
            mi_hideTestButtons.setVisible(true);

        } else if(menuProp.equals("HIDE_BUTTONS")) {

            testButtonsLayout.setVisibility(View.GONE);
            mi_showTestButtons.setVisible(true);
            mi_hideTestButtons.setVisible(false);

        } else if(menuProp.equals("APP_LOGOUT")){

            logoutFirestore();
        }
    }

    private void logoutFirestore(){

        mAuth.signOut();
        removeClubInfo();
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }

    public class RealmClearTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            final Calendar timeOut = Calendar.getInstance();
            timeOut.add(Calendar.SECOND, -60);

            // Realm Operations
            Realm mRealm = null;

            // Clear previous sessions data
            try {

                mRealm = Realm.getDefaultInstance();

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        realm.where(FitWorkPerson.class)
                                .lessThan("hrLastDataUpdateDate", timeOut.getTime())
                                .findAll().deleteAllFromRealm();

                        realm.where(FitWorkPerson.class).equalTo("fitPerson.testUser", true).findAll().deleteAllFromRealm();

                        if(realm.where(FitWork.class).count() == 0){

                            FitWorkType fitWorkType = realm.createObject(FitWorkType.class, UUID.randomUUID().toString());
                            fitWorkType.setCreateDate(new Date());
                            fitWorkType.setUpdateDate(fitWorkType.getCreateDate());
                            fitWorkType.setTypeIsValid(true);
                            fitWorkType.setTypeName("Kardiyo");

                            FitWork fitWork = realm.createObject(FitWork.class, UUID.randomUUID().toString());
                            fitWork.setCreateDate(new Date());
                            fitWork.setActivityName("Kardiyo");
                            fitWork.setActivityType(fitWorkType);

                        }
                    }
                });

            } catch (Exception e) {

                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());

            } finally {
                if(mRealm!=null) {
                    mRealm.close();
                }
            }

            return null;

        }

    }

    private void manageAppSettings(){

        StaticVariables.init(new WeakReference<Context>(this));
        ProcessController.init();

        if(Settings.hubList == null) {
            Settings.hubList = new ArrayList<String>();
        }

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
                        FileUtil.saveSettingsToFile(FitWorkActivity.this, Settings.toJSONString());

                    } else {

                        // Try reading from file
                        String jsonString = FileUtil.getSettingsFromFile(FitWorkActivity.this);
                        if (jsonString != null
                                && !jsonString.equalsIgnoreCase("")
                                && Settings.isJSONValid(jsonString)) {

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
                bgRealm.close();
            }
        }

    }

    public void forceANRMain(View view){
        
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private class UpdateChartsTask extends AsyncTask<String, Void, Void>{

        private BarData barData = null;
        private LineData lineData = null;

        @Override
        protected void onPreExecute() {

            chartCalZone.clear();
            chartHrMin.clear();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

                final String userName = strings[0];

                try {

                    barData = dbService.getBarData(userName);
                    lineData = dbService.getLineData(userName);

                } catch (Exception e) {
                    e.printStackTrace();
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            try {

                chartFrame.setVisibility(View.VISIBLE);

                chartCalZone.setData(barData);
                chartHrMin.setData(lineData);

                chartCalZone.animateY(3000);
                chartHrMin.animateY(3000);

            } catch (Exception e) {
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
            }


            super.onPostExecute(aVoid);
        }
    }


    private class UpdateVersionTask extends AsyncTask<Void, Void, Void> {

        private final String clsName = "UpdateVersionService";
        private String urlStr = "";

        @Override
        protected void onPreExecute() {

            createProgressDialog(getString(R.string.text_waiting_update));

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... aVoid) {

            try {

                String folderName = "FitConnectApks";

                String gsRefUrl = "";
                if(StaticVariables.availableVersion!=null
                        && !"".equals(StaticVariables.availableVersion.getVersionUrl())){
                    gsRefUrl = StaticVariables.availableVersion.getVersionUrl();
                } else {
                    return null;
                }

                File dir = new File(Environment.getExternalStorageDirectory(),
                        folderName);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                final File localFile = File.createTempFile("FitConnect", "apk", dir);
                FirebaseStorage.getInstance()
                        .getReferenceFromUrl(gsRefUrl)
                        .getFile(localFile)
                        .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                                progressDialog.dismiss();

                                if(task.isSuccessful()) {

                                    Intent i = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                                    i.setDataAndType(Uri.fromFile(localFile), "application/vnd.android.package-archive");
                                    i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(i);

                                } else {
                                    Toast.makeText(FitWorkActivity.this,
                                            getString(R.string.text_error_update), Toast.LENGTH_LONG).show();
                                }

                            }
                        });


            } catch (Exception e) {
                Log.e("YourApp", "Well that didn't work out so well...");
                Log.e("YourApp", e.getMessage());
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                return null;
            }

            return null;
        }

    }

    public void createClubLoginDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FitWorkActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View clubLoginView = inflater.inflate(R.layout.dialog_club_login, null);

        clubLoginLayout = clubLoginView.findViewById(R.id.clubLoginLayout);
        clubLoginLayout.setVisibility(View.VISIBLE);
        clubLoginProgressLayout = clubLoginView.findViewById(R.id.clubLoginProgressLayout);
        clubLoginProgressLayout.setVisibility(View.GONE);
        final EditText clubEmailVal = (EditText) clubLoginView.findViewById(R.id.clubEmail_value);
        final EditText clubPwVal = (EditText) clubLoginView.findViewById(R.id.clubPw_value);
        final EditText siteIdVal = (EditText) clubLoginView.findViewById(R.id.siteId_value);
        Button clubLoginOkButton = (Button) clubLoginView.findViewById(R.id.clubLoginOkButton);

        clubLoginOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                attemptClubLogin(clubEmailVal, clubPwVal, siteIdVal);
            }
        });

        dialogBuilder.setView(clubLoginView);

        clubLoginDialog = dialogBuilder.create();
        clubLoginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        clubLoginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        clubLoginDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(KeyEvent.KEYCODE_BACK == keyCode) {
                    return true;
                }
                return false;
            }
        });
        clubLoginDialog.show();

    }

    private void attemptClubLogin(EditText clubEmailVal, EditText clubPwVal, EditText siteIdVal) {

        // Reset errors.
        clubEmailVal.setError(null);
        clubPwVal.setError(null);
        siteIdVal.setError(null);

        // Store values at the time of the login attempt.
        String email = clubEmailVal.getText().toString();
        String password = clubPwVal.getText().toString();
        String siteIdStr = siteIdVal.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if ("".equals(password)) {
            clubPwVal.setError(getString(R.string.text_error_notnull));
            focusView = clubPwVal;
            cancel = true;
        }

        // Check for a valid email address.
        if ("".equals(email) || !email.contains("@")) {
            clubEmailVal.setError(getString(R.string.text_error_notnull));
            focusView = clubEmailVal;
            cancel = true;
        }

        // Check for a valid siteId.
        if ("".equals(siteIdStr) || siteIdStr.length() < 2) {
            siteIdVal.setError(getString(R.string.text_error_notnull));
            focusView = siteIdVal;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgressClubLogin(true);
            clubLogin(email, password, Integer.valueOf(siteIdStr), true);
        }
    }

    private void showProgressClubLogin(final boolean show){

        if(show){
            clubLoginLayout.setVisibility(View.GONE);
            clubLoginProgressLayout.setVisibility(View.VISIBLE);
        } else {
            clubLoginLayout.setVisibility(View.VISIBLE);
            clubLoginProgressLayout.setVisibility(View.GONE);
        }

    }

    private void clubLogin(final String email, final String password, final int siteId, final boolean saveClubInfo){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        showProgressClubLogin(false);

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Firestore", "signInWithEmail:success");
                            clubLoginDialog.dismiss();
                            initFirestore();
                            if(saveClubInfo) {
                                saveClubInfo(email, password, siteId);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firestore", "signInWithEmail:failure", task.getException());
                            Toast.makeText(FitWorkActivity.this, "Hatalı giriş!",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }

    private void createProgressDialog(String message){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FitWorkActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View progressView = inflater.inflate(R.layout.progress_layout, null);
        TextView progressTextView = (TextView) progressView.findViewById(R.id.progressTextView);
        progressTextView.setText(message);
        dialogBuilder.setView(progressView);

        progressDialog = dialogBuilder.create();
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.show();

    }

    private void saveSiteInfo(final String siteId){

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    SettingsData appSettingsData = realm.where(SettingsData.class).findFirst();
                    appSettingsData.setSiteId(Integer.valueOf(siteId));

                    Settings.fromRealmDB(appSettingsData);
                }
            });

            // Save to file
            FileUtil.saveSettingsToFile(this, Settings.toJSONString());


        } catch (Exception e){
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if(mRealm!=null) {
                mRealm.close();
            }
        }

    }

    private void saveLocInfo(final String siteId, final String locationId){

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    SettingsData appSettingsData = realm.where(SettingsData.class).findFirst();
                    appSettingsData.setSiteId(Integer.valueOf(siteId));
                    appSettingsData.setLocationId(Integer.valueOf(locationId));

                    Settings.fromRealmDB(appSettingsData);
                }
            });

            // Save to file
            FileUtil.saveSettingsToFile(this, Settings.toJSONString());


        } catch (Exception e){
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if(mRealm!=null) {
                mRealm.close();
            }
        }

    }

    private void removeClubInfo(){

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    SettingsData appSettingsData = realm.where(SettingsData.class).findFirst();
                    appSettingsData.setLocationId(0);
                    appSettingsData.setClubName("");
                    appSettingsData.setClubEmail("");
                    appSettingsData.setClubPw("");

                    Settings.fromRealmDB(appSettingsData);
                }
            });

            // Save to file
            FileUtil.saveSettingsToFile(FitWorkActivity.this, Settings.toJSONString());


        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if (mRealm != null) {
                mRealm.close();
            }
        }

    }

    private void saveClubInfo(final String email, final String password, final int siteId){

        try{

            fsdb.collection(AppDefaults.FS_CLUBS)
                    .whereEqualTo("clubEmail", email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if(task.isSuccessful()) {

                                final Club club = task.getResult().getDocuments().get(0).toObject(Club.class);

                                Realm mRealm = null;

                                try {

                                    mRealm = Realm.getDefaultInstance();
                                    mRealm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {

                                            SettingsData appSettingsData = realm.where(SettingsData.class).findFirst();
                                            appSettingsData.setLocationId(Integer.valueOf(club.getClubId()));
                                            appSettingsData.setClubName(club.getClubName());
                                            appSettingsData.setClubEmail(email);
                                            appSettingsData.setClubPw(password);
                                            appSettingsData.setSiteId(siteId);

                                            Settings.fromRealmDB(appSettingsData);
                                        }
                                    });

                                    // Save to file
                                    FileUtil.saveSettingsToFile(FitWorkActivity.this, Settings.toJSONString());


                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                                            + ":" + e.getStackTrace()[0].getLineNumber());
                                } finally {
                                    if (mRealm != null) {
                                        mRealm.close();
                                    }
                                    continueInit();
                                }

                            }

                        }
                    });

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private class SendMailTask extends AsyncTask<MailData, Void, Boolean> {

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
                SharedPreferences prefs = FitConnectApplication.getInstance().getPrefs();
                prefs.edit().clear().apply();
            }

            super.onPostExecute(isSent);
        }
    }

    private final BroadcastReceiver snackBarReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction()!=null && intent.getAction().equals(AppDefaults.ACTION_BRDC_SNACKBAR)) {

                String message = intent.getStringExtra("beltId")
                        + " belt bilgisi bulunamadı. Lütfen Kiosk ya da FitConnect hesabınızdan belt kaydı yapınız.";

                Snackbar.make(findViewById(R.id.fitWorkRecyclerView), message, Snackbar.LENGTH_LONG).show();

            }
        }
    };

}
