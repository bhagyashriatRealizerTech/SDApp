package com.realizer.sallado.utils;

import com.google.firebase.database.FirebaseDatabase;
import com.realizer.sallado.databasemodel.UserDietProgram;

/**
 * Created by Win on 16-06-2017.
 */

public class Singleton {
    public static Singleton instance = null;
    public static boolean isChange = false;
    public static boolean isDayListChange =false;
    public static boolean isDietProgramChange = false;
    public static UserDietProgram userDietProgram;
    public static FirebaseDatabase database = null;

    private Singleton(){

    }

    public static Singleton getInstance(){

        if(instance == null){
            instance = new Singleton();
        }

        return instance;
    }

    public static boolean isChange() {
        return isChange;
    }

    public static void setIsChange(boolean isChange) {
        Singleton.isChange = isChange;
    }

    public static UserDietProgram getUserDietProgram() {
        return userDietProgram;
    }

    public static void setUserDietProgram(UserDietProgram userDietProgram) {
        Singleton.userDietProgram = userDietProgram;
    }

    public static boolean isDayListChange() {
        return isDayListChange;
    }

    public static void setIsDayListChange(boolean isDayListChange) {
        Singleton.isDayListChange = isDayListChange;
    }

    public static boolean isDietProgramChange() {
        return isDietProgramChange;
    }

    public static void setIsDietProgramChange(boolean isDietProgramChange) {
        Singleton.isDietProgramChange = isDietProgramChange;
    }

    public static FirebaseDatabase getDatabase() {
        return database;
    }

    public static void setDatabase(FirebaseDatabase database) {
        boolean flag = false;
        if(Singleton.database == null)
            flag = true;
        Singleton.database = database;
        if(flag)
        Singleton.database.setPersistenceEnabled(true);
    }
}
