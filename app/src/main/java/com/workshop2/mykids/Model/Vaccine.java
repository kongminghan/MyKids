package com.workshop2.mykids.model;

/**
 * Created by MingHan on 5/10/2016.
 */

public class Vaccine {
  String vaccine_dis, vaccine_func, vaccine_id, vaccine_image, vaccine_name, vaccine_sym;

    public String getVaccine_dis() {
        return vaccine_dis;
    }

    public void setVaccine_dis(String vaccine_dis) {
        this.vaccine_dis = vaccine_dis;
    }

    public String getVaccine_func() {
        return vaccine_func;
    }

    public void setVaccine_func(String vaccine_func) {
        this.vaccine_func = vaccine_func;
    }

    public String getVaccine_id() {
        return vaccine_id;
    }

    public void setVaccine_id(String vaccine_id) {
        this.vaccine_id = vaccine_id;
    }

    public String getVaccine_image() {
        return vaccine_image;
    }

    public void setVaccine_image(String vaccine_image) {
        this.vaccine_image = vaccine_image;
    }

    public String getVaccine_name() {
        return vaccine_name;
    }

    public void setVaccine_name(String vaccine_name) {
        this.vaccine_name = vaccine_name;
    }

    public String getVaccine_sym() {
        return vaccine_sym;
    }

    public void setVaccine_sym(String vaccine_sym) {
        this.vaccine_sym = vaccine_sym;
    }

    public Vaccine(){

    }

    public Vaccine(String vaccine_dis, String vaccine_func, String vaccine_id, String vaccine_image, String vaccine_name, String vaccine_sym){
        this.vaccine_dis = vaccine_dis;
        this.vaccine_func = vaccine_func;
        this.vaccine_id = vaccine_id;
        this.vaccine_image = vaccine_image;
        this.vaccine_sym = vaccine_sym;
    }
}
