package com.wiita.smartlockapp;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Wiita on 7/18/2017.
 */

@IgnoreExtraProperties
public class HistoryEvent {

    public String header;
    public String subheader;
    public String date;
    public String time;
    public String url;

    public HistoryEvent() {
    }

    public HistoryEvent(String header, String subheader, String date, String time, String url) {
        this.header = header;
        this.subheader = subheader;
        this.date = date;
        this.time = time;
        this.url = url;
    }
}
