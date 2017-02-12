package com.watermelon.mn.watermelon;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by Martin on 9/19/2016.
 */
@IgnoreExtraProperties
public class ProductInfo {
    public String title;
    public String desc;
    public String price;
    public String negotiable;
    public List<String> productPictureURLs;
    public String ownerUserID="";

    ProductInfo(){

    }



    ProductInfo(String title, String desc, String price, String negotiable, List<String> productPictureURLs, String ownerUserID){
        this.title = title;
        this.desc = desc;
        this.price = price;
        this.negotiable = negotiable;
        this.productPictureURLs = productPictureURLs;
        this.ownerUserID = ownerUserID;
    }
}
