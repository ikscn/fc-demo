package com.applissima.fitconnectdemo;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ilkerkuscan on 15/03/17.
 */

public class InternetCheck extends AsyncTask<Void, Void, Void> {


    private Activity mActivity;
    private InternetCheckListener mListener;

    public InternetCheck(Activity activity){

        mActivity= activity;

    }

    @Override
    protected Void doInBackground(Void... params) {


        boolean b = hasInternetAccess();
        mListener.onComplete(b);

        return null;
    }


    public void isInternetConnectionAvailable(InternetCheckListener listener){
        mListener = listener;
        execute();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    private boolean hasInternetAccess() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "closeDialog");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("NETWORK STATUS", "NETWORK STATUS: No network available!");
        }
        return false;
    }

    public interface InternetCheckListener{
        void onComplete(boolean connected);
    }

}
