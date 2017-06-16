package com.realizer.sallado.databasemodel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Win on 22-05-2017.
 */

public class DayProgram implements Serializable {

    public String day;
    public String breakfastDishID;
    public String dinnerDishId;
    public String lunchDishID;
    public String snacksDishId;
    public String breakfastGroupID;
    public String dinnerGroupID;
    public String lunchGroupID;
    public String snacksGroupID;
    public String title;
    public String desc;

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

    public String getBreakfastGroupID() {
        return breakfastGroupID;
    }

    public void setBreakfastGroupID(String breakfastGroupID) {
        this.breakfastGroupID = breakfastGroupID;
    }

    public String getDinnerGroupID() {
        return dinnerGroupID;
    }

    public void setDinnerGroupID(String dinnerGroupID) {
        this.dinnerGroupID= dinnerGroupID;
    }

    public String getLunchGroupID() {
        return lunchGroupID;
    }

    public void setLunchGroupID(String lunchGroupID) {
        this.lunchGroupID = lunchGroupID;
    }

    public String getSnacksGroupID() {
        return snacksGroupID;
    }

    public void setSnacksGroupID(String snacksGroupID) {
        this.snacksGroupID = snacksGroupID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
