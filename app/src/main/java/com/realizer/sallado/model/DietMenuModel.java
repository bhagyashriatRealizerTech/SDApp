package com.realizer.sallado.model;

import java.io.Serializable;

/**
 * Created by Win on 10-05-2017.
 */

public class DietMenuModel implements Serializable{

    public String menuName;
    public String menuPrice;
    public String menuRatings;
    public String menuType;
    public String menuDetail;
    public String menuImage;
    public String menuImp;
    public String menuID;
    public String quantity;

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(String menuPrice) {
        this.menuPrice = menuPrice;
    }

    public String getMenuRatings() {
        return menuRatings;
    }

    public void setMenuRatings(String menuRatings) {
        this.menuRatings = menuRatings;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getMenuDetail() {
        return menuDetail;
    }

    public void setMenuDetail(String menuDetail) {
        this.menuDetail = menuDetail;
    }

    public String getMenuImage() {
        return menuImage;
    }

    public void setMenuImage(String menuImage) {
        this.menuImage = menuImage;
    }

    public String getMenuImp() {
        return menuImp;
    }

    public void setMenuImp(String menuImp) {
        this.menuImp = menuImp;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMenuID() {
        return menuID;
    }

    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }
}
