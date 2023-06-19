package com.applissima.fitconnectdemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dsi.ant.AntService;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntChannelProvider;
import com.dsi.ant.channel.AntCommandFailedException;
import com.dsi.ant.channel.IAntChannelEventHandler;
import com.dsi.ant.channel.PredefinedNetwork;
import com.dsi.ant.message.ChannelId;
import com.dsi.ant.message.ChannelType;
import com.dsi.ant.message.EventCode;
import com.dsi.ant.message.ExtendedAssignment;
import com.dsi.ant.message.LibConfig;
import com.dsi.ant.message.fromant.BroadcastDataMessage;
import com.dsi.ant.message.fromant.ChannelEventMessage;
import com.dsi.ant.message.fromant.DataMessage;
import com.dsi.ant.message.fromant.MessageFromAntType;
import com.dsi.ant.message.ipc.AntMessageParcel;

import java.util.HashMap;
import java.util.Locale;

public class AntPlusService extends Service {


    private final String clsName = "AntPlusService";

    //SPEED CALCULATION
    //Bike wheel circumference [mm]
    static final public int WHEEL_CIRCUMFERENCE = 2070;
    //Time unit factor for BSC events
    static final public int BSC_EVT_TIME_FACTOR = 1024;
    //Time unit factor for RPM unit
    static final public int BSC_RPM_TIME_FACTOR = 60;
    //Numerator of [m/s] to [kph] ratio
    static final public int BSC_MS_TO_KPH_NUM = 36;
    //Denominator of [m/s] to [kph] ratio
    static final public int BSC_MS_TO_KPH_DEN = 10;
    //Unit factor [m/s] to [mm/s]
    static final public int BSC_MM_TO_M_FACTOR = 1000;
    static final public int SPEED_COEFFICIENT = (WHEEL_CIRCUMFERENCE * BSC_EVT_TIME_FACTOR * BSC_MS_TO_KPH_NUM / BSC_MS_TO_KPH_DEN / BSC_MM_TO_M_FACTOR);

    //CADENCE CALCULATION
    static final public int  CADENCE_COEFFICIENT = (BSC_EVT_TIME_FACTOR * BSC_RPM_TIME_FACTOR);

    // Moved to AppDefaults
    /*static final public String DATA_MSG = "info.hmm.antplusreceiver.antplusservice.DATA_MSG";
    static final public String DATA_HR = "info.hmm.antplusreceiver.antplusservice.DATA_HR";
    static final public String DATA_SC = "info.hmm.antplusreceiver.antplusservice.DATA_SC";*/

    private AntService antService = null;
    private AntChannelProvider antChannelProvider = null;

    private IBinder binder = new AntLocalBinder();

    private AntChannel antPlusChannel = null;

    private boolean antRadioServiceBound = false;
    private boolean allowAddChannel = false;

    private LocalBroadcastManager broadcastManager;

    HashMap<Integer, CalculationItem> mapSpeed = new HashMap<>();
    HashMap<Integer, CalculationItem> mapCadence = new HashMap<>();

    //private DatabaseService dbService;
    //private boolean mBoundDbService;
    //private Messenger messageHandler;

