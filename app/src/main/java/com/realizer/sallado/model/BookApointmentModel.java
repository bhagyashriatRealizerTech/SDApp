package com.realizer.sallado.model;

import java.util.List;

/**
 * Created by Win on 23-05-2017.
 */

public class BookApointmentModel {
    public String day;
    public String date;
    public String startTime;
    public String endTime;
    public String startTime2;
    public String endTime2;
    public List<String> slotList;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime2() {
        return startTime2;
    }

    public void setStartTime2(String startTime2) {
        this.startTime2 = startTime2;
    }

    public String getEndTime2() {
        return endTime2;
    }

    public void setEndTime2(String endTime2) {
        this.endTime2 = endTime2;
    }

    public List<String> getSlotList() {
        return slotList;
    }

    public void setSlotList(List<String> slotList) {
        this.slotList = slotList;
    }
}
