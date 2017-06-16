package com.realizer.sallado.databasemodel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Win on 22-05-2017.
 */

public class DietProgram implements Serializable{

    public boolean customized;
    public String programDescription;
    public String programId;
    public String programName;
    public String programPrice;
    public String programRatings;
    public String programThumbnailUrl;
    public String programType;
    public List<DayProgram> dayProgram;
    public int programDays;
    public boolean breakfastInclude;
    public boolean dinnerInclude;
    public boolean lunchInclude;
    public boolean snacksInclude;
    public String key;

    public boolean isCustomized() {
        return customized;
    }

    public void setCustomized(boolean customized) {
        this.customized = customized;
    }

    public String getProgramDescription() {
        return programDescription;
    }

    public void setProgramDescription(String programDescription) {
        this.programDescription = programDescription;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramPrice() {
        return programPrice;
    }

    public void setProgramPrice(String programPrice) {
        this.programPrice = programPrice;
    }

    public String getProgramRatings() {
        return programRatings;
    }

    public void setProgramRatings(String programRatings) {
        this.programRatings = programRatings;
    }

    public String getProgramThumbnailUrl() {
        return programThumbnailUrl;
    }

    public void setProgramThumbnailUrl(String programThumbnailUrl) {
        this.programThumbnailUrl = programThumbnailUrl;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public List<DayProgram> getDayProgram() {
        return dayProgram;
    }

    public void setDayProgram(List<DayProgram> dayProgram) {
        this.dayProgram = dayProgram;
    }

    public int getProgramDays() {
        return programDays;
    }

    public void setProgramDays(int programDays) {
        this.programDays = programDays;
    }

    public boolean isBreakfastInclude() {
        return breakfastInclude;
    }

    public void setBreakfastInclude(boolean breakfastInclude) {
        this.breakfastInclude = breakfastInclude;
    }

    public boolean isDinnerInclude() {
        return dinnerInclude;
    }

    public void setDinnerInclude(boolean dinnerInclude) {
        this.dinnerInclude = dinnerInclude;
    }

    public boolean isLunchInclude() {
        return lunchInclude;
    }

    public void setLunchInclude(boolean lunchInclude) {
        this.lunchInclude = lunchInclude;
    }

    public boolean isSnacksInclude() {
        return snacksInclude;
    }

    public void setSnacksInclude(boolean snacksInclude) {
        this.snacksInclude = snacksInclude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
