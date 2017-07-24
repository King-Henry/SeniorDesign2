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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Wiita on 7/9/2017.
 */

public class ImagesDatabaseHandler implements ChildEventListener, ValueEventListener {

    private DatabaseReference imagesDatabase;
    private ImageLoaderListener imageLoaderListener;
    private HandlerThread handlerThread;
    private Handler handler;
    private FirebaseDatabase firebaseDatabase;
    private boolean initialLoadComplete;

    ArrayList<Image> images = new ArrayList<>();

    public interface ImageLoaderListener{
        void setLoadingState();
        void onImageReady(Image image);
    }

    public ImagesDatabaseHandler(ImageLoaderListener listener){
        firebaseDatabase = FirebaseDatabase.getInstance();
        imagesDatabase = firebaseDatabase.getReference("gifs");
        imagesDatabase.addChildEventListener(this);
        imagesDatabase.addValueEventListener(this);
        imageLoaderListener = listener;
        handlerThread = new HandlerThread("BACKGROUND_THREAD");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d("Image event", "onChildAdded ");
        imageLoaderListener.setLoadingState();
        Log.d(TAG, "onChildAdded() called with: dataSnapshot = [" + dataSnapshot + "], s = [" + s + "]");
        Image image = dataSnapshot.getValue(Image.class);
        Log.d(TAG, image.url);
        images.add(image);
        if(!initialLoadComplete){return;}
        imageLoaderListener.onImageReady(image);

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
    public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) { }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

    @Override
    public void onCancelled(DatabaseError databaseError) { }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d("ImagesDatabaseHandler", "onDataChange");
        if(initialLoadComplete){return;}
        imageLoaderListener.onImageReady(images.get(images.size()-1));
        initialLoadComplete = true;
    }

    public void destroy(){
        handlerThread.quitSafely();
        imagesDatabase.removeEventListener((ChildEventListener) this);
        imagesDatabase.removeEventListener((ValueEventListener)this);
    }
}
