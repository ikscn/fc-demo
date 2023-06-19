package com.applissima.fitconnectdemo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ilkerkuscan on 02/02/17.
 */

public class FileUtil {

    private static String fileName = "fitConnectSettingsFile.json";
    private static String folderName = "FitConnectSettings";

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static void saveSettingsToFile(Context context, String mJsonResponse) {

        try {

            File dir = new File(Environment.getExternalStorageDirectory(),
                    folderName);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File reportFile = new File(dir, fileName);
            FileWriter fileWriter = new FileWriter(reportFile);
            fileWriter.write(mJsonResponse);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            Log.e("FileUtil Error", "Error in Writing: " + e.getLocalizedMessage());
        }
    }

    public static String getSettingsFromFile(Context context) {

        try {
            File f = new File(Environment.getExternalStorageDirectory() + "/" + folderName + "/" + fileName);
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e("FileUtil Error", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }


}
