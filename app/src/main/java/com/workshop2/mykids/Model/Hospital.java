package com.workshop2.mykids.Model;

/**
 * Created by MingHan on 14/11/2016.
 */

public class Hospital {
    String hospitalAddress, hospitalID, hospitalName, hospitalPoscode, hospitalTelNum
            , hospitalImage;

    public Hospital(String hospitalAddress, String hospitalID, String hospitalName, String hospitalPoscode, String hospitalTelNum) {
        this.hospitalAddress = hospitalAddress;
        this.hospitalID = hospitalID;
        this.hospitalName = hospitalName;
        this.hospitalPoscode = hospitalPoscode;
        this.hospitalTelNum = hospitalTelNum;
    }

    public String getHospitalImage() {
        return hospitalImage;
    }

    public void setHospitalImage(String hospitalImage) {
        this.hospitalImage = hospitalImage;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalPoscode() {
        return hospitalPoscode;
    }

    public void setHospitalPoscode(String hospitalPoscode) {
        this.hospitalPoscode = hospitalPoscode;
    }

    public String getHospitalTelNum() {
        return hospitalTelNum;
    }

    public void setHospitalTelNum(String hospitalTelNum) {
        this.hospitalTelNum = hospitalTelNum;
    }
}
