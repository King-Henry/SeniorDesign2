package com.wiita.smartlockapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Wiita on 7/22/2017.
 */

public class CommandDatabaseHandler {

    private DatabaseReference commandsDatabase;
    private FirebaseDatabase firebaseDatabase;

    public CommandDatabaseHandler(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        commandsDatabase = firebaseDatabase.getReference();
    }

    public void sendLockCommand(){
        Command command = new Command("lock");
        commandsDatabase.child("commands").setValue(command);
    }

    public void sendUnlockCommand(){
        Command command = new Command("unlock");
        commandsDatabase.child("commands").setValue(command);
    }
}
