package com.realizer.sallado.databasemodel;

import java.util.List;

/**
 * Created by Win on 24-05-2017.
 */

public class OrderFood {

    public String orderDate;
    public String orderId;
    public String orderLastUpdate;
    public String orderStatus;
    public String orderTax;
    public String orderTotalPrice;
    public List<OrderedFood> orderedFood;
    public String userID;


    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderLastUpdate() {
        return orderLastUpdate;
    }

    public void setOrderLastUpdate(String orderLastUpdate) {
        this.orderLastUpdate = orderLastUpdate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderTax() {
        return orderTax;
    }

    public void setOrderTax(String orderTax) {
        this.orderTax = orderTax;
    }

    public String getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(String orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }

    public List<OrderedFood> getOrderedFood() {
        return orderedFood;
    }

    public void setOrderedFood(List<OrderedFood> orderedFood) {
        this.orderedFood = orderedFood;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
