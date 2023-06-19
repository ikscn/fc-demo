package com.applissima.fitconnectdemo;
/**
 * Created by ilkerkuscan on 13/05/17.
 */

public class LogDataUpload {

    private String insertDate;
    private String sourceType;
    private String sourceId;
    private String locationId;
    private String insertedByClass;
    private String message;
    private String errorDesc;

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

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
}
