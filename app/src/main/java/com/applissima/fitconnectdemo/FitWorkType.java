package com.applissima.fitconnectdemo;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ilkerkuscan on 30/01/17.
 */

public class FitWorkType extends RealmObject {

    @PrimaryKey
    private String activityTypeId; // UUID
    private String typeName;
    private boolean typeIsValid = true;
    private Date createDate;
    private Date updateDate;

    public String getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(String activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isTypeIsValid() {
        return typeIsValid;
    }

    public void setTypeIsValid(boolean typeIsValid) {
        this.typeIsValid = typeIsValid;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
