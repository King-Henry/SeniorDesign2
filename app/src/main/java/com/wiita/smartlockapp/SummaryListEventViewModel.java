package com.wiita.smartlockapp;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Wiita on 7/17/2017.
 */

@IgnoreExtraProperties
public class SummaryListEventViewModel {

    public String header;
    public String subHeader;
    public String url;
    public String date;
    public String time;

    public SummaryListEventViewModel() {}

    public SummaryListEventViewModel(String header, String subHeader, String url, String date, String time) {
        this.header = header;
        this.subHeader = subHeader;
        this.url = url;
        this.date = date;
        this.time = time;
    }
}
