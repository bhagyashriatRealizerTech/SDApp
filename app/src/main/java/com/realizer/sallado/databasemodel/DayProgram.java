package com.realizer.sallado.databasemodel;

import java.util.List;

/**
 * Created by Win on 22-05-2017.
 */

public class DayProgram {

    public String day;
    public String breakfastDishID;
    public String dinnerDishId;
    public String lunchDishID;
    public String snacksDishId;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getBreakfastDishID() {
        return breakfastDishID;
    }

    public void setBreakfastDishID(String breakfastDishID) {
        this.breakfastDishID = breakfastDishID;
    }

    public String getDinnerDishId() {
        return dinnerDishId;
    }

    public void setDinnerDishId(String dinnerDishId) {
        this.dinnerDishId = dinnerDishId;
    }

    public String getLunchDishID() {
        return lunchDishID;
    }

    public void setLunchDishID(String lunchDishID) {
        this.lunchDishID = lunchDishID;
    }

    public String getSnacksDishId() {
        return snacksDishId;
    }

    public void setSnacksDishId(String snacksDishId) {
        this.snacksDishId = snacksDishId;
    }
}
