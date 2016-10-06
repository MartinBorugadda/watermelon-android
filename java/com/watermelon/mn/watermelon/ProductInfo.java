package com.watermelon.mn.watermelon;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Martin on 9/19/2016.
 */
@IgnoreExtraProperties
public class ProductInfo {
    public String title;
    public String desc;
    public String price;
    public String negotiable;

    ProductInfo(){

    }

    ProductInfo(String title, String desc, String price, String negotiable){
        this.title = title;
        this.desc = desc;
        this.price = price;
        this.negotiable = negotiable;
    }
}
