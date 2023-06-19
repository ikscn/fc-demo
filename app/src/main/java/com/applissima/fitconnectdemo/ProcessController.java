package com.applissima.fitconnectdemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilkerkuscan on 20/07/17.
 */

public class ProcessController {

    public static List<String> userUpdatingList;
    public static List<String> userAddingWorkList;

    public static void init(){

        userUpdatingList = new ArrayList<String>();
        userAddingWorkList = new ArrayList<String>();

    }

    public static void addToUserUpdatingList(String hrSensorId){
        if(!userUpdatingList.contains(hrSensorId)){
            userUpdatingList.add(hrSensorId);
        }
    }

    public static void removeFromUserUpdatingList(String hrSensorId){
        if(userUpdatingList.contains(hrSensorId)){
            userUpdatingList.remove(hrSensorId);
        }
    }

    public static void addToUserAddingWorkList(String hrSensorId){
        if(!userAddingWorkList.contains(hrSensorId)){
            userAddingWorkList.add(hrSensorId);
        }
    }

    public static void removeFromUserAddingWorkList(String hrSensorId){
        if(userAddingWorkList.contains(hrSensorId)){
            userAddingWorkList.remove(hrSensorId);
        }
    }

}
