package com.wiita.smartlockapp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.widget.Toast;

/**
 * Created by Wiita on 6/27/2017.
 */

public class BluetoothHandler {

    private BluetoothAdapter bluetoothAdapter;
    private Context context;


    public BluetoothHandler(Context context){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    public void turnBluetoothOn(){
        bluetoothAdapter.cancelDiscovery();
        if(!bluetoothAdapter.isEnabled()){
            Intent turnOnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if(context instanceof Activity){
                ((Activity) context).startActivityForResult(turnOnBluetooth,0);
            } else {
                Toast.makeText(context, "FAILED TO TURN ON",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "BLUETOOTH IS ALREADY ON",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isDiscovering()) {
            Log.d("turnBluetoothOn", "BluetoothAdapter already discovering");
            bluetoothAdapter.startDiscovery();
        }
    }

    public void turnBluetoothOff(){
        if(bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.disable();
            Toast.makeText(context, "TURNED OFF", Toast.LENGTH_SHORT).show();
        }
    }



    public void discoverDevices(){

    }
}
