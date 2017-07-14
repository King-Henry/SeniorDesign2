package com.wiita.smartlockapp;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

/**
 * Created by Wiita on 6/30/2017.
 */

public class BluetoothConnectionHandler extends Thread{

    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private BluetoothCommunicationReadyListener listener;
    private byte[] buffer;
    private Handler mHandler;

    public BluetoothConnectionHandler(BluetoothSocket bluetoothSocket){
        this.bluetoothSocket = bluetoothSocket;
        InputStream tempInputStream = null;
        OutputStream tempOutputStream = null;

        try {
            tempInputStream = bluetoothSocket.getInputStream();
        }catch (IOException e){
            Log.e("BluetoothConnectionh: ", "failed to create input stream",e);
        }

        try {
            tempOutputStream = bluetoothSocket.getOutputStream();
        }catch (IOException e){
            Log.e("BluetoothConnectionh: ", "failed to create output stream",e);
        }

        inputStream = tempInputStream;
        outputStream = tempOutputStream;
        if(tempInputStream != null) {
            dataInputStream = new DataInputStream(tempInputStream);
        }

        if(outputStream != null){
            dataOutputStream = new DataOutputStream(tempOutputStream);
        }

    }

    public void run(){
        buffer = new byte[1024];
        while(true){
            try {
                if(dataInputStream.available() == 0){
                    break;
                }
                int a = dataInputStream.read();
                Log.d(TAG, "" + a);
                Log.d("run() called", "reading input stream");
            } catch (IOException e){
                Log.e("BluetoothConnectionh","input stream not connected",e);
                break;
            }
        }
    }

    public void write(byte[] bytes){
        try {
            outputStream.write(bytes);

        }catch(IOException e){
            Log.e("BluetoothConnectionh","Couldnt send data to device",e);
        }
    }

    public void writeChar(char a){
        try {
            dataOutputStream.writeChar(a);

        }catch(IOException e){
            Log.e("BluetoothConnectionh","Couldnt send data to device",e);
        }
    }

    public void writeChars(String a){
        try {
            dataOutputStream.writeChars(a);
        }catch(IOException e){
            Log.e("BluetoothConnectionh","Couldnt send data to device",e);
        }
    }



    public void cancelCommunication(){
        try{
            dataInputStream.close();
            dataOutputStream.close();
        } catch (IOException e){
            Log.e("BluetoothConnectionh","failed to close the connection socket",e);
        }
    }

}
