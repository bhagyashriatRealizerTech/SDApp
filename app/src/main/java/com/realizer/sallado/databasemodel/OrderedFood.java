package com.realizer.sallado.databasemodel;

import java.io.Serializable;

/**
 * Created by Win on 23-05-2017.
 */

public class OrderedFood implements Serializable{

    public String dishId;
    public String dishQuantity;
    public String dishName;
    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getDishQuantity() {
        return dishQuantity;
    }

    public void setDishQuantity(String dishQuantity) {
        this.dishQuantity = dishQuantity;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }
}
