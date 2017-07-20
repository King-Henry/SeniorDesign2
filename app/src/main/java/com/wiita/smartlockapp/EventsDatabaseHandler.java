package com.wiita.smartlockapp;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Wiita on 7/18/2017.
 */

public class EventsDatabaseHandler implements ChildEventListener, ValueEventListener{

    private DatabaseReference eventsDatabase;
    private FirebaseDatabase firebaseDatabase;
    private EventsDatabaseListener listener;
    private boolean initialLoadComplete;

    ArrayList<HistoryEvent> events = new ArrayList<>();


    public EventsDatabaseHandler(EventsDatabaseListener listener){
        firebaseDatabase = FirebaseDatabase.getInstance();
        eventsDatabase = firebaseDatabase.getReference("history");
        eventsDatabase.addChildEventListener(this);
        eventsDatabase.addListenerForSingleValueEvent(this);
        this.listener = listener;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HistoryEvent historyEvent = dataSnapshot.getValue(HistoryEvent.class);
            Log.d("History event", "onChildAdded");
            Log.d("event header: ", historyEvent.header);
            Log.d("event subheader: ", historyEvent.subheader);
            Log.d("event url: ", historyEvent.url);
            Log.d("event date: ", historyEvent.date);
            Log.d("event header: ", historyEvent.time);
            events.add(historyEvent);
        if(initialLoadComplete){
            listener.onNewListOfEvents(events);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        //So this is weird but I'm using this callback to know when the consecutive onChildAdded calls are done
        //When they are done, I then build my models and handle bidnezzzzzz
        Log.d("EventDatabaseHandler", "onDataChange: ");
        initialLoadComplete = true;
        listener.onNewListOfEvents(events);

    }

    public void destroy(){
        eventsDatabase.removeEventListener((ValueEventListener)this);
        eventsDatabase.removeEventListener((ChildEventListener)this);
        events.clear();
    }

    public interface EventsDatabaseListener{
        void onNewListOfEvents(ArrayList<HistoryEvent> events);
    }
}
