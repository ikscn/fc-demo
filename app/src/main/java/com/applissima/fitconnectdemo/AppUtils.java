package com.applissima.fitconnectdemo;

import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ilkerkuscan on 17/02/17.
 */

public class AppUtils {

    private static final String clsName = "AppUtils";
    
    public static void checkNetworks(WeakReference<Context> weakContext){

        checkWifiStatus(weakContext);
        checkInternetStatus();

    }


    public static void checkWifiStatus(final WeakReference<Context> weakContext) {

        try {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Context mContext = weakContext.get();

                    if(mContext!=null) {

                        WifiManager wifiMgr = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

                            if (wifiMgr.getConnectionInfo().getNetworkId() == -1) {
                                StaticVariables.addWifiStatus(false);
                            } else {
                                StaticVariables.addWifiStatus(true);
                            }
                        } else {
                            StaticVariables.addWifiStatus(false);
                        }

                    }

                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isInternetConnected() {

        Process ipProcess = null;

        try {

            Runtime runtime = Runtime.getRuntime();
            ipProcess = runtime.exec("/system/bin/ping -c 1 google.com");
            int exitValue = ipProcess.waitFor();
            ipProcess.destroy();
            return (exitValue == 0);

        } catch (IOException e) {
            Log.e("ERROR", "IOException",e);
        }
        catch (InterruptedException e) {
            Log.e("ERROR", "InterruptedException",e);
        } finally {
            if(ipProcess!=null){
                ipProcess.destroy();
            }
        }

        return false;
    }

    public static void checkInternetStatus() {

        try {

            Process ipProcess = null;

            Runtime runtime = Runtime.getRuntime();
            try {
                ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                int     exitValue = ipProcess.waitFor();
                if(exitValue == 0){
                    StaticVariables.addInternetStatus(true);
                    ipProcess.destroy();
                    //StaticVariables.cntInternetOn ++;
                }
                return;
            }
            catch (IOException e) {
                e.printStackTrace();

            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            } finally {
                if(ipProcess!=null) {
                    ipProcess.destroy();
                }
            }

            StaticVariables.addInternetStatus(false);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String writeToFile(String currentStacktrace, String errorType) {

        String filename = "";

        try {

            //Gets the Android external storage directory & Create new folder Crash_Reports
            File dir = new File(Environment.getExternalStorageDirectory(),
                    AppDefaults.CRASH_REPORTS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            SimpleDateFormat df = new SimpleDateFormat(AppDefaults.TIME_FORMAT_FILENAME, Locale.getDefault());
            filename = errorType + "_" + df.format(Calendar.getInstance().getTime()) + ".STACKTRACE";

            // Write the file into the folder
            File reportFile = new File(dir, filename);
            FileWriter fileWriter = new FileWriter(reportFile);
            fileWriter.append(currentStacktrace);
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception e) {
            Log.e("ExceptionHandler", e.getMessage());
        }

        return filename;

    }

    public static void clearExpiredFiles(String fileDir, int numOfDaysBack) {

        try {

            File directory = new File(Environment.getExternalStorageDirectory(), fileDir);
            if(directory.exists()){

                File[] listFiles = directory.listFiles();
                Calendar tenDaysAgo = Calendar.getInstance();
                tenDaysAgo.set(Calendar.HOUR_OF_DAY, 0);
                tenDaysAgo.set(Calendar.MINUTE, 0);
                tenDaysAgo.set(Calendar.SECOND, 0);
                tenDaysAgo.add(Calendar.DAY_OF_YEAR, -numOfDaysBack);
                for(File listFile : listFiles) {
                    Calendar lastModified = Calendar.getInstance();
                    lastModified.setTimeInMillis(listFile.lastModified());
                    if(tenDaysAgo.after(lastModified)) {
                        if(!listFile.delete()) {
                            Log.e("FileProc", "Unable to delete file: " + listFile);
                            LoggerService.insertLog(clsName, "Unable to delete file: " + listFile, null);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }
    }

    public static List<File> getTodaysCrashReports(){

        List<File> todaysFiles = new ArrayList<File>();

        try {

            File directory = new File(Environment.getExternalStorageDirectory(), AppDefaults.CRASH_REPORTS_DIR);
            if(directory.exists()){
                File[] listFiles = directory.listFiles();
                for(File listFile : listFiles) {
                    Calendar lastModified = Calendar.getInstance();
                    lastModified.setTimeInMillis(listFile.lastModified());
                    if(isSameDay(Calendar.getInstance(), lastModified)) {
                        todaysFiles.add(listFile);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoggerService.insertLog(clsName, e.getMessage(), e.getStackTrace()[0].getClassName()
                    + ":" + e.getStackTrace()[0].getLineNumber());
        }

        return todaysFiles;

    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
                .get(Calendar.DAY_OF_YEAR) == cal2
                .get(Calendar.DAY_OF_YEAR));
    }

    public static String getEncryptedString(APIRequestType type){

        String encryptedString = "";
        String requestString = "";
        String requestKey = "";

        switch (type){
            case DOWNLOAD_PROFILE: requestKey = AppDefaults.API_RETRIEVE_KEY;
                break;
            case UPLOAD_MINAGG: requestKey = AppDefaults.API_INSERTDATA_KEY;
                break;
            case UPLOAD_LOG: requestKey = AppDefaults.API_INSERTLOGDATA_KEY;
                break;
        }

        SimpleDateFormat df = new SimpleDateFormat(AppDefaults.TIME_FORMAT_DEFAULT, Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        Log.i("UTCTime", df.format(calendar.getTime()));

        requestString = AppDefaults.SCRT_KEY + df.format(calendar.getTime()).substring(0,15)
                + requestKey + AppDefaults.API_PASS;

        encryptedString = md5(requestString);
        Log.i("Encrypted SessionId", encryptedString);

        return encryptedString;

    }

    public static String getDateString(Date date, String dateFormat){

        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(date);

    }

    public static String getUploadDateString(Date date){

        SimpleDateFormat df = new SimpleDateFormat(AppDefaults.TIME_FORMAT_FCT, Locale.getDefault());
        return df.format(date);

    }

    public static String getUploadLogDateString(Date date){

        SimpleDateFormat df = new SimpleDateFormat(AppDefaults.TIME_FORMAT_UPLOADLOGS, Locale.getDefault());
        return df.format(date);

    }

    public static String getTimerText(int second, int minute, int hour){

        String secText;
        String minText;
        String hrsText;

        if(second<=9){
            secText = "0" + String.valueOf(second);
        } else {
            secText = String.valueOf(second);
        }
        if(minute<=9){
            minText = "0" + String.valueOf(minute);
        } else {
            minText = String.valueOf(minute);
        }
        if(hour<=9){
            hrsText = "0" + String.valueOf(hour);
        } else {
            hrsText = String.valueOf(hour);
        }

        return hrsText + ":" + minText + ":" + secText;

    }


    public static String md5(final String password) {
        try {

            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getEndOfDayTimeDiff(){

        Calendar now = Calendar.getInstance();
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.set(Calendar.HOUR_OF_DAY, Integer.valueOf(AppDefaults.STR_END_OF_DAY.split(":")[0]));
        endOfDay.set(Calendar.MINUTE, Integer.valueOf(AppDefaults.STR_END_OF_DAY.split(":")[1]));
        endOfDay.set(Calendar.SECOND, 0);

        long diff = endOfDay.getTimeInMillis() - now.getTimeInMillis();
        if(diff<5000){
            endOfDay.add(Calendar.DAY_OF_YEAR, 1);
            diff = endOfDay.getTimeInMillis() - now.getTimeInMillis();
        }

        return diff;

    }

    public static long getRestartTimeDiff(){

        long midNightDiff;
        long morningDiff;

        Calendar now = Calendar.getInstance();
        Calendar midNight = Calendar.getInstance();
        midNight.set(Calendar.HOUR_OF_DAY, Integer.valueOf(AppDefaults.STR_MIDNIGHT.split(":")[0]));
        midNight.set(Calendar.MINUTE, Integer.valueOf(AppDefaults.STR_MIDNIGHT.split(":")[1]));
        midNight.set(Calendar.SECOND, 0);

        Calendar morning = Calendar.getInstance();
        morning.set(Calendar.HOUR_OF_DAY, Integer.valueOf(AppDefaults.STR_MORNING.split(":")[0]));
        morning.set(Calendar.MINUTE, Integer.valueOf(AppDefaults.STR_MORNING.split(":")[1]));
        morning.set(Calendar.SECOND, 0);

        midNightDiff = midNight.getTimeInMillis() - now.getTimeInMillis();
        if(midNightDiff<5000){
            midNight.add(Calendar.DAY_OF_YEAR, 1);
            midNightDiff = midNight.getTimeInMillis() - now.getTimeInMillis();
        }

        morningDiff = morning.getTimeInMillis() - now.getTimeInMillis();
        if(morningDiff<5000){
            morning.add(Calendar.DAY_OF_YEAR, 1);
            morningDiff = morning.getTimeInMillis() - now.getTimeInMillis();
        }

        return Math.min(midNightDiff, morningDiff);

    }

    public static long getNextQuarterDiff(){

        Calendar now = Calendar.getInstance();
        int currMin = now.get(Calendar.MINUTE);
        int diff = 15 - (currMin % 15);

        Calendar nextQuarter = Calendar.getInstance();
        nextQuarter.set(Calendar.SECOND, 0);
        nextQuarter.add(Calendar.MINUTE, diff);

        return nextQuarter.getTimeInMillis() - now.getTimeInMillis();
    }

    public static boolean isServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("SERVICE_INFO", serviceClass.getSimpleName() + " is already running.");
                return true;
            }
        }
        Log.i("SERVICE_INFO", serviceClass.getSimpleName() + " is not running.");
        return false;
    }

    public static String readableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String getMachineIP(WeakReference<Context> weakContext){

        String ipStr = "";

        try {

            Context mContext = weakContext.get();
            if(mContext!=null) {

                WifiManager wifiMan = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInf = wifiMan.getConnectionInfo();
                int ipAddress = wifiInf.getIpAddress();
                ipStr = String.format(Locale.getDefault(), "%d.%d.%d.%d", (ipAddress & 0xff),
                        (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

            } else {
                ipStr = "0.0.0.0";
            }
        } catch (Exception e) {
            Log.i("MachineIP", "MachineIP UDPService: Error getting machine IP");
            e.printStackTrace();
        }

        return ipStr;
    }

    public static String getMinAggId(Date insertDate, String userName){

        return getDateString(insertDate, AppDefaults.TIME_FORMAT_MINAGGID) + userName;

    }

}
