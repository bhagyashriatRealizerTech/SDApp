package com.realizer.sallado.databasemodel;

/**
 * Created by Win on 22-05-2017.
 */

public class DoctorHolidays {

    public String doctorId;
    public String holidayEndDate;
    public String holidayStartDate;


    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getHolidayEndDate() {
        return holidayEndDate;
    }

    public void setHolidayEndDate(String holidayEndDate) {
        this.holidayEndDate = holidayEndDate;
    }

    public String getHolidayStartDate() {
        return holidayStartDate;
    }

    public void setHolidayStartDate(String holidayStartDate) {
        this.holidayStartDate = holidayStartDate;
    }
}
