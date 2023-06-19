package com.applissima.fitconnectdemo;
/*
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

*//**
 * Created by ilkerkuscan on 20/03/17.
 *//*

public class UpdateVersionService extends AsyncTask<Map<String, String>, Void, String> {

    private final String clsName = "UpdateVersionService";

    private Context mContext;

    public UpdateVersionService(Context context){
        this.mContext = context;
    }


    @Override
    protected String doInBackground(Map<String, String>... versionMap) {

        String folderName = "FitConnectApks";

        String versionNo = versionMap[0].get("versionNo");
        String urlStr = versionMap[0].get("versionUrl");

        File dir = new File(Environment.getExternalStorageDirectory(),
                folderName);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String path = Environment.getExternalStorageDirectory() + "/FitConnectApks/FitConnect_v1.apk";
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
                //publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            output.flush();
            output.closeDialog();
            input.closeDialog();
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

        if(path!=null) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            Log.d("Lofting", "About to install new .apk");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(i);
        }
    }


}*/
