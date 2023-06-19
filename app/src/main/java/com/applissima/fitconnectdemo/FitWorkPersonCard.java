package com.applissima.fitconnectdemo;

/**
 * Created by ilkerkuscan on 11/05/17.
 */

public class FitWorkPersonCard {

    private String nickName;
    private String imageUrl;
    private int gender;
    private int currentHr;
    private int caloriesBurned;
    private int currentPerf;
    private int currentZone;

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCurrentHr() {
        return currentHr;
    }

    public void setCurrentHr(int currentHr) {
        this.currentHr = currentHr;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public int getCurrentPerf() {
        return currentPerf;
    }

    public void setCurrentPerf(int currentPerf) {
        this.currentPerf = currentPerf;
    }

    public int getCurrentZone() {
        return currentZone;
    }

    public void setCurrentZone(int currentZone) {
        this.currentZone = currentZone;
    }
}
