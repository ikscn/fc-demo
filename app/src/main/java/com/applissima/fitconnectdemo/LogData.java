package com.applissima.fitconnectdemo;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;
import io.realm.RealmObject;

/**
 * Created by ilkerkuscan on 06/03/17.
 */

public class LogData extends RealmObject {

    private String sourceType;
    private String sourceId;
    private String locationId;
    private String insertedByClass;
    private String message;
    private String errorDesc;
    private Date insertDate;
    private Date uploadTryDate;
    private int uploadTryCount = 0;
    private Date uploadDate;
    private boolean uploadSuccessful = false;
    private boolean test = false;

    public JSONObject toJSON(){

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("insertDate", AppUtils.getUploadLogDateString(this.insertDate));
            jsonObject.put("sourceType", this.sourceType);
            jsonObject.put("sourceId", this.sourceId);
            jsonObject.put("locationId", this.locationId);
            jsonObject.put("insertedByClass", this.insertedByClass);
            jsonObject.put("message", this.message);
            jsonObject.put("errorDesc", this.errorDesc);

        } catch (JSONException e) {
            jsonObject = null;
            e.printStackTrace();
        }

        return jsonObject;

    }

    public String toJSONString(){

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("insertDate", AppUtils.getUploadLogDateString(this.insertDate));
            jsonObject.put("sourceType", this.sourceType);
            jsonObject.put("sourceId", this.sourceId);
            jsonObject.put("locationId", this.locationId);
            jsonObject.put("insertedByClass", this.insertedByClass);
            jsonObject.put("message", this.message);
            jsonObject.put("errorDesc", this.errorDesc);

        } catch (JSONException e) {
            jsonObject = null;
            e.printStackTrace();
        }

        return jsonObject==null? "" : jsonObject.toString();

    }

    /*public LogDataUpload prepareUpload(){

        LogDataUpload data = new LogDataUpload();
        data.setInsertDate(AppUtils.getUploadDateString(this.getInsertDate()));
        data.setSourceType(this.getSourceType());
        data.setSourceId(this.getSourceId());
        data.setLocationId(this.getLocationId());
        data.setInsertedByClass(this.getInsertedByClass());
        data.setMessage(this.getMessage());
        data.setErrorDesc(this.getErrorDesc());

        return data;

    }*/

    /*public String toJSONStringOld(){

        String jsonString =
                        "{"
                        + "\"insertDate\":\""+ AppUtils.getUploadDateString(this.insertDate) +  "\""
                        + ",\"sourceType\":\"" + this.sourceType + "\""
                        + ",\"sourceId\":\"" + this.sourceId + "\""
                        + ",\"locationId\":\"" + this.locationId + "\""
                        + ",\"insertedByClass\":\"" + this.insertedByClass + "\""
                        + ",\"message\":\"" + this.message + "\""
                        + ",\"errorDesc\":\"" + this.errorDesc + "\""
                        + "}";

        return jsonString;
    }*/

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getInsertedByClass() {
        return insertedByClass;
    }

    public void setInsertedByClass(String insertedByClass) {
        this.insertedByClass = insertedByClass;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public Date getUploadTryDate() {
        return uploadTryDate;
    }

    public void setUploadTryDate(Date uploadTryDate) {
        this.uploadTryDate = uploadTryDate;
    }

    public int getUploadTryCount() {
        return uploadTryCount;
    }

    public void setUploadTryCount(int uploadTryCount) {
        this.uploadTryCount = uploadTryCount;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public boolean isUploadSuccessful() {
        return uploadSuccessful;
    }

    public void setUploadSuccessful(boolean uploadSuccessful) {
        this.uploadSuccessful = uploadSuccessful;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }
}
