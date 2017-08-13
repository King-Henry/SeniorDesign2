package com.wiita.smartlockapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import static android.content.ContentValues.TAG;

/**
 * Created by Wiita on 7/14/2017.
 */

public class BluetoothReceiver extends BroadcastReceiver implements BluetoothConnectedListener{

    private static BluetoothConnector bluetoothConnector;
    private static BluetoothConnectionHandler connectionHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(BluetoothDevice.ACTION_FOUND.equals(action)){
            Log.d("BluetoothReceiver", " - bluetooth device found");
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            String deviceMAC = device.getAddress();
            Toast.makeText(context,deviceName,Toast.LENGTH_SHORT).show();
            if(deviceName != null && !deviceName.isEmpty()){
                Log.d("onReceive: ", deviceName);
                Log.d("onReceive: ", deviceMAC);
                if(deviceMAC.equals("98:D3:32:30:4B:1F")){
                    EventBus.getDefault().post(new OnBluetoothDeviceFoundEvent());
                    Toast.makeText(context,"Connected to LED",Toast.LENGTH_SHORT);
                    bluetoothConnector = new BluetoothConnector(device,this);
                    bluetoothConnector.run();
                }
                Toast.makeText(context, deviceName ,Toast.LENGTH_SHORT).show();
            }
        } else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            createNotification(context);
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter.isEnabled()){
                if(!bluetoothAdapter.isDiscovering()){
                    //so this still causes a notification to pop up whether we're turning bluetooth on or off...if I have time I'll go back and fix this
                    createNotification(context);
                }
            }
        }
    }

    @Override
    public void onConnected(BluetoothSocket bluetoothSocket) {
        connectionHandler = new BluetoothConnectionHandler(bluetoothSocket);
    }

    public static void writeToStreamAndClose(char message){
        if(connectionHandler != null){
            connectionHandler.writeChar(message);
            //connectionHandler.run();
            connectionHandler.cancelCommunication();
        }
    }

    private PendingIntent createAuthenticationIntent(Context context){
        Intent authIntent = new Intent(context, BluetoothAuthActivity.class);
        PendingIntent intent = PendingIntent.getActivity(context, 0,authIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        return intent;
    }

    private void createNotification(Context context){
        PendingIntent intent = createAuthenticationIntent(context);
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Tap to unlock door")
                .setContentText("When in range of the A1 Security System, tap to unlock door.")
                .setSubText("Bluetooth")
                .setContentIntent(intent);

        NotificationManager notficationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notficationManager.notify(103, builder.build());
    }
}
