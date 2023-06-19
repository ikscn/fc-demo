package com.applissima.fitconnectdemo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilkerkuscan on 14/06/17.
 */

public class FbVersion {

    public FbVersion(){
    }

    private int siteId;
    private String versionNo;
    private String versionUrl;

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("siteId", siteId);
        result.put("versionNo", versionNo);
        result.put("versionUrl", versionUrl);

        return result;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getVersionUrl() {
        return versionUrl;
    }

    public void setVersionUrl(String versionUrl) {
        this.versionUrl = versionUrl;
    }
}
