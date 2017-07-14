package com.wiita.smartlockapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Wiita on 6/30/2017.
 */

public class BluetoothConnector extends Thread {

    private final static UUID UUID_STRING = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;
    private final BluetoothConnectedListener listener;

    public BluetoothConnector(BluetoothDevice device, BluetoothConnectedListener listener) {

        this.listener = listener;
        BluetoothSocket temp = null;
        bluetoothDevice = device;

        try {
            temp = device.createRfcommSocketToServiceRecord(UUID_STRING);
        } catch (IOException e){
            Log.e("BluetoothConnector", "Failed to create socket",e);
        }

        bluetoothSocket = temp;
    }

    public void run(){
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        try {
            bluetoothSocket.connect();
            listener.onConnected(bluetoothSocket);
        } catch (IOException connectException){
            try {
                bluetoothSocket.close();
            }catch (IOException closeException) {
                Log.e("BluetoothConnector", "Could not close socket",closeException);
            }
            return;
        }
    }

    public void cancelConnection(){
        try{
            bluetoothSocket.close();
        }catch (IOException e) {
            Log.e("BluetoothConnector", "Could not close socket",e);
        }
    }

}
