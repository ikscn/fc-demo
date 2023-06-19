package com.applissima.fitconnectdemo;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class LoggingUDPActivity extends AppCompatActivity {

    private EditText logText;
    private boolean bKeepRunning = true;
    public static String SERVERIP;
    public static final int SERVERPORT = 1199;
    private DatagramSocket socket;
    private int currHeartRate = 0;

    private List<String> waitingList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging_udp);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        logText = (EditText) findViewById(R.id.logText);
        logText.append("\n");

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );


        SERVERIP = StaticVariables.machineIP;

       /* WifiManager wifiMan = (WifiManager) LoggingUDPActivity.this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        SERVERIP = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));*/

        logText.append("\nServer IP: " + SERVERIP);
        logText.append("\nServer Port: " + SERVERPORT);
        initUDP();


    }

    @Override
    protected void onPause() {
        bKeepRunning = false;
        super.onPause();

    }

    @Override
    protected void onResume() {
        bKeepRunning = true;
        super.onResume();
    }

    private void initUDP(){

        new Thread(new Server()).start();
        /* GIve the Server some time for startup */
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    new Server();
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();*/

        // Kickoff the Client
        //new Thread(new Client()).start();

    }

    public class Server implements Runnable {

        //public static final String SERVERIP = "127.0.0.1"; // 'Within' the emulator!
        //public static final String SERVERIP = "127.0.0.1";
        //public static final int SERVERPORT = 1199;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
            /* Retrieve the ServerName */
                InetAddress serverAddr = InetAddress.getByName(SERVERIP);

                Log.d("UDP", "S: Connecting...");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logText.append("\nUDP -- S: Connecting...");
                    }
                });

            /* Create new UDP-Socket */
                socket = new DatagramSocket(SERVERPORT, serverAddr);

            /* By magic we know, how much data will be waiting for us */

                Log.d("UDP", "S: Receiving...");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logText.append("\nUDP -- S: Receiving...");
                    }
                });


            /* Receive the UDP-Packet */
                while (bKeepRunning) {

                    byte[] buf = new byte[101];
            /* Prepare a UDP-Packet that can
             * contain the data we want to receive */
                    final DatagramPacket packet = new DatagramPacket(buf, buf.length);

                    socket.receive(packet);


                    RawData rawData = byteToRawData(packet.getData());


                    //Log.d("UDP", "S: Received: '" + new String(packet.getData()) + "'");
                    Log.d("UDP", "S: Done.");


                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            logText.append("\nUDP -- S: Received: New Data '" *//*+ new String(packet.getData())*//* + "'");
                            logText.append("\nUDP -- S: Done.");
                        }
                    });*/
                }

                /*InetAddress clientAddr = packet.getAddress();
                int clientPort = packet.getPort();
                String s = "Thanks";
                byte[] bufBack = new byte[15000];
                bufBack = s.getBytes();
                final DatagramPacket packetBack = new DatagramPacket(bufBack, bufBack.length, clientAddr, clientPort);

                Log.d("UDP", "S: Sending: '" + new String(buf) + "'");
                final byte[] finalBufBack = bufBack;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logText.append("\nUDP -- S: Sending: '" + new String(finalBufBack) + "'");
                    }
                });
                socket.send(packetBack);*/


            } catch (final Exception e) {
                Log.e("UDP", "S: Error", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logText.append("\nUDP -- S: Error: " + "\n" + e.getCause());
                        logText.append("\nUDP -- S: " + e.getMessage());
                    }
                });
            }
        }

    }

    private RawData byteToRawData(final byte[] data){


        try {

            int hrValue = new BigInteger(toFourBytes(Arrays.copyOfRange(data, 10, 11))).intValue();
            int deviceNo = new BigInteger(toFourBytes(Arrays.copyOfRange(data, 12, 14))).intValue();
            int deviceType = new BigInteger(toFourBytes(Arrays.copyOfRange(data, 14, 15))).intValue();
            int rssiValue = new BigInteger(Arrays.copyOfRange(data, 17, 18)).shortValue();


            final RawData rawData = new RawData();
            rawData.setDeviceNo(String.format("%05d", deviceNo));
            rawData.setDeviceType(deviceType);
            rawData.setHeartRate(hrValue);
            rawData.setRssi(rssiValue);


                Log.i("RAW DATA Created", "(deviceNo: " + rawData.getDeviceNo() +
                        ", deviceType: " + rawData.getDeviceType() +
                        ", heartRate: " + rawData.getHeartRate() +
                        ", rssi: " + rawData.getRssi() + ")");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        logText.append("\n(RAW DATA deviceNo: " + rawData.getDeviceNo() +
                                ", deviceType: " + rawData.getDeviceType() +
                                ", heartRate: " + rawData.getHeartRate() +
                                ", rssi: " + rawData.getRssi() + ")");


                    }
                });

            return rawData;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    /*public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length*2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }*/


    @Override
    public void onBackPressed() {
        bKeepRunning = false;
        super.onBackPressed();
    }

    /*private void processRawData(RawData rawData){

        Realm realm = Realm.getDefaultInstance();

        if(rawData.getDeviceNo()!=null && !waitingList.contains(rawData.getDeviceNo())){

            int currentPerf = 0;
            int currentZone = 0;

            FitWorkPerson fitWorkPerson = realm.where(FitWorkPerson.class).equalTo("actHrSensorId", rawData.getDeviceNo()).findFirst();

            if(fitWorkPerson!=null){
                // if the person is already in activity, insert new data to this activityPerson

                int heartRate = rawData.getHeartRate();

                currentPerf = calcPerf(heartRate, fitWorkPerson.getFitPerson());
                currentZone = calcZone(heartRate, fitWorkPerson.getFitPerson());

                FitWorkDataMinDetail dataMinDetail = new FitWorkDataMinDetail();
                //dataMinDetail.setFitWork(fitWork);
                dataMinDetail.setFitPerson(fitWorkPerson.getFitPerson());
                dataMinDetail.setInsertDate(new Date());
                dataMinDetail.setCurrentHr(heartRate);
                dataMinDetail.setCurrentPerf(currentPerf);
                dataMinDetail.setCurrentZone(currentZone);
                dataMinDetail.setCurrentCadence(0);
                dataMinDetail.setCurrentSpeed(0);
                dataMinDetail.setCurrentRssi(rawData.getRssi());

                addFitWorkDataMinDetail(dataMinDetail);

            } else {
                // if the person is not in activity, add it to FitWorkPerson

                FitPerson fitPerson = realm.where(FitPerson.class)
                        .equalTo("hrSensorId", rawData.getDeviceNo())
                        .findFirst();

                // Search Person in local, if found, add person to the activity
                if(fitPerson!=null){

                    final Date lastUpdateDate = fitPerson.getLastUpdateDate();
                    //Check Person data, if old, update it due to settings
                    final Calendar checkTime = Calendar.getInstance();
                    checkTime.add(Calendar.MINUTE, (-1 * Settings.checkProfileUpdates));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            logText.append("\n lastUpdateDate: " + lastUpdateDate.toString());
                            logText.append("\n checkTime: " + checkTime.toString());

                            if(lastUpdateDate.before(checkTime.getTime())){
                                logText.append("\n Profile Update is older than " + Settings.checkProfileUpdates + " minutes.");
                            }


                        }
                    });


                    if(fitPerson.getLastUpdateDate()!=null && fitPerson.getLastUpdateDate().before(checkTime.getTime())){



                    }

                }

            }

        } else {

        }

    }

    private void addFitWorkDataMinDetail(FitWorkDataMinDetail dataMinDetail){

        Realm realm = Realm.getDefaultInstance();

        try {

            FitWorkPerson fitWorkPerson = realm.where(FitWorkPerson.class)
                    .equalTo("fitPerson.userName", dataMinDetail.getFitPerson().getUserName())
                    .equalTo("fitWork.activityName", dataMinDetail.getFitWork().getActivityName())
                    .findFirst();

            if(fitWorkPerson!=null) {
                realm.beginTransaction();
                fitWorkPerson.setCurrentHr(dataMinDetail.getCurrentHr());
                fitWorkPerson.setCurrentPerf(dataMinDetail.getCurrentPerf());
                fitWorkPerson.setCurrentZone(dataMinDetail.getCurrentZone());
                fitWorkPerson.setCurrentSpeed(dataMinDetail.getCurrentSpeed());
                fitWorkPerson.setCurrentCadence(dataMinDetail.getCurrentCadence());
                fitWorkPerson.setCurrentRssi(dataMinDetail.getCurrentRssi());
                fitWorkPerson.setHrLastDataUpdateDate(new Date());
                realm.commitTransaction();
            }

        } catch (Exception e){
            e.printStackTrace();
        }


    }

    private int calcZone(int heartRate, FitPerson person){

        int zone1 = (int) ((double) person.getMaxHr() * 0.5);
        int zone2 = (int) ((double) person.getMaxHr() * 0.6);
        int zone3 = (int) ((double) person.getMaxHr() * 0.7);
        int zone4 = (int) ((double) person.getMaxHr() * 0.8);
        int zone5 = (int) ((double) person.getMaxHr() * 0.9);

        if(heartRate < zone2){
            return 1;
        } else if(heartRate >= zone2 && heartRate < zone3){
            return 2;
        } else if(heartRate >= zone3 && heartRate < zone4){
            return 3;
        } else if(heartRate >= zone4 && heartRate < zone5){
            return 4;
        } else if(heartRate >= zone5){
            return 5;
        } else {
            return 0;
        }

    }

    private int calcPerf(int heartRate, FitPerson person){
        return (int) Math.round((double)heartRate / (double) person.getMaxHr() * 100);
    }*/

    private byte[] toFourBytes(byte[] bytes){

        int j = bytes.length-1;
        byte[] fourBytes = new byte[4];
        for(int i=0; i<=3; i++){
            if(i < 4-bytes.length){
                fourBytes[i] = 0;
            }else {
                fourBytes[i] = bytes[j];
                j--;
            }

        }
        return fourBytes;

    }


    /*public class Client implements Runnable {

        @Override
        public void run() {
            try {
                // Retrieve the ServerName
                InetAddress serverAddr = InetAddress.getByName(SERVERIP);

                Log.d("UDP", "C: Connecting...");
            *//* Create new UDP-Socket *//*
                DatagramSocket socket = new DatagramSocket();

            *//* Prepare some data to be sent. *//*
                byte[] buf = ("Hello from Client").getBytes();

            *//* Create UDP-packet with
             * data & destination(url+port) *//*
                DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, SERVERPORT);
                Log.d("UDP", "C: Sending: '" + new String(buf) + "'");

            *//* Send out the packet *//*
                socket.send(packet);
                Log.d("UDP", "C: Sent.");
                Log.d("UDP", "C: Done.");

                socket.receive(packet);
                Log.d("UDP", "C: Received: '" + new String(packet.getData()) + "'");

            } catch (Exception e) {
                Log.e("UDP", "C: Error", e);
            }
        }

    }*/


    /*private class UDPReceiver extends Thread {
        private boolean bKeepRunning = true;
        private String lastMessage = "";
        DatagramSocket socket;

        public void run() {
            String message;
            byte[] lmessage = new byte[15000];
            DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);

            try {
                socket = new DatagramSocket(SERVERPORT);

                while(bKeepRunning) {
                    socket.receive(packet);
                    message = new String(lmessage, 0, packet.getLength());
                    lastMessage = message;
                    //runOnUiThread(updateTextMessage);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (socket != null) {
                socket.closeDialog();
            }
        }

        public void kill() {
            bKeepRunning = false;
        }

        public String getLastMessage() {
            return lastMessage;
        }
    }*/
}
