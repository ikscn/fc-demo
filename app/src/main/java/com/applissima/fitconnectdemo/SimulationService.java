package com.applissima.fitconnectdemo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by ilkerkuscan on 29/03/17.
 */

public class SimulationService {

    public static String[] simulationBelts = new String[]
            {
                    //"10024",
                    "10025",
                    "10030",
                    "10031",
                    //"10032",
                    "10033",
                    "10034",
                    "10035",
                    "10036",
                    "10037",
                    "10038",
                    "10039",
                    "10040",
                    "10041",
                    "10042",
                    "10043",
                    "10050",
                    "10051",
                    "10053",
                    "10054",
                    "10055",
                    "11024",
                    "11124"
            };


    /*public static void generateSimulationFitPersons(int personCount, DatabaseService dbService){

        Realm mRealm = null;

        try {

            mRealm = Realm.getDefaultInstance();

            for (int i = 0; i < personCount; i++) {

                String hrSensorId = simulationBelts[i];

                if (mRealm.where(FitPerson.class).equalTo("hrSensorId", hrSensorId).count() > 0) {
                    continue;
                }

                dbService.updateOrAddLocalUserData(hrSensorId, true);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(mRealm!=null) {
                mRealm.closeDialog();
            }
        }

    }*/

    /*public static void generateSimulationFwPersons(int personCount, DatabaseService databaseService){

        for(int i=0; i<personCount; i++){
            databaseService.createInitWork(simulationBelts[i]);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }*/
}
