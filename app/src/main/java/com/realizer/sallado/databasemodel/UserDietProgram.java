package com.realizer.sallado.databasemodel;

/**
 * Created by Win on 24-05-2017.
 */

public class UserDietProgram {

    public DietProgram dietProgram;
    public String userId;
    public String startDate;
    public String endDate;

    public DietProgram getDietProgram() {
        return dietProgram;
    }

    public void setDietProgram(DietProgram dietProgram) {
        this.dietProgram = dietProgram;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