    //Handle Channel Events and Messages
    private IAntChannelEventHandler antChannelEventHandler = new IAntChannelEventHandler() {
        @Override
        public void onReceiveMessage(final MessageFromAntType messageFromAntType, final AntMessageParcel antMessageParcel) {

            try{

                if(!StaticVariables.isAntOn){
                    StaticVariables.isAntOn = true;
                }

                if(messageFromAntType == MessageFromAntType.CHANNEL_EVENT)
                {
                    ChannelEventMessage msg = new ChannelEventMessage(antMessageParcel);

                    if(msg.getEventCode() == EventCode.CHANNEL_CLOSED)
                    {
                        if(antPlusChannel != null)
                            antPlusChannel.open();
                    }
                }
                else if(messageFromAntType == MessageFromAntType.ACKNOWLEDGED_DATA
                        || messageFromAntType == MessageFromAntType.BROADCAST_DATA
                        || messageFromAntType == MessageFromAntType.BURST_TRANSFER_DATA) {
                    DataMessage message = null;
                    if (messageFromAntType == MessageFromAntType.BROADCAST_DATA) {
                        message = new BroadcastDataMessage(antMessageParcel);

                        //Heart Rate
                        if(message.getExtendedData().getChannelId().getDeviceType() == 120)
                        {
                            byte[] payload = message.getPayload();
                            byte page =(byte)(payload[0] & 0x7F);
                            if(page == 0x00 || page == 0x01 || page == 0x02 || page == 0x03 || page == 0x04 || page == 0x05 || page == 0x06 || page == 0x07){

                                int hr = (payload[7] & 0xFF);
                                int rssi = message.getExtendedData().getRssi().getRssiValue();
                                int devId = message.getExtendedData().getChannelId().getDeviceNumber();

                                // FIXME Bu özellik için mesafe testi yapılmalı
                                //if(hr!=0 && rssi <= Settings.distSensibility) {

                                    final RawData rawData = new RawData();
                                    rawData.setDeviceType(message.getExtendedData().getChannelId().getDeviceType());
                                    rawData.setDeviceNo(String.format(Locale.getDefault(), "%05d", devId));
                                    rawData.setHeartRate(hr);
                                    rawData.setRssi(rssi);

                                    sendData(rawData);

                                    /*if(dbService!=null) {
                                        dbService.processRawData(rawData, false);
                                    }*/

                                //}

                        /*Toast.makeText(AntPlusService.this, "New Data: DevId: " + devId
                                + " Hr: " + hr, Toast.LENGTH_LONG).show();*/

                                //SensorListItem item = new SensorListItem(devId, ""+devId, ""+hr, "", ""+rssi, false);

                                //sendDataHr(item);
                            }
                        }
                        //Speed / Cadence
                        else if(message.getExtendedData().getChannelId().getDeviceType() == 121)
                        {
                            byte[] payload = message.getPayload();

                            int rssi = message.getExtendedData().getRssi().getRssiValue();
                            int devId = message.getExtendedData().getChannelId().getDeviceNumber();

                            double cadence = 0;
                            double speed = 0;

                            //Calculate Speed
                            if(!mapSpeed.containsKey(devId)){
                                int rev_cnt = (payload[6] & 0xFF) +((payload[7] & 0xFF) << 8);
                                int evt_time = (payload[4] & 0xFF) +((payload[5] & 0xFF) << 8);

                                CalculationItem calcItem = new CalculationItem();
                                calcItem.AccumulatedValue = 0;
                                calcItem.PreAccumulatedValue = rev_cnt;
                                calcItem.EventCount = 0;
                                calcItem.PreEventCount = evt_time;

                                mapSpeed.put(devId, calcItem);
                            }
                            else {
                                int rev_cnt = (payload[6] & 0xFF) +((payload[7] & 0xFF) << 8);
                                int evt_time = (payload[4] & 0xFF) +((payload[5] & 0xFF) << 8);

                                if(evt_time != mapSpeed.get(devId).PreEventCount) {

                                    mapSpeed.get(devId).AccumulatedValue += rev_cnt - mapSpeed.get(devId).PreValue;
                                    mapSpeed.get(devId).EventCount += evt_time - mapSpeed.get(devId).PreCount;

                                    if (mapSpeed.get(devId).PreValue > rev_cnt) {
                                        mapSpeed.get(devId).AccumulatedValue += 65536;
                                    }

                                    if (mapSpeed.get(devId).PreCount > evt_time) {
                                        mapSpeed.get(devId).EventCount += 65536;
                                    }

                                    mapSpeed.get(devId).PreValue = rev_cnt;
                                    mapSpeed.get(devId).PreCount = evt_time;

                                    speed = (SPEED_COEFFICIENT * (mapSpeed.get(devId).AccumulatedValue - mapSpeed.get(devId).PreAccumulatedValue)) / (mapSpeed.get(devId).EventCount - mapSpeed.get(devId).PreEventCount);

                                    mapSpeed.get(devId).PreAccumulatedValue = mapSpeed.get(devId).AccumulatedValue;
                                    mapSpeed.get(devId).PreEventCount = mapSpeed.get(devId).EventCount;

                                }
                            }

                            //Calculate Cadence
                            if(!mapCadence.containsKey(devId)){
                                int rev_cnt = (payload[6] & 0xFF) +((payload[7] & 0xFF) << 8);
                                int evt_time = (payload[4] & 0xFF) +((payload[5] & 0xFF) << 8);

                                CalculationItem calcItem = new CalculationItem();
                                calcItem.AccumulatedValue = 0;
                                calcItem.PreAccumulatedValue = rev_cnt;
                                calcItem.EventCount = 0;
                                calcItem.PreEventCount = evt_time;

                                mapCadence.put(devId, calcItem);
                            }
                            else {
                                int rev_cnt = (payload[2] & 0xFF) +((payload[3] & 0xFF) << 8);
                                int evt_time = (payload[0] & 0xFF) +((payload[1] & 0xFF) << 8);

                                if(evt_time != mapCadence.get(devId).PreEventCount) {

                                    mapCadence.get(devId).AccumulatedValue += rev_cnt - mapCadence.get(devId).PreValue;
                                    mapCadence.get(devId).EventCount += evt_time - mapCadence.get(devId).PreCount;

                                    if (mapCadence.get(devId).PreValue > rev_cnt) {
                                        mapCadence.get(devId).AccumulatedValue += 65536;
                                    }

                                    if (mapCadence.get(devId).PreCount > evt_time) {
                                        mapCadence.get(devId).EventCount += 65536;
                                    }

                                    mapCadence.get(devId).PreValue = rev_cnt;
                                    mapCadence.get(devId).PreCount = evt_time;

                                    cadence = (CADENCE_COEFFICIENT * (mapCadence.get(devId).AccumulatedValue - mapCadence.get(devId).PreAccumulatedValue)) / (mapCadence.get(devId).EventCount - mapCadence.get(devId).PreEventCount);

                                    mapCadence.get(devId).PreAccumulatedValue = mapCadence.get(devId).AccumulatedValue;
                                    mapCadence.get(devId).PreEventCount = mapCadence.get(devId).EventCount;


                                }
                            }


                            if(speed > 0 && cadence > 0) {
                                SensorListItem item = new SensorListItem(devId, "" + devId,
                                        String.format("%.2f", speed),
                                        String.format("%.2f", cadence), "" + rssi, false);
                                sendDataSc(item);
                            }
                        }
                    }




                }
            }
            catch (Exception e){
                e.printStackTrace();
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
            }
        }

        @Override
        public void onChannelDeath() {
            Log.i(clsName, "Channel Death!");
        }
    };

