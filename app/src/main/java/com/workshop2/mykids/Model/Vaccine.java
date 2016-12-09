package com.workshop2.mykids.model;

/**
 * Created by MingHan on 5/10/2016.
 */

public class Vaccine {
  String vaccineDisease;
    String vaccineFunction;
    String vaccineID;
    String vaccineImage;
    String vaccineName;
    String vaccineDiseaseSymptom;
    String vaccineAbb;

    public String getVaccineAbb() {
        return vaccineAbb;
    }

    public void setVaccineAbb(String vaccineAbb) {
        this.vaccineAbb = vaccineAbb;
    }

    public String getVaccineDisease() {
        return vaccineDisease;
    }

    public void setVaccineDisease(String vaccineDisease) {
        this.vaccineDisease = vaccineDisease;
    }

    public String getVaccineFunction() {
        return vaccineFunction;
    }

    public void setVaccineFunction(String vaccineFunction) {
        this.vaccineFunction = vaccineFunction;
    }

    public String getVaccineID() {
        return vaccineID;
    }

    public void setVaccineID(String vaccineID) {
        this.vaccineID = vaccineID;
    }

    public String getVaccineImage() {
        return vaccineImage;
    }

    public void setVaccineImage(String vaccineImage) {
        this.vaccineImage = vaccineImage;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getVaccineDiseaseSymptom() {
        return vaccineDiseaseSymptom;
    }

    public void setVaccineDiseaseSymptom(String vaccineDiseaseSymptom) {
        this.vaccineDiseaseSymptom = vaccineDiseaseSymptom;
    }

    public Vaccine(){

    }

    public Vaccine(String vaccineDisease, String vaccineFunction, String vaccineID, String vaccineImage, String vaccineName, String vaccineDiseaseSymptom){
        this.vaccineDisease = vaccineDisease;
        this.vaccineFunction = vaccineFunction;
        this.vaccineID = vaccineID;
        this.vaccineImage = vaccineImage;
        this.vaccineDiseaseSymptom = vaccineDiseaseSymptom;
    }
}
