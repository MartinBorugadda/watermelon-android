package com.watermelon.mn.watermelon;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Martin on 9/19/2016.
 */
@IgnoreExtraProperties
public class UserProfileInfo {
    public String address;
    public String email;
    public String username;
    public String phone;

    UserProfileInfo(){

    }

    UserProfileInfo(String username, String email, String phone, String address){
        this.address = address;
        this.email = email;
        this.username = username;
        this.phone = phone;
    }
}
