package com.wiita.smartlockapp;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Wiita on 7/9/2017.
 */
@IgnoreExtraProperties
public class Image {

    public String url;
    public String entryId;

    public Image(){}

    public Image(String id, String url){
        this.url = url;
        entryId = id;
    }
}
