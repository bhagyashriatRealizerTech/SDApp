package com.realizer.sallado.databasemodel;

import java.io.Serializable;

/**
 * Created by Win on 22-05-2017.
 */

public class DoctorAvalability implements Serializable{

    public String availableEn2Time;
    public String availableStart2Time;
    public String availableEndTime;
    public String availableStartTime;
    public String doctorId;
    public String locationId;
    public String weekDayEnd;
    public String weekDayStart;

    public String getAvailableEn2Time() {
        return availableEn2Time;
    }

    public void setAvailableEn2Time(String availableEn2Time) {
        this.availableEn2Time = availableEn2Time;
    }

    public String getAvailableStart2Time() {
        return availableStart2Time;
    }

    public void setAvailableStart2Time(String availableStart2Time) {
        this.availableStart2Time = availableStart2Time;
    }

    public String getAvailableEndTime() {
        return availableEndTime;
    }

    public void setAvailableEndTime(String availableEndTime) {
        this.availableEndTime = availableEndTime;
    }

    public String getAvailableStartTime() {
        return availableStartTime;
    }

    public void setAvailableStartTime(String availableStartTime) {
        this.availableStartTime = availableStartTime;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getWeekDayEnd() {
        return weekDayEnd;
    }

    public void setWeekDayEnd(String weekDayEnd) {
        this.weekDayEnd = weekDayEnd;
    }

    public String getWeekDayStart() {
        return weekDayStart;
    }

    public void setWeekDayStart(String weekDayStart) {
        this.weekDayStart = weekDayStart;
    }
}
