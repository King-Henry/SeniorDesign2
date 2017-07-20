package com.wiita.smartlockapp;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Wiita on 7/18/2017.
 */
@IgnoreExtraProperties
public class Command {

    public String command;
    public String id;

    public Command() {}

    public Command(String command) {
        this.command = command;
        this.id = this.toString();
        Log.d("Command id: " , id);
    }
}