    private ServiceConnection antRadioServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            // Must pass in the received IBinder object to correctly construct an AntService object

            //Toast.makeText(AntPlusService.this, "AntPlusService Connected!", Toast.LENGTH_LONG).show();

            /*new Thread(new Runnable() {
                @Override
                public void run() {*/

                    antService = new AntService(service);

                    try {

                        int tryCount = 1;

                        // Getting a channel provider in order to acquire channels
                        antChannelProvider = antService.getChannelProvider();

                        // Initial check for number of channels available
                        boolean channelAvailable = antChannelProvider.getNumChannelsAvailable() > 0;
                        // Initial check for if legacy interface is in use. If the
                        // legacy interface is in use, applications can free the ANT
                        // radio by attempting to acquire a channel.
                        boolean legacyInterfaceInUse = antChannelProvider.isLegacyInterfaceInUse();

                        while(!channelAvailable && !legacyInterfaceInUse && tryCount<=5){
                            tryCount++;
                            Thread.sleep(1000);
                            if(antChannelProvider!=null) {
                                channelAvailable = antChannelProvider.getNumChannelsAvailable() > 0;
                            }
                        }


                        // If there are channels OR legacy interface in use, allow adding channels
                        if(channelAvailable || legacyInterfaceInUse) {

                            allowAddChannel = true;

                            if(null != antChannelProvider)
                            {
                                try
                                {
                                    antPlusChannel = antChannelProvider.acquireChannel(AntPlusService.this, PredefinedNetwork.ANT_PLUS);

                                    Thread.sleep(100);

                                    //Configure Adapter
                                    LibConfig config = new LibConfig();
                                    //Enable Extended Message Format (Device ID)
                                    config.setEnableChannelIdOutput(true);
                                    //Enable RSSI Output
                                    config.setEnableRssiOutput(true);
                                    antPlusChannel.setAdapterWideLibConfig(config);

                                    Thread.sleep(100);
                                    //Configure Background Channel
                                    ExtendedAssignment asign = new ExtendedAssignment();
                                    asign.enableBackgroundScanning();
                                    antPlusChannel.assign(ChannelType.SLAVE_RECEIVE_ONLY, asign);
                                    Thread.sleep(100);
                                    antPlusChannel.setRfFrequency(57);
                                    Thread.sleep(100);
                                    antPlusChannel.setPeriod(2048);//16Hz
                                    Thread.sleep(100);

                                    try{
                                        ChannelId channelId = new ChannelId(0, 0, 0);
                                        antPlusChannel.setChannelId(channelId);
                                    }
                                    catch(Exception ex){

                                    }

                                    antPlusChannel.setChannelEventHandler(antChannelEventHandler);

                                    Thread.sleep(100);

                                    //Open Background Channel
                                    antPlusChannel.open();
                                    Thread.sleep(100);

                                } catch (RemoteException e)
                                {
                                }
                            }
                        }
                        else {
                            // If no channels available AND legacy interface is not in use
                            allowAddChannel = false;
                        }



                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                                + ":" + e.getStackTrace()[0].getLineNumber());
                    }

            /*    }
            }).start();*/


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            antChannelProvider = null;
            antService = null;
            allowAddChannel = false;
            if(StaticVariables.isAntOn){
                StaticVariables.isAntOn = false;
            }
        }

    };

    private final BroadcastReceiver channelProviderStateChangedReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            Log.i(clsName, "Action: " + intent.getAction());

            if(AntChannelProvider.ACTION_CHANNEL_PROVIDER_STATE_CHANGED.equals(intent.getAction())) {
                boolean update = false;
                // Retrieving the data contained in the intent
                int numChannels = intent.getIntExtra(AntChannelProvider.NUM_CHANNELS_AVAILABLE, 0);
                boolean legacyInterfaceInUse = intent.getBooleanExtra(AntChannelProvider.LEGACY_INTERFACE_IN_USE, false);

                try {

                    if (numChannels > 0) {
                        if(!StaticVariables.isAntOn){
                            StaticVariables.isAntOn = true;
                        }
                    } else {
                        if(StaticVariables.isAntOn){
                            StaticVariables.isAntOn = false;
                        }
                    }
                    sendServiceStatusChanged();

                } catch (Exception e) {
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                    e.printStackTrace();
                }

                if(allowAddChannel) {
                    // Was a acquire channel allowed
                    // If no channels available AND legacy interface is not in use, disallow acquiring of channels
                    if(0 == numChannels && !legacyInterfaceInUse) {
                        allowAddChannel = false;
                        update = true;
                    }
                } else {
                    // Acquire channels not allowed
                    // If there are channels OR legacy interface in use, allow acquiring of channels
                    if(numChannels > 0 || legacyInterfaceInUse) {
                        allowAddChannel = true;
                        update = true;
                    }
                }
            }
        }
    };

    private void doBindAntRadioService()
    {
        try {

            // Start listing for channel available intents
            registerReceiver(channelProviderStateChangedReceiver, new IntentFilter(AntChannelProvider.ACTION_CHANNEL_PROVIDER_STATE_CHANGED));

            // Creating the intent and calling context.bindService() is handled by
            // the static bindService() method in AntService
            antRadioServiceBound = AntService.bindService(this, antRadioServiceConnection);

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }
    }

    private void doUnbindAntRadioService()
    {

        // Stop listing for channel available intents
        try{
            unregisterReceiver(channelProviderStateChangedReceiver);
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }

        try
        {
            unbindService(antRadioServiceConnection);
        }
        catch(IllegalArgumentException e)
        {
            // Not bound, that's what we want anyway
            e.printStackTrace();
        }

        antRadioServiceBound = false;

        /*if(antRadioServiceBound)
        {
            try
            {
                unbindService(antRadioServiceConnection);
            }
            catch(IllegalArgumentException e)
            {
                // Not bound, that's what we want anyway
                e.printStackTrace();
            }

            antRadioServiceBound = false;
        }*/
    }

    public AntPlusService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        //messageHandler = (Messenger) intent.getExtras().get("MESSENGER");


        return binder;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        antRadioServiceBound = false;

        broadcastManager = LocalBroadcastManager.getInstance(this);
        doBindAntRadioService();
    }

    @Override
    public void onDestroy()
    {
        try {

            doUnbindAntRadioService();

            if(antPlusChannel != null) {
                antPlusChannel.clearChannelEventHandler();
                antPlusChannel.close();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            //Toast.makeText(AntPlusService.this, "Error: " + e.getStackTrace()[0].toString(), Toast.LENGTH_LONG).show();
        } catch (AntCommandFailedException e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }


        antChannelProvider = null;

        /*try {
            if(mBoundDbService) {
                unbindService(mDbConnection);
            }
        } catch (Exception e){
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }*/

        super.onDestroy();
    }

    /*private ServiceConnection mDbConnection = new ServiceConnection() {

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
    };*/

    public void sendData(RawData rawData){
        Intent intent = new Intent(AppDefaults.DATA_HR);
        if(rawData != null)
            intent.putExtra(AppDefaults.DATA_MSG, rawData);
        broadcastManager.sendBroadcast(intent);
    }

    public void sendDataHr(SensorListItem msg) {
        Intent intent = new Intent(AppDefaults.DATA_HR);
        if(msg != null)
            intent.putExtra(AppDefaults.DATA_MSG, msg);
        broadcastManager.sendBroadcast(intent);
    }

    public void sendDataSc(SensorListItem msg) {
        Intent intent = new Intent(AppDefaults.DATA_SC);
        if(msg != null)
            intent.putExtra(AppDefaults.DATA_MSG, msg);
        broadcastManager.sendBroadcast(intent);
    }

    public void sendServiceStatusChanged(){
        Intent intent = new Intent(AppDefaults.DATA_STATUS);
        intent.putExtra(AppDefaults.DATA_MSG, true);
        broadcastManager.sendBroadcast(intent);
    }

    public class AntLocalBinder extends Binder {
        public AntPlusService getService() {
            return AntPlusService.this;
        }
    }

}
