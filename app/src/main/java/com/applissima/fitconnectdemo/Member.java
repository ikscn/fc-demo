package com.applissima.fitconnectdemo;


import java.util.Date;

/**
 * Created by ilkerkuscan on 26/10/17.
 */

public class Member {

    private String email;
    private String fullName;
    private String nickName;
    private String imageUrl;
    private String tempBeltId;
    private int gender;
    private int age;
    private int height;
    private int weight;
    private int maxHr;
    private int restHr;
    private String birthDate;
    private Date lastDataUpdateDate;


    public Member(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTempBeltId() {
        return tempBeltId;
    }

    public void setTempBeltId(String tempBeltId) {
        this.tempBeltId = tempBeltId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Date getLastDataUpdateDate() {
        return lastDataUpdateDate;
    }

    public void setLastDataUpdateDate(Date lastDataUpdateDate) {
        this.lastDataUpdateDate = lastDataUpdateDate;
    }
}
