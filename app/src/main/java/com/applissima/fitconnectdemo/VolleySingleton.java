package com.applissima.fitconnectdemo;
/*
import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

*//**
 * Created by ilkerkuscan on 17/05/17.
 *//*

public class VolleySingleton {

    private static VolleySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context mContext;

    private VolleySingleton(Context context){
        mContext = context;
        requestQueue = getRequestQueue();
    }


    public RequestQueue getRequestQueue(){
        if(requestQueue==null){

            Cache cache = new DiskBasedCache(mContext.getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();

            //requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized VolleySingleton getInstance(Context context){

        if(mInstance==null){
            mInstance = new VolleySingleton(context);
        }

        return mInstance;

    }

    public<T> void addToRequestQueue(Request<T> request){
        requestQueue.add(request);
    }

}*/
