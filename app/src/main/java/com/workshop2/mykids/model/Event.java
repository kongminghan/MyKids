package com.workshop2.mykids.model;

/**
 * Created by MingHan on 20/11/2016.
 */

public class Event {
    String s_name;
    String s_date;
    String s_id;
    String s_time;
    String notifyBefore;
    String notifyTime;
    String notifyDate;

    String kid_name, kid_image;

    boolean s_status;
    int type;

    public static final int TITLE = 11;
    public static final int EVENT = 10;


    public String getKid_name() {
        return kid_name;
    }

    public void setKid_name(String kid_name) {
        this.kid_name = kid_name;
    }

    public String getKid_image() {
        return kid_image;
    }

    public void setKid_image(String kid_image) {
        this.kid_image = kid_image;
    }

    public Event(String s_name, String s_date, String s_id, String s_time, String kid_name, String kid_image, int type) {
        this.s_name = s_name;
        this.s_date = s_date;
        this.s_id = s_id;
        this.s_time = s_time;
        this.kid_image = kid_image;
        this.kid_name = kid_name;
        this.type = type;
    }

    public Event(){}

    public Event(String kid_name, int type){
        this.type = type;
        this.kid_name=kid_name;
    }

    public String getS_date() {
        return s_date;
    }

    public void setS_date(String s_date) {
        this.s_date = s_date;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getS_time() {
        return s_time;
    }

    public void setS_time(String s_time) {
        this.s_time = s_time;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNotifyBefore() {
        return notifyBefore;
    }

    public void setNotifyBefore(String notifyBefore) {
        this.notifyBefore = notifyBefore;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(String notifyDate) {
        this.notifyDate = notifyDate;
    }

    public boolean getS_status() {
        return s_status;
    }

    public void setS_status(boolean s_status) {
        this.s_status = s_status;
    }
}
