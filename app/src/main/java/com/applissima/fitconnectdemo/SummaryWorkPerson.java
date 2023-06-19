package com.applissima.fitconnectdemo;

import android.graphics.Bitmap;

/**
 * Created by ilkerkuscan on 06/04/17.
 */

public class SummaryWorkPerson {

    //private Bitmap mImageBitmap = null;
    //private String hrSensorId = "";
    private String mUserName = "";
    private String mNickname = "";
    private String mDuration = "";
    private String mTotalCal = "";
    private String mCalMin = "";
    private String mMaxPerf = "";
    private String mAvgPerf = "";
    private String mMaxHr = "";
    private String mAvgHr = "";
    private int mGender = 0;
    private String mImageUrl = "";

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    /*public String getHrSensorId() {
        return hrSensorId;
    }

    public void setHrSensorId(String hrSensorId) {
        this.hrSensorId = hrSensorId;
    }*/

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public int getmGender() {
        return mGender;
    }

    public void setmGender(int mGender) {
        this.mGender = mGender;
    }

    /*public Bitmap getmImageBitmap() {
        return mImageBitmap;
    }

    public void setmImageBitmap(Bitmap mImageBitmap) {
        this.mImageBitmap = mImageBitmap;
    }*/

    public String getmNickname() {
        return mNickname;
    }

    public void setmNickname(String mNickname) {
        this.mNickname = mNickname;
    }

    public String getmDuration() {
        return mDuration;
    }

    public void setmDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public String getmTotalCal() {
        return mTotalCal;
    }

    public void setmTotalCal(String mTotalCal) {
        this.mTotalCal = mTotalCal;
    }

    public String getmCalMin() {
        return mCalMin;
    }

    public void setmCalMin(String mCalMin) {
        this.mCalMin = mCalMin;
    }

    public String getmMaxPerf() {
        return mMaxPerf;
    }

    public void setmMaxPerf(String mMaxPerf) {
        this.mMaxPerf = mMaxPerf;
    }

    public String getmAvgPerf() {
        return mAvgPerf;
    }

    public void setmAvgPerf(String mAvgPerf) {
        this.mAvgPerf = mAvgPerf;
    }

    public String getmMaxHr() {
        return mMaxHr;
    }

    public void setmMaxHr(String mMaxHr) {
        this.mMaxHr = mMaxHr;
    }

    public String getmAvgHr() {
        return mAvgHr;
    }

    public void setmAvgHr(String mAvgHr) {
        this.mAvgHr = mAvgHr;
    }
}
