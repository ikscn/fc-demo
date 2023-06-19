package com.applissima.fitconnectdemo;
/*
import android.util.Log;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

*//**
 * Created by ilkerkuscan on 22/02/17.
 *//*

public class UDPServiceOld {

    private static final String clsName = "UDPService";
    private static boolean bKeepRunning = true;
    private static String SERVERIP;
    private static final int SERVERPORT = 1199;
    private static DatagramSocket socket;
    //private DatabaseService dbService;
    //private List<String> waitingList;
    //public static Map<String, List<RawData>> dataMap;
    //private DatabaseService dbServiceUdp;

    public static Thread udpBgThread;
    public static Thread minuteThread;
    //public Thread testDataThread;

    public UDPService(){

        //dbService = databaseService;

        SERVERIP = AppUtils.getMachineIP();

        initUDP();
    }

    public static void init(){

        SERVERIP = AppUtils.getMachineIP();

        StaticVariables.init();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    //Thread.sleep(ThreadController.tInitialDelay * 1000);
                    Thread.sleep(3000);

                    initUDP();

                } catch (InterruptedException e) {
                    LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                            + ":" + e.getStackTrace()[0].getLineNumber());
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void setDbService(DatabaseService dbService){
        this.dbServiceUdp = dbService;
    }




    public static void initUDP(){

        try {

            setTimerForUdpBgThread();
            setTimerForMinute();

            new Thread(new Server()).start();

             GIve the Server some time for startup *//**//*
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static class Server implements Runnable {

        //public static final String SERVERIP = "127.0.0.1"; // 'Within' the emulator!
        //public static final String SERVERIP = "127.0.0.1";
        //public static final int SERVERPORT = 1199;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                // Retrieves the server name
                InetAddress serverAddr = InetAddress.getByName(SERVERIP);

                Log.d("UDP", "S: Connecting...");

                // Creates a new UDP-Socket
                socket = new DatagramSocket(null);

                SocketAddress socketAddress = new InetSocketAddress(serverAddr, SERVERPORT);
                socket.setReuseAddress(true);
                socket.setBroadcast(true);
                socket.bind(socketAddress);

                Log.d("UDP", "S: Receiving...  by IP/Port: " + SERVERIP + ":" + SERVERPORT);

                // Receives the UDP-Packet
                while (bKeepRunning) {

                    // Generated Test Data
                    for(int i=0; i<10; i++){
                        processRawDataTest(generateRandomRawData());
                    }
                    if(testData.getHeartRate()!=0 && !inProgress) {
                        processRawDataTest(testData);
                        Log.d("UDP", "S: Received Non-Zero Data.");
                        RawDataPersonTask task = new RawDataPersonTask();
                        task.execute(testData);
                        //processRawDataTest(rawData);
                    }


                    byte[] buf = new byte[50];

                    final DatagramPacket packet = new DatagramPacket(buf, buf.length);

                    socket.receive(packet);

                    //Log.d("UDP", "S: Received Some Data.");

                    RawData rawData = byteToRawData(packet.getData());

                    if(rawData!=null && rawData.getHeartRate()!=0) {

                        //Log.d("UDP", "S: Received Non-Zero Data.");
                        //processRawData(rawData);
                        dbService.processRawData(rawData, false);

                    }

                    
                    //Log.i("UDP RawData", "S: rawDataListSize: " + rawDataList.size());
                    if(!isBeingCreated) {
                        isBeingCreated = true;
                        processRawDataTest(rawData);
                    }

                    //Log.d("UDP", "S: Done.");

                }



            } catch (final Exception e) {
                LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                        + ":" + e.getStackTrace()[0].getLineNumber());
                //Log.e("UDP", "S: Error", e);
            } finally {
                socket.closeDialog();
            }
        }

    }

    public static void closeSocket(){
        if(socket!=null && !socket.isClosed()) {
            socket.closeDialog();
            Log.i("UDP", "UDP Hub Socket Closed.");
        } else {
            Log.i("UDP", "UDP Hub Socket is already closed.");
        }
    }

    public void setTimerForGenTestData(){


        testDataThread = new Thread() {

            @Override
            public void run() {
                try {

                    // calls every minute and refreshes UDP data
                    while (!isInterrupted()) {
                        Thread.sleep(2 * 1000);

                        generateRandomDataForSimUsers();

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        testDataThread.start();

    }

    public static void setTimerForUdpBgThread(){

        udpBgThread = new Thread() {

            @Override
            public void run() {
                try {

                    int timer = 0;

                    // calls every minute and refreshes UDP data
                    while (!isInterrupted()) {
                        Thread.sleep(1000);

                        if(timer > 0 && timer%ThreadController.tMinute==0){
                            dbService.addDataToMinAgg();
                            dbService.removeExpiredPersonsFromActivity();
                            //refreshData();
                        }
                        if(timer > 0 && timer%ThreadController.tCheckUnknownProfile()==0){
                            refreshWaitingList();
                        }
                        if(timer > 0
                                && timer%ThreadController.tGenDataForSimUsers==0
                                && Settings.simulationPersonCount > 0
                                //&& timer<20
                            ){
                            generateRandomDataForSimUsers();
                        }

                        timer++;
                        if(timer==20000){
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

        udpBgThread.start();

    }

    private static void refreshWaitingList(){

        StaticVariables.waitingList = new ArrayList<String>();

    }

    public static void setTimerForMinute(){

        minuteThread = new Thread() {

            @Override
            public void run() {
                try {

                    // calls every minute and refreshes data
                    while (!isInterrupted()) {
                        Thread.sleep(60 * 1000);

                        DatabaseService.addDataToMinAgg();
                        DatabaseService.removeExpiredPersonsFromActivity();

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        minuteThread.start();

    }



    private void refreshData(){

        List<String> curUDPList = StaticVariables.getCurUDPList();
        Map<String, List<RawData>> curDataMap = StaticVariables.dataMap;

        //dbServiceUdp.addDataToMinAgg(curDataMap, curUDPList);
        addDataToMinAgg(curDataMap, curUDPList);

        String logText = "";
        for(String hrSensorId :curUDPList){
            RawData data = StaticVariables.getLastRawData(hrSensorId);
            if(data!=null) {
                logText += hrSensorId + "-> HR:" + String.valueOf(data.getHeartRate()) + "\n";
            }
        }

        if(!logText.equalsIgnoreCase("")) {
            Log.i("DataMap", "Data Map: " + logText);
        }

        StaticVariables.dataMap = new HashMap<String, List<RawData>>();

    }

    public void addDataToMinAgg(final Map<String, List<RawData>> curDataMap, final List<String> curUDPList){

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // TODO: 27/02/17 FitWork'ler ayrılacak
                    FitWork fitWork = realm.where(FitWork.class).findFirst();

                    for (String hrSensorId : curUDPList) {

                        FitPerson fitPerson = realm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).findFirst();

                        if (fitPerson != null) {

                            int maxHr = getMaxHr(curDataMap.get(hrSensorId));
                            int avgHr = calcAvgHr(curDataMap.get(hrSensorId));
                            int avgPerf = calcPerf(avgHr, fitPerson);
                            int avgZone = calcZone(avgHr, fitPerson);
                            double calBurned = calcCalBurned(avgHr, fitPerson);

                            //Log.i("Yakilan Kalori: ", "Yakilan Kalori: " + calBurned);

                            FitWorkPerson fitWorkPerson = realm.where(FitWorkPerson.class).equalTo("actHrSensorId", hrSensorId).findFirst();
                            if (fitWorkPerson != null) {
                                fitWorkPerson.setAvgHr(avgHr);
                                fitWorkPerson.setMaxHr(maxHr);
                                fitWorkPerson.setAvgPerf(avgPerf);
                                fitWorkPerson.setTotalCal(fitWorkPerson.getTotalCal() + calBurned);
                            }

                            FitWorkDataMinAgg minAgg = realm.createObject(FitWorkDataMinAgg.class);
                            minAgg.setFitWork(fitWork);
                            minAgg.setFitPerson(fitPerson);
                            minAgg.setHrDataCount(curDataMap.get(hrSensorId).size());
                            minAgg.setMaxHr(maxHr);
                            minAgg.setAvgHr(avgHr);
                            minAgg.setAvgPerf(avgPerf);
                            minAgg.setAvgZone(avgZone);
                            minAgg.setTotalCal(calBurned);
                            minAgg.setInsertDate(Calendar.getInstance().getTime());

                        }

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
        } finally {
            mRealm.closeDialog();
        }


    }

    private void addDataToMinAgg(final Map<String, List<RawData>> curDataMap, final List<String> curUDPList){

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // TODO: 27/02/17 FitWork'ler ayrılacak
                    FitWork fitWork = realm.where(FitWork.class).findFirst();

                    for (String hrSensorId : curUDPList) {

                        FitPerson fitPerson = realm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).findFirst();

                        if (fitPerson != null) {

                            int maxHr = getMaxHr(curDataMap.get(hrSensorId));
                            int avgHr = calcAvgHr(curDataMap.get(hrSensorId));
                            int avgPerf = calcPerf(avgHr, fitPerson);
                            int avgZone = calcZone(avgHr, fitPerson);
                            double calBurned = calcCalBurned(avgHr, fitPerson);

                            FitWorkPerson fitWorkPerson = realm.where(FitWorkPerson.class).equalTo("actHrSensorId", hrSensorId).findFirst();
                            if (fitWorkPerson != null) {
                                fitWorkPerson.setAvgHr(avgHr);
                                fitWorkPerson.setMaxHr(maxHr);
                                fitWorkPerson.setAvgPerf(avgPerf);
                                fitWorkPerson.setTotalCal(fitWorkPerson.getTotalCal() + calBurned);
                            }

                            FitWorkDataMinAgg minAgg = realm.createObject(FitWorkDataMinAgg.class);
                            minAgg.setFitWork(fitWork);
                            minAgg.setFitPerson(fitPerson);
                            minAgg.setHrDataCount(curDataMap.get(hrSensorId).size());
                            minAgg.setMaxHr(maxHr);
                            minAgg.setAvgHr(avgHr);
                            minAgg.setAvgPerf(avgPerf);
                            minAgg.setAvgZone(avgZone);
                            minAgg.setTotalCal(calBurned);
                            minAgg.setInsertDate(Calendar.getInstance().getTime());

                        }

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getLocalizedMessage());
        } finally {
            if(mRealm!=null) {
                mRealm.closeDialog();
            }
        }


    }


    private int calcAvgHr(List<RawData> rawDataList){

        if(rawDataList==null || rawDataList.size() == 0){
            return 0;
        }

        int hrSum = 0;
        int avgHr = 0;
        for(RawData rawData :rawDataList){
            hrSum += rawData.getHeartRate();
        }

        avgHr = hrSum / rawDataList.size();

        return avgHr;
    }

    private int getMaxHr(List<RawData> rawDataList){
        int maxHr = 0;
        for(RawData rawData :rawDataList){
            if(rawData.getHeartRate()>maxHr){
                maxHr = rawData.getHeartRate();
            }
        }
        return maxHr;
    }

    private double calcCalBurned(int calcAvgHr, FitPerson person){

        double calBurned = 0.0;
        int gender = person.getGender();
        int age = person.getAge();
        int weight = person.getWeight();
        if(gender==1){ // male
            calBurned = (age * 0.2017 + weight * 0.1988 + calcAvgHr * 0.6309 - 55.0969) * (1/4.184);
        } else if(gender == 2) { // female
            calBurned = (age * 0.074 + weight * 0.1263 + calcAvgHr * 0.4472 - 20.4022) * (1/4.184);
        }

        if(calBurned>0){
            return calBurned;
        } else {
            return 0;
        }
    }

    private static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private static RawData byteToRawData(byte[] data){


        try {

            int hrValue = new BigInteger(toFourBytes(Arrays.copyOfRange(data, 10, 11))).intValue();
            int deviceNo = new BigInteger(toFourBytes(Arrays.copyOfRange(data, 12, 14))).intValue();
            int deviceType = new BigInteger(toFourBytes(Arrays.copyOfRange(data, 14, 15))).intValue();
            int rssiValue = new BigInteger(Arrays.copyOfRange(data, 17, 18)).shortValue();
            String hexString = bytesToHex(data);

            //Log.d("UDP", "S: Received: '" + hexString + "'");


            RawData rawData = new RawData();
            rawData.setDeviceNo(String.format("%05d", deviceNo));
            rawData.setDeviceType(deviceType);
            rawData.setHeartRate(hrValue);
            rawData.setRssi(rssiValue);
            rawData.setHexString(hexString);


            if(hrValue!=0) {
                Log.i("RAW DATA Created", "(deviceNo: " + rawData.getDeviceNo() +
                        ", deviceType: " + rawData.getDeviceType() +
                        ", heartRate: " + rawData.getHeartRate() +
                        ", rssi: " + rawData.getRssi() + ")");
            }


            return rawData;


        } catch (Exception e) {
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        }

        return null;

    }

    private static byte[] toFourBytes(byte[] bytes){

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

    public boolean isPersonInWaitingList(String hrSensorId){

        return waitingList.contains(hrSensorId);
    }

    public void addPersonToWaitingList(String hrSensorId){

        waitingList.add(hrSensorId);

    }

    private class RawDataPersonTask extends AsyncTask<RawData, Void, Void>{

        String hrSensorId = "";
        RawData rawData = null;

        @Override
        protected Void doInBackground(RawData... rawDatas) {

            rawData = rawDatas[0];
            hrSensorId = rawData.getDeviceNo();

            Realm realm = Realm.getDefaultInstance();

            // Check if person exist in Local
            FitPerson person = realm.where(FitPerson.class).equalTo("hrSensorId", rawData.getDeviceNo()).findFirst();
            if(person==null){

                Log.i("Udp Person", "Udp Person is null. Now creating in Local DB." + AppUtils.getDateString(Calendar.getInstance().getTime()));

                APIServices apiServices = new APIServices(mContext);
                apiServices.updateOrAddLocalUserData(rawData.getDeviceNo());

            } else {

                long workCount = realm.where(FitWorkPerson.class).equalTo("actHrSensorId", rawData.getDeviceNo()).count();
                if(workCount==0){
                    try {

                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                Log.i("FitWorkPerson Adding", "FitWorkPerson is adding...");

                                FitWorkPerson fitWorkPerson = realm.createObject(FitWorkPerson.class);
                                fitWorkPerson.setFitPerson(realm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).findFirst());
                                fitWorkPerson.setAddedDate(Calendar.getInstance().getTime());
                                fitWorkPerson.setCurrentHr(0);
                                fitWorkPerson.setCurrentPerf(0);
                                fitWorkPerson.setCurrentZone(0);
                                fitWorkPerson.setCurrentRssi(0);
                                fitWorkPerson.setTotalCal(100);
                                fitWorkPerson.setActHrSensorId(hrSensorId);
                                fitWorkPerson.setHrLastDataUpdateDate(Calendar.getInstance().getTime());
                                Log.i("FitWorkPerson ADDED", "FitWorkPerson ADDED!!");
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            Log.i("Udp Data Changed", "Udp Data Changed.");


            //FitPerson udpPerson = realm.where(FitPerson.class).equalTo("hrSensorId", rawData.getDeviceNo()).findFirst();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("Process RawData", "RawData Processed Successfully!!");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progList.remove(hrSensorId);
            //addPersonToActivity(hrSensorId, rawData);
            super.onPostExecute(aVoid);
        }
    }

    private void addPersonToActivity(final String hrSensorId, final RawData rawData){

        Realm realm = Realm.getDefaultInstance();

        realm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).count()==0){
            realm = Realm.getDefaultInstance();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Log.i("FitWorkPerson Adding", "FitWorkPerson is adding...");

                FitWorkPerson fitWorkPerson = realm.where(FitWorkPerson.class).equalTo("actHrSensorId", hrSensorId).findFirst();

                if (fitWorkPerson == null) {
                    fitWorkPerson = realm.createObject(FitWorkPerson.class);
                    fitWorkPerson.setFitPerson(realm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).findFirst());
                    fitWorkPerson.setAddedDate(Calendar.getInstance().getTime());
                    fitWorkPerson.setCurrentHr(rawData.getHeartRate());
                    fitWorkPerson.setCurrentPerf(calcPerf(rawData.getHeartRate(), fitWorkPerson.getFitPerson()));
                    fitWorkPerson.setCurrentZone(calcZone(rawData.getHeartRate(), fitWorkPerson.getFitPerson()));
                    fitWorkPerson.setCurrentRssi(rawData.getRssi());
                    fitWorkPerson.setTotalCal(100);
                    fitWorkPerson.setActHrSensorId(hrSensorId);
                    fitWorkPerson.setHrLastDataUpdateDate(Calendar.getInstance().getTime());
                    Log.i("FitWorkPerson ADDED", "FitWorkPerson ADDED!!");
                } else {
                    fitWorkPerson.setCurrentHr(rawData.getHeartRate());
                    fitWorkPerson.setCurrentPerf(calcPerf(rawData.getHeartRate(), fitWorkPerson.getFitPerson()));
                    fitWorkPerson.setCurrentZone(calcZone(rawData.getHeartRate(), fitWorkPerson.getFitPerson()));
                    fitWorkPerson.setCurrentRssi(rawData.getRssi());
                    fitWorkPerson.setTotalCal(fitWorkPerson.getTotalCal() + 1);
                    fitWorkPerson.setActHrSensorId(hrSensorId);
                    fitWorkPerson.setHrLastDataUpdateDate(Calendar.getInstance().getTime());
                    Log.i("FitWorkPerson UPDATED", "FitWorkPerson UPDATED!!");
                }


            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

                Log.i("FitWorkPerson ADDED", "FitWorkPerson ADDED Successfully!!");

                inProgress = false;

            }
        });
    }

    public RawData generateRandomRawData(){

        int heartRate = 86; // between 60 & 160
        String deviceNo = "00080";
        int deviceType = 120;
        int rssi = -71; // between -50 & 50

        RawData data = new RawData(heartRate, deviceNo, deviceType, rssi);

        return data;
    }

    private static void generateRandomDataForSimUsers(){

        if(Settings.simulationPersonCount == 0){
            return;
        }

        try {

            for(int i=0; i<Settings.simulationPersonCount; i++){
                String beltId = SimulationService.simulationBelts[i];
                RawData rawData = generateRandomRawData(beltId);
                DatabaseService.processRawData(rawData, true);
            }

        } catch (Exception e) {
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        }
    }

    private static RawData generateRandomRawData(String deviceNo){

        Random rand = new Random();
        int heartRate = rand.nextInt(81) + 80; // between 80 & 160
        int deviceType = 1;
        int rssi = rand.nextInt(100) - 50; // between -50 & 50

        RawData data = new RawData(heartRate, deviceNo, deviceType, rssi);

        return data;
    }

    public static void interruptUDPThreads(){
        if(udpBgThread!=null) {
            udpBgThread.interrupt();
        }
        if(minuteThread!=null){
            minuteThread.interrupt();
        }
        closeSocket();
        //testDataThread.interrupt();
    }

    private void processRawData(RawData rawData){

        if(StaticVariables.isPersonInWaitingList(rawData.getDeviceNo())){
            Log.i("InWaitingList", "This belt is in waitinglist: " + rawData.getDeviceNo());
            return;
        }

        String hrSensorId = rawData.getDeviceNo();

        if(StaticVariables.dataMap.containsKey(hrSensorId)){
            StaticVariables.dataMap.get(hrSensorId).add(rawData);
        } else {
            List<RawData> rawDataList = new ArrayList<RawData>();
            rawDataList.add(rawData);
            StaticVariables.dataMap.put(hrSensorId, rawDataList);
        }

    }



    private void processRawDataTest(RawData rawData){

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
                dataMinDetail.setInsertDate(Calendar.getInstance().getTime());
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

                    Log.i("Profile Update","CurrentTime: " + AppUtils.getDateString(Calendar.getInstance().getTime()));
                    Log.i("Profile Update","CheckTime: " + AppUtils.getDateString(checkTime.getTime()));

                    if(fitPerson.getLastUpdateDate()!=null && fitPerson.getLastUpdateDate().before(checkTime.getTime())){
                        Log.i("Profile Update","Profile Update is older than " + Settings.checkProfileUpdates + " minutes.");


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
    }

    class LooperThread extends Thread {
        public Handler mHandler;

        @Override
        public void run() {
            Looper.prepare();

            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // process incoming messages here
                }
            };

            Looper.loop();
        }
    }

    public List<String> getSimUsers(){

        final List<String> simUsers = new ArrayList<String>();

        Realm mRealm = Realm.getDefaultInstance();

        try {

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmResults<FitPerson> realmResults = realm.where(FitPerson.class)
                            .beginsWith("hrSensorId", "1")
                            .findAll();

                    for(FitPerson fitPerson :realmResults){
                        simUsers.add(fitPerson.getHrSensorId());
                    }

                }
            });

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            mRealm.closeDialog();
        }

        return simUsers;
    }

}*/
