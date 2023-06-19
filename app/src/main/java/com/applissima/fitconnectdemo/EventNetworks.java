package com.applissima.fitconnectdemo;

/**
 * Created by ilkerkuscan on 16/05/17.
 */

public class EventNetworks {

    private boolean[] networkStatus;

    public EventNetworks(boolean[] networkStatus) {
        this.networkStatus = networkStatus;
    }

    public boolean[] getNetworkStatus(){
        return networkStatus;
    }
}