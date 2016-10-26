package com.workshop2.mykids.Model;

import java.io.Serializable;

/**
 * Created by MingHan on 25/10/2016.
 */

public class Schedule implements Serializable{
    String s_name, s_date, s_id, s_time;

    public Schedule(String s_name, String s_date, String s_id, String s_time) {
        this.s_name = s_name;
        this.s_date = s_date;
        this.s_id = s_id;
        this.s_time = s_time;
    }

    public Schedule(){}

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
}
