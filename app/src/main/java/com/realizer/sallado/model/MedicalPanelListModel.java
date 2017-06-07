package com.realizer.sallado.model;

import com.realizer.sallado.databasemodel.DoctorAvalability;
import com.realizer.sallado.databasemodel.DoctorHolidays;
import com.realizer.sallado.databasemodel.DoctorLocation;
import com.realizer.sallado.databasemodel.MedicalPanel;

import java.util.List;

/**
 * Created by Win on 22-05-2017.
 */

public class MedicalPanelListModel {

    public MedicalPanel medicalPanel;
    public List<DoctorLocation> doctorLocations;
    public List<DoctorAvalability>doctorAvalabilities;
    public List<DoctorHolidays> doctorHolidays;
    public String doctorType;
    public String key;

    public MedicalPanel getMedicalPanel() {
        return medicalPanel;
    }

    public void setMedicalPanel(MedicalPanel medicalPanel) {
        this.medicalPanel = medicalPanel;
    }

    public List<DoctorLocation>  getDoctorLocations() {
        return doctorLocations;
    }

    public void setDoctorLocations(List<DoctorLocation> doctorLocations) {
        this.doctorLocations = doctorLocations;
    }

    public List<DoctorAvalability> getDoctorAvalabilities() {
        return doctorAvalabilities;
    }

    public void setDoctorAvalabilities( List<DoctorAvalability> doctorAvalabilities) {
        this.doctorAvalabilities = doctorAvalabilities;
    }

    public List<DoctorHolidays> getDoctorHolidays() {
        return doctorHolidays;
    }

    public void setDoctorHolidays(List<DoctorHolidays> doctorHolidays) {
        this.doctorHolidays = doctorHolidays;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDoctorType() {
        return doctorType;
    }

    public void setDoctorType(String doctorType) {
        this.doctorType = doctorType;
    }
}
