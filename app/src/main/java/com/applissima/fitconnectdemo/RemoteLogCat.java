package com.applissima.fitconnectdemo;

/**
 * Created by ilkerkuscan on 08/09/17.
 */

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RemoteLogCat extends AsyncTask<String, String, String> {

    public RemoteLogCat(){

    }

    public RemoteLogCat(String apikey){
        this.apikey = apikey;
    }

    public String apikey = "";


    public void log(String channel,String message)
    {
        try {
            this.execute("http://www.remotelogcat.com/log.php?apikey=" + apikey +
                    "&channel=" + URLEncoder.encode(channel, "utf-8") +
                    "&log=" + URLEncoder.encode(message, "utf-8"));
        }
        catch(UnsupportedEncodingException ex){

        }
    }

    public void log(String channel,String message,String apikey)
    {
        this.apikey = apikey;
        this.log(channel, message);
    }

    @Override
    protected String doInBackground(String... uri) {
        String responseString = "";
        try {
            URL url = new URL(uri[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            responseString = new String(readFully(in), "utf-8");
        }
        catch(IOException ex)
        {
            responseString = ex.toString();
        }
        return responseString;
    }

    private byte[] readFully(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
