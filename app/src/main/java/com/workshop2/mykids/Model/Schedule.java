package com.workshop2.mykids.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MingHan on 25/10/2016.
 */

public class Schedule implements Serializable{
    String s_name;
    String s_date;
    String s_id;
    String s_time;

    public boolean getS_status() {
        return s_status;
    }

    public void setS_status(boolean s_status) {
        this.s_status = s_status;
    }

    boolean s_status;
    int type;

    public static final int CALENDER_TYPE = 11;
    public static final int  EVENT_TYPE = 10;

    public Schedule(String s_name, String s_date, String s_id, String s_time, int type, boolean s_status) {
        this.s_name = s_name;
        this.s_date = s_date;
        this.s_id = s_id;
        this.s_time = s_time;
        this.type = type;
        this.s_status = s_status;
    }

    public Schedule(String s_name, String s_date, String s_id, String s_time) {
        this.s_name = s_name;
        this.s_date = s_date;
        this.s_id = s_id;
        this.s_time = s_time;
    }

    public Schedule(){}

    public Schedule(String s_date, int type){
        this.type = type;
        this.s_date=s_date;
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

    @JsonIgnore
    public Date compare() throws ParseException {
        SimpleDateFormat se = new SimpleDateFormat("dd-MM-yyyy");
        Date date = se.parse(s_date);
        return date;
    }
}
