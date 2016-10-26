package com.workshop2.mykids.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MingHan on 1/10/2016.
 */

public class Kid implements Serializable{
    private String kid_id;
    private String kid_name;
    private int kid_age=0;
    private String kid_date;
    private String kid_image;
    private String kid_gender;
    private String kid_image_name;
    private ArrayList<Schedule> schedule;

    public ArrayList<Schedule> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<Schedule> schedule) {
        this.schedule = schedule;
    }

    public String getKid_image_name() {
        return kid_image_name;
    }

    public void setKid_image_name(String kid_image_name) {
        this.kid_image_name = kid_image_name;
    }

    public Kid(String kid_name, String kid_date, String kid_image, String kid_id, String kid_gender, int kid_age){
        this.kid_name = kid_name;
        this.kid_date = kid_date;
        this.kid_image = kid_image;
        this.kid_id = kid_id;
        this.kid_gender = kid_gender;
        this.kid_age = kid_age;
    }

    public Kid(){}

    public String getKid_image() {
        return kid_image;
    }

    public void setKid_image(String kid_image) {
        this.kid_image = kid_image;
    }

    public String getKid_id() {
        return kid_id;
    }

    public void setKid_id(String kid_id) {
        this.kid_id = kid_id;
    }

    public String getKid_name() {
        return kid_name;
    }

    public void setKid_name(String kid_name) {
        this.kid_name = kid_name;
    }

    public int getKid_age() {
        return kid_age;
    }

    public void setKid_age(int kid_age) {
        this.kid_age = kid_age;
    }

    public String getKid_date() {
        return kid_date;
    }

    public void setKid_date(String kid_date) {
        this.kid_date = kid_date;
    }
    public String getKid_gender() {
        return kid_gender;
    }

    public void setKid_gender(String kid_gender) {
        this.kid_gender = kid_gender;
    }

    public Map<String, Object> toMap_new() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("kid_name", kid_name);
        result.put("kid_date", kid_date);
        result.put("kid_image", kid_image);
        result.put("kid_gender", kid_gender);

        return result;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("kid_name", kid_name);
        result.put("kid_date", kid_date);
        result.put("kid_gender", kid_gender);

        return result;
    }

}
