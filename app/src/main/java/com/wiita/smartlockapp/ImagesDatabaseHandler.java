package com.wiita.smartlockapp;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Wiita on 7/9/2017.
 */

public class ImagesDatabaseHandler implements ChildEventListener {

    private DatabaseReference imagesDatabase;
    private DatabaseReference commandsDatabase;
    private DatabaseReference historyDatabase;
    private ImageLoaderListener imageLoaderListener;
    private HandlerThread handlerThread;
    private Handler handler;
    private FirebaseDatabase firebaseDatabase;

    ArrayList<HistoryEvent> events = new ArrayList<>();

    public interface ImageLoaderListener{
        void setLoadingState();
        void onImageReady(String url);
    }

    public ImagesDatabaseHandler(ImageLoaderListener listener){
        firebaseDatabase = FirebaseDatabase.getInstance();
        imagesDatabase = firebaseDatabase.getReference("images");
        commandsDatabase = firebaseDatabase.getReference();
        historyDatabase = firebaseDatabase.getReference("history");
        historyDatabase.addChildEventListener(this);
        imagesDatabase.addChildEventListener(this);
        imageLoaderListener = listener;
        handlerThread = new HandlerThread("BACKGROUND_THREAD");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//        Log.d("Image event", "onChildAdded ");
//        imageLoaderListener.setLoadingState();
//        Log.d(TAG, "onChildAdded() called with: dataSnapshot = [" + dataSnapshot + "], s = [" + s + "]");
//        Image image = dataSnapshot.getValue(Image.class);
//        Log.d(TAG, image.url);
//        imageLoaderListener.onImageReady(image.url);

    }

    public void clearImageCache(final Context context){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        });
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

    public void sendLockCommand(){
        Command command = new Command("lock");
        commandsDatabase.child("commands").setValue(command);
    }

    public void sendUnlockCommand(){
        Command command = new Command("unlock");
        commandsDatabase.child("commands").setValue(command);
    }

    public void destroy(){
        handlerThread.quitSafely();
        historyDatabase.removeEventListener(this);
        imagesDatabase.removeEventListener(this);
    }
}