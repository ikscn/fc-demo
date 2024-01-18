package com.applissima.fitconnectdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SettingsActivity extends AppCompatActivity {

    private final String clsName = "SettingsActivity";
    private SettingsObj settingsObject;
    private Button btn_siteId;
    private Button btn_simulationPersonCount;
    private Button btn_checkProfileUpdates; // minutes
    private Button btn_checkUnknownProfile; // minutes
    private Button btn_checkTempBelts; // minutes
    private Button btn_tempBeltsExp; // minutes
    private Button btn_uploadMinAgg; // minutes
    private Button btn_maxUploadData; //
    private Button btn_uploadLogs; // minutes
    private Button btn_distSensibility; // (1-10)
    private Button btn_workSumPeriod; // minutes
    private Button btn_workSumDuration; // minutes
    private Button btn_zoneColors;
    private Button btn_cardDistribution; // i.e. 4|9|16|20
    private Button btn_timeDiff;
    private ImageView btn_updateVersion;

    private Switch sw_lockAppForDevice;
    private TextView tv_machineIp;
    private TextView tv_appVersion;
    private TextView tv_currVersion;
    private TextView tv_clubName;
    private TextView tv_locationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    SettingsData settingsData = realm.where(SettingsData.class).findFirst();
                    settingsObject = settingsData.toObject();
                }
            });

        } catch (Exception e){
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            if(mRealm!=null) {
                mRealm.close();
            }
        }

        initViews();

    }

    private void initViews(){

        final AlertDialog.Builder d = new AlertDialog.Builder(SettingsActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();

        tv_machineIp = (TextView) findViewById(R.id.set_machineIp);
        tv_machineIp.setText(StaticVariables.machineIP);

        tv_appVersion = (TextView) findViewById(R.id.set_appVersion);
        tv_appVersion.setText(StaticVariables.availableVersion.getVersionNo());

        tv_currVersion = (TextView) findViewById(R.id.set_currVersion);
        tv_currVersion.setText(AppDefaults.CURRENT_VERSION);

        tv_clubName = (TextView) findViewById(R.id.set_clubName);
        tv_clubName.setText(Settings.clubName);

        tv_locationId = (TextView) findViewById(R.id.set_locationId);
        tv_locationId.setText(String.valueOf(Settings.locationId));

        btn_siteId = (Button) findViewById(R.id.set_siteId);
        btn_siteId.setText(String.valueOf(settingsObject.getSiteId()));
        btn_siteId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditText(view);
            }
        });

        btn_simulationPersonCount = (Button) findViewById(R.id.set_simPersonCnt);
        btn_simulationPersonCount.setText(String.valueOf(settingsObject.getSimulationPersonCount()));
        btn_simulationPersonCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 0, 20);
            }
        });

        btn_checkProfileUpdates = (Button) findViewById(R.id.set_checkProfileUpd);
        btn_checkProfileUpdates.setText(String.valueOf(settingsObject.getCheckProfileUpdates()));
        btn_checkProfileUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 1, 30);
            }
        });


        btn_checkUnknownProfile = (Button) findViewById(R.id.set_checkUnkProfile);
        btn_checkUnknownProfile.setText(String.valueOf(settingsObject.getCheckUnknownProfile()));
        btn_checkUnknownProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 1, 30);
            }
        });

        btn_checkTempBelts = (Button) findViewById(R.id.set_checkTempBelts);
        btn_checkTempBelts.setText(String.valueOf(settingsObject.getCheckTempBelts()));
        btn_checkTempBelts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 1, 30);
            }
        });

        btn_tempBeltsExp = (Button) findViewById(R.id.set_tempBeltsExp);
        btn_tempBeltsExp.setText(String.valueOf(settingsObject.getTempBeltsExp()));
        btn_tempBeltsExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 1, 30);
            }
        });

        btn_uploadMinAgg = (Button) findViewById(R.id.set_uploadMinAgg);
        btn_uploadMinAgg.setText(String.valueOf(settingsObject.getUploadMinAgg()));
        btn_uploadMinAgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 3, 60);
            }
        });

        btn_maxUploadData = (Button) findViewById(R.id.set_maxUploadData);
        btn_maxUploadData.setText(String.valueOf(settingsObject.getMaxUploadData()));
        btn_maxUploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 100, 1500);
            }
        });

        btn_uploadLogs = (Button) findViewById(R.id.set_uploadLogs);
        btn_uploadLogs.setText(String.valueOf(settingsObject.getUploadLogs()));
        btn_uploadLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 3, 60);
            }
        });

        btn_distSensibility = (Button) findViewById(R.id.set_distSensibility);
        btn_distSensibility.setText(String.valueOf(settingsObject.getDistSensibility()));
        btn_distSensibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, -300, 50);
            }
        });

        btn_workSumPeriod = (Button) findViewById(R.id.set_workSumPeriod);
        btn_workSumPeriod.setText(String.valueOf(settingsObject.getWorkSumPeriod()));
        btn_workSumPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 1, 20);
            }
        });;

        btn_workSumDuration = (Button) findViewById(R.id.set_workSumDuration);
        btn_workSumDuration.setText(String.valueOf(settingsObject.getWorkSumDuration()));
        btn_workSumDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 10, 60);
            }
        });

        btn_zoneColors = (Button) findViewById(R.id.set_zoneColors);
        btn_zoneColors.setText(String.valueOf(settingsObject.getZoneColors()));
        btn_zoneColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 0, 5);
            }
        });

        btn_cardDistribution = (Button) findViewById(R.id.set_cardDistribution);
        btn_cardDistribution.setText(settingsObject.getCardDistribution());
        btn_cardDistribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 0, 1);
            }
        });

        btn_timeDiff = (Button) findViewById(R.id.set_timeDiff);
        btn_timeDiff.setText(String.valueOf(settingsObject.getTimeDiff()));
        btn_timeDiff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(view, 0, 23);
            }
        });

        sw_lockAppForDevice = (Switch) findViewById(R.id.set_lockAppForDevice);
        sw_lockAppForDevice.setChecked(settingsObject.isLockAppForDevice());

        btn_updateVersion = (ImageView) findViewById(R.id.updVerFromSettingsButton);
        btn_updateVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Update version
                UpdateVersionTask updateVersionService = new UpdateVersionTask();
                updateVersionService.execute();

            }
        });
        btn_updateVersion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    btn_updateVersion.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this, R.color.userBlue));
                } else {
                    btn_updateVersion.setBackground(null);
                }
            }
        });
        if(StaticVariables.availableVersion!=null
                && StaticVariables.availableVersion.getVersionNo()!=null
                && !StaticVariables.availableVersion.getVersionNo().equals(AppDefaults.CURRENT_VERSION)){
            btn_updateVersion.setVisibility(View.VISIBLE);
        } else {
            btn_updateVersion.setVisibility(View.GONE);
        }

    }

    public void showPicker(View view, final int minVal, final int maxVal)
    {

        final Button button = (Button) view;
        final String property = button.getTag().toString();

        final NumberPicker picker = new NumberPicker(SettingsActivity.this);
        picker.setWrapSelectorWheel(true);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setFocusable(true);
        if(property.equals("set_cardDistribution")){
            picker.setMinValue(minVal);
            picker.setMaxValue(maxVal);
            picker.setDisplayedValues(new String[]{"4|9|16|20", "4|9|12|16|20"});
            picker.setValue(0);
        } else if(property.equals("set_hubMacAddress")){
            List<String> hubList = Settings.hubList;
            picker.setMinValue(0);
            if(hubList!=null && hubList.size()>0) {
                picker.setMaxValue(hubList.size()-1);
                picker.setDisplayedValues(hubList.toArray(new String[0]));
                String currentValue = button.getText().toString();
                for(int i=0; i<hubList.size(); i++){
                    if(hubList.get(i).equalsIgnoreCase(currentValue)){
                        picker.setValue(i);
                        break;
                    }
                }
            } else {
                picker.setMaxValue(0);
                picker.setValue(0);
            }

        } else if(property.equals("set_distSensibility")){
            picker.setMinValue(0);
            picker.setMaxValue(maxVal - minVal);
            picker.setValue(Integer.valueOf(button.getText().toString())-minVal);
            picker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int index) {
                    return Integer.toString(index + minVal);
                }
            });
        } else if(property.equals("set_maxUploadData")){
            final int step = 100;
            String[] valueSet = new String[maxVal/minVal];
            for (int i = minVal; i <= maxVal; i += step) {
                valueSet[(i/step)-1] = String.valueOf(i);
            }
            picker.setDisplayedValues(valueSet);
            picker.setMinValue(0);
            picker.setMaxValue(maxVal/minVal-1);
            picker.setValue(Arrays.asList(valueSet).indexOf(button.getText().toString()));
        } else {
            picker.setMinValue(minVal);
            picker.setMaxValue(maxVal);
            picker.setValue(Integer.valueOf(button.getText().toString()));
        }

        final FrameLayout layout = new FrameLayout(SettingsActivity.this);
        layout.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        new AlertDialog.Builder(SettingsActivity.this)
                .setView(layout)
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(property.equals("set_cardDistribution")
                                || property.equals("set_maxUploadData")
                                || property.equals("set_hubMacAddress")){
                            int position = picker.getValue();
                            button.setText(String.valueOf(picker.getDisplayedValues()[position]));
                        } else if(property.equals("set_distSensibility")){
                            button.setText(String.valueOf(picker.getValue()+minVal));
                        } else {
                            button.setText(String.valueOf(picker.getValue()));
                        }
                        updateSettings(property);
                    }
                })
                .setNegativeButton("Vazgeç", null)
                .show();


    }

    public void showEditText(View view){

        final Button button = (Button) view;
        final String property = button.getTag().toString();

        final EditText siteIdText = new EditText(SettingsActivity.this);
        siteIdText.setInputType(InputType.TYPE_CLASS_NUMBER);
        siteIdText.setText(button.getText());
        siteIdText.setSelection(siteIdText.getText().length());


        final FrameLayout layout = new FrameLayout(SettingsActivity.this);
        layout.addView(siteIdText, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        final AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this)
                .setNegativeButton("Vazgeç", null)
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        button.setText(siteIdText.getText());
                        updateSettings(property);
                    }
                })
                .setView(layout)
                .show();

        siteIdText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return false;
                if(keyCode == KeyEvent.KEYCODE_ENTER ){

                    button.setText(siteIdText.getText());
                    updateSettings(property);
                    dialog.dismiss();

                    return true;
                }
                return false;
            }
        });
    }

    private void updateSettings(final String property){

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    SettingsData appSettingsData = realm.where(SettingsData.class).findFirst();

                    switch (property) {
                        case ("set_simPersonCnt"):
                            appSettingsData.setSimulationPersonCount(Integer.valueOf(btn_simulationPersonCount.getText().toString()));
                            break;
                        case ("set_checkProfileUpd"):
                            appSettingsData.setCheckProfileUpdates(Integer.valueOf(btn_checkProfileUpdates.getText().toString()));
                            break;
                        case ("set_checkUnkProfile"):
                            appSettingsData.setCheckUnknownProfile(Integer.valueOf(btn_checkUnknownProfile.getText().toString()));
                            break;
                        case ("set_checkTempBelts"):
                            appSettingsData.setCheckTempBelts(Integer.valueOf(btn_checkTempBelts.getText().toString()));
                            break;
                        case ("set_tempBeltsExp"):
                            appSettingsData.setTempBeltsExp(Integer.valueOf(btn_tempBeltsExp.getText().toString()));
                            break;
                        case ("set_uploadMinAgg"):
                            appSettingsData.setUploadMinAgg(Integer.valueOf(btn_uploadMinAgg.getText().toString()));
                            break;
                        case ("set_maxUploadData"):
                            appSettingsData.setMaxUploadData(Integer.valueOf(btn_maxUploadData.getText().toString()));
                            break;
                        case ("set_uploadLogs"):
                            appSettingsData.setUploadLogs(Integer.valueOf(btn_uploadLogs.getText().toString()));
                            break;
                        case ("set_distSensibility"):
                            appSettingsData.setDistSensibility(Integer.valueOf(btn_distSensibility.getText().toString()));
                            break;
                        case ("set_workSumPeriod"):
                            appSettingsData.setWorkSumPeriod(Integer.valueOf(btn_workSumPeriod.getText().toString()));
                            break;
                        case ("set_workSumDuration"):
                            appSettingsData.setWorkSumDuration(Integer.valueOf(btn_workSumDuration.getText().toString()));
                            break;
                        case ("set_zoneColors"):
                            appSettingsData.setZoneColors(Integer.valueOf(btn_zoneColors.getText().toString()));
                            break;
                        case ("set_cardDistribution"):
                            appSettingsData.setCardDistribution(btn_cardDistribution.getText().toString());
                            break;
                        case ("set_timeDiff"):
                            appSettingsData.setTimeDiff(Integer.valueOf(btn_timeDiff.getText().toString()));
                            break;
                        case ("set_siteId"):
                            appSettingsData.setSiteId(Integer.valueOf(btn_siteId.getText().toString()));
                            break;
                    }

                    Settings.fromRealmDB(appSettingsData);

                    // Save to file
                    FileUtil.saveSettingsToFile(SettingsActivity.this, Settings.toJSONString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(mRealm!=null) {
                mRealm.close();
            }
        }

    }


    public void uploadTestJSONAsync(View view){

        String encryptedString = AppUtils.getEncryptedString(APIRequestType.UPLOAD_MINAGG);
        Log.i("Encrypted SessionId", encryptedString);

        String url = "insertActivityData.ashx";

        String jsonString = "[{\"avgSpeed\":0,\"hrZone\":2,\"avgHr\":115,\"hrDataCount\":83,\"beltId\":\"00024\"," +
                "\"siteId\":123,\"maxHr\":180,\"avgPerf\":64,\"insertSourceId\":\"COLL123001\",\"cadence\":0," +
                "\"insertSourceType\":\"FcCollector\",\"distance\":0,\"scDataCount\":0,\"userEmail\":\"*****@gmail.com\"," +
                "\"pace\":0,\"calBurned\":9.997394837476099,\"avgHrRSSI\":0,\"avgScRSSI\":0,\"fctTimestamp\":\"2017.02.08 23:22\"," +
                "\"activity\":\"Kardiyo\"},{\"avgSpeed\":0,\"hrZone\":2,\"avgHr\":121,\"hrDataCount\":115,\"beltId\":\"00024\"," +
                "\"siteId\":123,\"maxHr\":178,\"avgPerf\":64,\"insertSourceId\":\"COLL123001\",\"cadence\":0," +
                "\"insertSourceType\":\"FcCollector\",\"distance\":0,\"scDataCount\":0,\"userEmail\":\"*****@gmail.com\"," +
                "\"pace\":0,\"calBurned\":10.05599904397705,\"avgHrRSSI\":0,\"avgScRSSI\":0,\"fctTimestamp\":\"2017.02.08 23:23\"," +
                "\"activity\":\"Kardiyo\"},{\"avgSpeed\":0,\"hrZone\":2,\"avgHr\":119,\"hrDataCount\":102,\"beltId\":\"00024\"," +
                "\"siteId\":123,\"maxHr\":180,\"avgPerf\":64,\"insertSourceId\":\"COLL123001\",\"cadence\":0,\"insertSourceType\":\"FcCollector\"," +
                "\"distance\":0,\"scDataCount\":0,\"userEmail\":\"*****@gmail.com\",\"pace\":0,\"calBurned\":10.45592734225622," +
                "\"avgHrRSSI\":0,\"avgScRSSI\":0,\"fctTimestamp\":\"2017.02.08 23:24\",\"activity\":\"Kardiyo\"},{\"avgSpeed\":0," +
                "\"hrZone\":3,\"avgHr\":124,\"hrDataCount\":300,\"beltId\":\"00024\",\"siteId\":123,\"maxHr\":180,\"avgPerf\":69," +
                "\"insertSourceId\":\"COLL123001\",\"cadence\":0,\"insertSourceType\":\"FcCollector\",\"distance\":0," +
                "\"scDataCount\":0,\"userEmail\":\"*****@gmail.com\",\"pace\":0,\"calBurned\":11.49703632887189," +
                "\"avgHrRSSI\":0,\"avgScRSSI\":0,\"fctTimestamp\":\"2017.02.08 23:25\",\"activity\":\"Kardiyo\"}]";

        RequestParams params = new RequestParams();
        params.put("email", "*****@gmail.com");
        params.put("sessionid", encryptedString);
        params.put("activityData", jsonString);


        APIRestClient.post(url, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.i("UploadAsync", "UploadAsync Successful");
                Log.i("UploadAsync", "UploadAsync Response ---> " + response);

                Toast.makeText(SettingsActivity.this, "UploadAsync Successful", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {

                // Do something with the response
                Log.i("UploadAsync", "UploadAsync Successful (Array Response)");
                Log.i("UploadAsync", "UploadAsync Response Array ---> " + responseArray.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                Log.i("UploadAsync", "UploadAsync Failure");
                Log.i("UploadAsync", "UploadAsync Response ---> " + throwable.getMessage());

                Toast.makeText(SettingsActivity.this, "UploadAsync Failed: " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                Log.i("UploadAsync", "UploadAsync Failure (Array)");
                Log.i("UploadAsync", "UploadAsync Response ---> " + throwable.getMessage());

                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Log.i("UploadAsync", "UploadAsync Failure (String)");
                Log.i("UploadAsync", "UploadAsync Response ---> " + throwable.getMessage());

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }

    private class UploadJSONAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            String encryptedString = AppUtils.getEncryptedString(APIRequestType.UPLOAD_MINAGG);
            Log.i("Encrypted SessionId", encryptedString);

            String url = "insertActivityData.ashx";

            String jsonString = "[{\"avgSpeed\":0,\"hrZone\":2,\"avgHr\":115,\"hrDataCount\":83,\"beltId\":\"00024\"," +
                    "\"siteId\":123,\"maxHr\":180,\"avgPerf\":64,\"insertSourceId\":\"COLL123001\",\"cadence\":0," +
                    "\"insertSourceType\":\"FcCollector\",\"distance\":0,\"scDataCount\":0,\"userEmail\":\"*****@gmail.com\"," +
                    "\"pace\":0,\"calBurned\":9.997394837476099,\"avgHrRSSI\":0,\"avgScRSSI\":0,\"fctTimestamp\":\"2017.02.08 23:22\"," +
                    "\"activity\":\"Kardiyo\"},{\"avgSpeed\":0,\"hrZone\":2,\"avgHr\":121,\"hrDataCount\":115,\"beltId\":\"00024\"," +
                    "\"siteId\":123,\"maxHr\":178,\"avgPerf\":64,\"insertSourceId\":\"COLL123001\",\"cadence\":0," +
                    "\"insertSourceType\":\"FcCollector\",\"distance\":0,\"scDataCount\":0,\"userEmail\":\"*****@gmail.com\"," +
                    "\"pace\":0,\"calBurned\":10.05599904397705,\"avgHrRSSI\":0,\"avgScRSSI\":0,\"fctTimestamp\":\"2017.02.08 23:23\"," +
                    "\"activity\":\"Kardiyo\"},{\"avgSpeed\":0,\"hrZone\":2,\"avgHr\":119,\"hrDataCount\":102,\"beltId\":\"00024\"," +
                    "\"siteId\":123,\"maxHr\":180,\"avgPerf\":64,\"insertSourceId\":\"COLL123001\",\"cadence\":0,\"insertSourceType\":\"FcCollector\"," +
                    "\"distance\":0,\"scDataCount\":0,\"userEmail\":\"*****@gmail.com\",\"pace\":0,\"calBurned\":10.45592734225622," +
                    "\"avgHrRSSI\":0,\"avgScRSSI\":0,\"fctTimestamp\":\"2017.02.08 23:24\",\"activity\":\"Kardiyo\"},{\"avgSpeed\":0," +
                    "\"hrZone\":3,\"avgHr\":124,\"hrDataCount\":300,\"beltId\":\"00024\",\"siteId\":123,\"maxHr\":180,\"avgPerf\":69," +
                    "\"insertSourceId\":\"COLL123001\",\"cadence\":0,\"insertSourceType\":\"FcCollector\",\"distance\":0," +
                    "\"scDataCount\":0,\"userEmail\":\"*****@gmail.com\",\"pace\":0,\"calBurned\":11.49703632887189," +
                    "\"avgHrRSSI\":0,\"avgScRSSI\":0,\"fctTimestamp\":\"2017.02.08 23:25\",\"activity\":\"Kardiyo\"}]";

            RequestParams params = new RequestParams();
            params.put("email", "*****@gmail.com");
            params.put("sessionid", encryptedString);
            params.put("activityData", jsonString);


            APIRestClient.post(url, params, new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray

                    Log.i("UploadAsync", "UploadAsync Successful");
                    Log.i("UploadAsync", "UploadAsync Response ---> " + response);

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {

                    // Do something with the response
                    Log.i("UploadAsync", "UploadAsync Successful (Array Response)");
                    Log.i("UploadAsync", "UploadAsync Response Array ---> " + responseArray.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                    Log.i("UploadAsync", "UploadAsync Failure");
                    Log.i("UploadAsync", "UploadAsync Response ---> " + errorResponse.toString());

                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                    Log.i("UploadAsync", "UploadAsync Failure (Array)");
                    Log.i("UploadAsync", "UploadAsync Response ---> " + errorResponse.toString());

                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });

            return null;

        }
    }

    public void testUdp(View view){

        Intent i = new Intent(this, LoggingUDPActivity.class);
        startActivity(i);

    }

    public void viewLog(View view){

        Intent i = new Intent(this, DisplayLogActivity.class);
        startActivity(i);

    }

    public void sendTestMail(View view){

        try {

            MailData mailData = new MailData();
            mailData.prepareDefault();
            mailData.setEmailSubject("FitConnect Daily Report - " + Settings.locationId + " | " + Settings.siteId);
            mailData.setEmailBody("");

            new MailTask(SettingsActivity.this).execute(mailData);

        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }

    }

    public void clearLogs(View view){

        final RealmConfiguration logConfig = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name(AppDefaults.REALMLOG_FILENAME)
                .build();

        Realm logRealm = null;

        try {

            logRealm = Realm.getInstance(logConfig);

            logRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    realm.where(LogData.class)
                            .findAll()
                            .deleteAllFromRealm();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(logRealm!=null) {
                logRealm.close();
            }
        }

    }

    public class MailTask extends AsyncTask {

        private ProgressDialog statusDialog;
        private Activity sendMailActivity;

        public MailTask(Activity activity) {
            sendMailActivity = activity;

        }

        protected void onPreExecute() {
            statusDialog = new ProgressDialog(sendMailActivity);
            statusDialog.setMessage("Getting ready...");
            statusDialog.setIndeterminate(false);
            statusDialog.setCancelable(false);
            statusDialog.show();
        }

        @Override
        protected Object doInBackground(Object... args) {
            try {

                MailData mailData = (MailData) args[0];

                Log.i("SendMailTask", "About to instantiate GMail...");
                publishProgress("Processing input....");
                GmailSender androidEmail = new GmailSender(mailData);
                publishProgress("Preparing mail message....");
                androidEmail.createEmailMessage();
                publishProgress("Sending email....");
                androidEmail.sendEmail();
                publishProgress("Email Sent.");
                Log.i("SendMailTask", "Mail Sent.");
            } catch (Exception e) {
                publishProgress(e.getMessage());
                Log.e("SendMailTask", e.getMessage(), e);
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Object... values) {
            statusDialog.setMessage(values[0].toString());
        }

        @Override
        public void onPostExecute(Object result) {
            statusDialog.dismiss();
        }

    }

    public void testHubConnection(View view){

        if(StaticVariables.cntHubOn > 0){
            Toast.makeText(SettingsActivity.this, "Hub is available.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(SettingsActivity.this, "Hub is not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SettingsActivity.this, FitWorkActivity.class);
        startActivity(intent);

        super.onBackPressed();
    }

    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

    private class UpdateVersionTask extends AsyncTask<Void, Void, String> {

        private final String clsName = "UpdateVersionService";

        @Override
        protected void onPreExecute() {

            ProgressDialogHelper.create(SettingsActivity.this, getString(R.string.text_waiting_update));

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... aVoid) {

            String folderName = "FitConnectApks";
            String urlStr = "";
            if(StaticVariables.availableVersion!=null
                    && !"".equals(StaticVariables.availableVersion.getVersionUrl())){
                urlStr = StaticVariables.availableVersion.getVersionUrl();
            } else {
                return null;
            }


            File dir = new File(Environment.getExternalStorageDirectory(),
                    folderName);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String path = Environment.getExternalStorageDirectory() + "/FitConnectApks/FitConnect.apk";
            try {
                URL url = new URL(urlStr);
                URLConnection connection = url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(path);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("YourApp", "Well that didn't work out so well...");
                Log.e("YourApp", e.getMessage());
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                return null;
            }
            return path;
        }

        // begin the installation by opening the resulting file
        @Override
        protected void onPostExecute(String path) {

            ProgressDialogHelper.closeDialog();

            if (path != null) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                Log.d("Lofting", "About to install new .apk");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } else {
                Toast.makeText(SettingsActivity.this, getString(R.string.text_error_update), Toast.LENGTH_LONG).show();
            }
        }

    }


}
