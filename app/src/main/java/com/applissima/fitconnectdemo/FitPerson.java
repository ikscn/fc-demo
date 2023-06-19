package com.applissima.fitconnectdemo;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ilkerkuscan on 28/01/17.
 *
 * ios: Person
 *
 */

public class FitPerson extends RealmObject {

    @PrimaryKey
    private String personId;
    private String userName;
    private String fullName;
    private String photoURL;
    private byte[] photo;
    private String nickName;
    private String birthDate;
    private String hrSensorId = "";
    private String hrTempSensorId = "";
    private String locationId;
    private int age = 0;
    private int gender = 0;
    private int height = 0;
    private int weight = 0;
    private int maxHr = 0;
    private int restHr = 0;
    private boolean enableBelt = false;
    private boolean enableAutoAdjust = false;
    private boolean testUser = false;
    private Date lastUpdateDate;

    public void fromJSON(JSONObject object){

        JSONObject userDetails = null;
        try {
            userDetails = object.getJSONObject("userDetails");
            if(userDetails!=null){
                this.setHrSensorId(userDetails.getString("beltID"));
                this.setFullName(userDetails.getString("fullName"));
                this.setNickName(userDetails.getString("nickname"));
                this.setBirthDate(userDetails.getString("dateOfBirth"));
                this.setAge(userDetails.getInt("age"));
                this.setGender(userDetails.getInt("gender"));
                this.setPhotoURL(userDetails.getString("url"));
                this.setHeight(userDetails.getInt("height"));
                this.setWeight(userDetails.getInt("weight"));
                this.setMaxHr(userDetails.getInt("maxHR"));
                this.setRestHr(userDetails.getInt("restHR"));
                this.setEnableBelt(userDetails.getBoolean("enableBelt"));
                this.setEnableAutoAdjust(userDetails.getBoolean("enableAutoAdjust"));
                this.setLocationId(userDetails.getString("location"));
                this.setUserName(userDetails.getString("userName"));
                this.setLastUpdateDate(Calendar.getInstance().getTime());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fromMember(Member member){
        this.setHrTempSensorId(member.getTempBeltId());
        this.setFullName(member.getFullName());
        this.setNickName(member.getNickName());
        this.setBirthDate(member.getBirthDate());
        this.setAge(member.getAge());
        this.setGender(member.getGender());
        this.setPhotoURL(member.getImageUrl());
        this.setHeight(member.getHeight());
        this.setWeight(member.getWeight());
        this.setMaxHr(member.getMaxHr());
        this.setRestHr(member.getRestHr());
        this.setUserName(member.getEmail());
        this.setLastUpdateDate(Calendar.getInstance().getTime());
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getHrSensorId() {
        return hrSensorId;
    }

    public void setHrSensorId(String hrSensorId) {
        this.hrSensorId = hrSensorId;
    }

    public String getHrTempSensorId() {
        return hrTempSensorId;
    }

    public void setHrTempSensorId(String hrTempSensorId) {
        this.hrTempSensorId = hrTempSensorId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getMaxHr() {
        return maxHr;
    }

    public void setMaxHr(int maxHr) {
        this.maxHr = maxHr;
    }

    public int getRestHr() {
        return restHr;
    }

    public void setRestHr(int restHr) {
        this.restHr = restHr;
    }

    public boolean isEnableBelt() {
        return enableBelt;
    }

    public void setEnableBelt(boolean enableBelt) {
        this.enableBelt = enableBelt;
    }

    public boolean isEnableAutoAdjust() {
        return enableAutoAdjust;
    }

    public void setEnableAutoAdjust(boolean enableAutoAdjust) {
        this.enableAutoAdjust = enableAutoAdjust;
    }

    public boolean isTestUser() {
        return testUser;
    }

    public void setTestUser(boolean testUser) {
        this.testUser = testUser;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
