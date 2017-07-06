package com.realizer.sallado.databasemodel;

/**
 * Created by Win on 06-07-2017.
 */

public class DeliveryPoint {
    public String address;
    public String deliveryPointId;
    public double latitude;
    public double longitude;
    public String key;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeliveryPointId() {
        return deliveryPointId;
    }

    public void setDeliveryPointId(String deliveryPointId) {
        this.deliveryPointId = deliveryPointId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
