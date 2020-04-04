package com.tecnosols.homeaidworker;

public class workerDetail {
    String wName, wPhone, wType, wAdress, wId, isApproved;

    public workerDetail() {
    }

    public workerDetail(String wName, String wPhone, String wType, String wAdress, String wId, String isApproved) {
        this.wName = wName;
        this.wPhone = wPhone;
        this.wType = wType;
        this.wAdress = wAdress;
        this.wId = wId;
        this.isApproved = isApproved;
    }

    public String getwName() {
        return wName;
    }

    public void setwName(String wName) {
        this.wName = wName;
    }

    public String getwPhone() {
        return wPhone;
    }

    public void setwPhone(String wPhone) {
        this.wPhone = wPhone;
    }

    public String getwType() {
        return wType;
    }

    public void setwType(String wType) {
        this.wType = wType;
    }

    public String getwAdress() {
        return wAdress;
    }

    public void setwAdress(String wAdress) {
        this.wAdress = wAdress;
    }

    public String getwId() {
        return wId;
    }

    public void setwId(String wId) {
        this.wId = wId;
    }

    public String getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(String isApproved) {
        this.isApproved = isApproved;
    }
}
