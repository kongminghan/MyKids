package com.workshop2.mykids.Model;

import android.content.Context;

/**
 * Created by MingHan on 5/10/2016.
 */

public class Vaccine {
    private String v_id;
    private String v_name;
    private String v_dosage;
    private String v_image;
    private String v_desc;
    private String v_expdate;

    public Vaccine(String v_id, String v_name, String v_dosage, String v_image, String v_desc, String v_expdate){
        this.v_id = v_id;
        this.v_name = v_name;
        this.v_dosage = v_dosage;
        this.v_image = v_image;
        this.v_desc = v_desc;
        this.v_expdate = v_expdate;
    }
    public String getV_id() {
        return v_id;
    }

    public void setV_id(String v_id) {
        this.v_id = v_id;
    }

    public String getV_name() {
        return v_name;
    }

    public void setV_name(String v_name) {
        this.v_name = v_name;
    }

    public String getV_dosage() {
        return v_dosage;
    }

    public void setV_dosage(String v_dosage) {
        this.v_dosage = v_dosage;
    }

    public String getV_image() {
        return v_image;
    }

    public void setV_image(String v_image) {
        this.v_image = v_image;
    }

    public String getV_desc() {
        return v_desc;
    }

    public void setV_desc(String v_desc) {
        this.v_desc = v_desc;
    }

    public String getExpdate() {
        return v_expdate;
    }

    public void setV_expdate(String v_expdate) {
        this.v_expdate = v_expdate;
    }
    public int getImageResourceId(Context context) {
        return context.getResources().getIdentifier(this.v_image, "drawable", context.getPackageName());
    }
}
