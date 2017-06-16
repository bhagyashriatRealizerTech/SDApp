package com.realizer.sallado.databasemodel;

import java.io.Serializable;

/**
 * Created by Win on 14-06-2017.
 */

public class DishGroupItem implements Serializable{

    public String dishID;
    public DishGroupItem(){}

    public DishGroupItem(String s) {
        this.dishID = s;
    }

    public String getDishID() {
        return dishID;
    }

    public void setDishID(String dishID) {
        this.dishID = dishID;
    }
}
