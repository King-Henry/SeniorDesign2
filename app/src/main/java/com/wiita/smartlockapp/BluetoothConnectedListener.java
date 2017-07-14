package com.wiita.smartlockapp;

import android.bluetooth.BluetoothSocket;

/**
 * Created by Wiita on 6/30/2017.
 */

public interface BluetoothConnectedListener {

    public void onConnected(BluetoothSocket bluetoothSocket);
}
