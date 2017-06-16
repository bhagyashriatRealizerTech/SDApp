package com.realizer.sallado.databasemodel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Win on 14-06-2017.
 */

public  class DishGroup implements Serializable  {

    public String groupID;
    public String groupName;
    public List<DishGroupItem> dishItems;

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<DishGroupItem> getDishItems() {
        return dishItems;
    }

    public void setDishItems(List<DishGroupItem> dishItems) {
        this.dishItems = dishItems;
    }
}
