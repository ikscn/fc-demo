package com.applissima.fitconnectdemo;

import java.io.Serializable;

/**
 * Created by ilkerkuscan on 26/10/17.
 */

public class Club implements Serializable {

    private String clubId;
    private String clubName;
    private String clubEmail;
    private int beltLimit;

    public Club() {
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public Club(String email){
        this.clubEmail = email;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubEmail() {
        return clubEmail;
    }

    public void setClubEmail(String clubEmail) {
        this.clubEmail = clubEmail;
    }

    public int getBeltLimit() {
        return beltLimit;
    }

    public void setBeltLimit(int beltLimit) {
        this.beltLimit = beltLimit;
    }
}
