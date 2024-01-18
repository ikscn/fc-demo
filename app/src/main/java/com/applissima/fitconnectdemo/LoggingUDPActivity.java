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

    }

    public class Server implements Runnable {

        @Override
        public void run() {
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
                    Log.d("UDP", "S: Done.");
                }
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

    @Override
    public void onBackPressed() {
        bKeepRunning = false;
        super.onBackPressed();
    }

    private byte[] toFourBytes(byte[] bytes){

        int j = bytes.length-1;
        byte[] fourBytes = new byte[4];
        for(int i=0; i<=3; i++){
            if(i < 4-bytes.length){
                fourBytes[i] = 0;
            } else {
                fourBytes[i] = bytes[j];
                j--;
            }

        }
        return fourBytes;
    }
    
}
