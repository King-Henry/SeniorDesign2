package com.wiita.smartlockapp;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements BluetoothConnectedListener, DatabaseHandler.ImageLoaderListener {

    private Button unlockButton;
    private Button lockButton;
    private Button ledOnButton;
    private Button ledOffButton;
    private ImageView liveFeedImage;
    private ProgressBar progressBar;

    private BluetoothHandler bluetoothHandler;
    private BluetoothConnector bluetoothConnector;
    private DatabaseHandler databaseHandler;
    private BluetoothConnectionHandler connectionHandler;
    private final static int REQUEST_COARSE_LOCATION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothHandler = new BluetoothHandler(this);
        databaseHandler = new DatabaseHandler(this);

        liveFeedImage = (ImageView)findViewById(R.id.live_feed_imageview);
        unlockButton = (Button)findViewById(R.id.mainactivity_unlock_button);
        lockButton = (Button)findViewById(R.id.mainactivity_lock_button);
        ledOnButton = (Button)findViewById(R.id.led_on_button);
        ledOffButton = (Button)findViewById(R.id.led_off_button);
        progressBar = (ProgressBar)findViewById(R.id.live_feed_loader);
        checkLocationPermission();
        databaseHandler.clearImageCache(this);

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothHandler.turnBluetoothOn();
//                progressBar.setVisibility(View.VISIBLE);
            }
        });

        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectionHandler != null) {
                    connectionHandler.cancelCommunication();
                }
                bluetoothConnector.cancelConnection();
                bluetoothHandler.turnBluetoothOff();
//                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        ledOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                connectionHandler.writeChars('1');
                connectionHandler.run();
            }
        });

        ledOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionHandler.writeChar('0');
            }
        });

    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                if(deviceName != null && !deviceName.isEmpty()){
                    Log.d("onReceive: ", deviceName);
                    if(deviceName.equals("HC-05")){
                        Toast.makeText(context,"Connected to LED",Toast.LENGTH_SHORT);
                        bluetoothConnector = new BluetoothConnector(device,MainActivity.this);
                        bluetoothConnector.run();
                    }
                    Toast.makeText(context, deviceName ,Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_COARSE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                bluetoothHandler.prepareBroadcastReceiver(broadcastReceiver);
            }
            else{
                Log.d("onRequestPermissions...", "onRequestPermissionsResult: Permission not granted");
            }
        }
    }

    @Override
    public void onConnected(BluetoothSocket socket) {
        connectionHandler = new BluetoothConnectionHandler(socket);
        ledOnButton.setEnabled(true);
        ledOffButton.setEnabled(true);
//        connectionHandler.run();
    }

    @Override
    public void onImageReady(String url) {
        Glide.with(this)
                .load(url)
                .into(liveFeedImage);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setLoadingState() {
        Log.d("setLoadingState,", "setLoadingState: making loader visible");
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
    }

    @Override
    protected void onResume() {

        if(bluetoothHandler != null){
            bluetoothHandler.prepareBroadcastReceiver(broadcastReceiver);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(bluetoothHandler != null){
            bluetoothHandler.cancelDiscovery();
            bluetoothHandler.unRegisterBroadcastReceiver(broadcastReceiver);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(bluetoothHandler != null){
            bluetoothHandler.cancelDiscovery();
            databaseHandler.destroy();
        }
        super.onDestroy();
    }
}
