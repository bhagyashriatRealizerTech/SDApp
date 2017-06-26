package com.realizer.sallado.databasemodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Win on 17-05-2017.
 */

public class User implements Serializable{

    public String userId;
    public String userName;
    public String userLoginID;
    public String password;
    public boolean isActive;
    public String userType;
    public String mobileNo;
    public String emailId;
    public String userDietType;
    public boolean isFirstLogin;
    public String gender;
    public String birthday;
    public List<String> userAddress;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLoginID() {
        return userLoginID;
    }

    public void setUserLoginID(String userLoginID) {
        this.userLoginID = userLoginID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserDietType() {
        return userDietType;
    }

    public void setUserDietType(String userDietType) {
        this.userDietType = userDietType;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public List<String> getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(List<String> userAddress) {
        this.userAddress = userAddress;
    }
}
