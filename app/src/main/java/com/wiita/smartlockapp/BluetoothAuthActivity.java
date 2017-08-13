package com.wiita.smartlockapp;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.content.DialogInterface.BUTTON_NEGATIVE;

public class BluetoothAuthActivity extends AppCompatActivity implements AuthenticationListener, DialogInterface.OnClickListener {

    private LoginHandler loginHandler;
    private AlertDialog fingerPrintDialog;
    private ProgressDialog progressDialog;
    private AlertDialog pinDialog;
    private final int REQUEST_COARSE_LOCATION = 10;
    BluetoothAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_auth);
        adapter = BluetoothAdapter.getDefaultAdapter();
        loginHandler = new LoginHandler(this);
        checkLocationPermission();
        addProgressDialog();
        if(loginHandler == null){
            loginHandler = new LoginHandler(this);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnNewDeviceFoundEvent(OnBluetoothDeviceFoundEvent event){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        if(LoginHandler.fingerPrintIsCompatible) {
            addFingerPrintDialog();
        } else {
            addPinDialog();
        }
    }

    private void addFingerPrintDialog(){
        fingerPrintDialog = new AlertDialog.Builder(this)
                .setTitle("Sign in")
                .setView(R.layout.fingerprint_alertdialog_layout)
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fingerPrintDialog.dismiss();
                        loginHandler.stopListeningForFingerprints();
                        finish();
                    }
                }).show();

        loginHandler.startListeningForFingerprints();
    }

    private void addPinDialog(){
        pinDialog = new AlertDialog.Builder(this)
                .setTitle("Enter PIN")
                .setView(R.layout.pin_alertdialog_layout)
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pinDialog.dismiss();
                        finish();
                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        authorizePin();
                    }
                }).show();
    }

    private void addProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setButton(BUTTON_NEGATIVE, "Cancel",this);
        progressDialog.setMessage("Connecting to Security System");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void authorizePin(){
        EditText edittext = (EditText)pinDialog.findViewById(R.id.pin_bluetooth_auth_edittext);
        String pin = edittext.getText().toString();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String preferredPin = preferences.getString("preferred_pin","");
        if(pin.equals(preferredPin)){
            BluetoothReceiver.writeToStreamAndClose('u');
            if(adapter.isDiscovering()){
                Log.d("Bluetooth Adapter", "canceling discovery");
                adapter.cancelDiscovery();
            }
            finish();
        }else{
            Toast.makeText(this,"WRONG PIN",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(int moduleTag) {
        BluetoothReceiver.writeToStreamAndClose('u');
        if(adapter.isDiscovering()){
            Log.d("Bluetooth Adapter", "canceling discovery");
            adapter.cancelDiscovery();
        }
        finish();
    }

    @Override
    public void onFailure(AuthenticationFailureReason failureReason, boolean fatal, CharSequence errorMessage, int moduleTag, int errorCode) {
        Toast.makeText(this,errorMessage,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_COARSE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //TODO: DO THIS
                Log.d("onRequestPerms", " - starting Bluetooth discovery");
                if(adapter.isDiscovering()){
                    Log.d("Bluetooth Adapter", "canceling discovery");
                    adapter.cancelDiscovery();
                }
                adapter.startDiscovery();
            }
            else{
                Log.d("onRequestPermissions...", "onRequestPermissionsResult: Permission not granted");
            }
        }
    }

    private void checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        } else {
            if(adapter.isDiscovering()){
                Log.d("Bluetooth Adapter", "canceling discovery");
                adapter.cancelDiscovery();
            }
            Log.d("checkLocationPerms", " - starting Bluetooth discovery");
            adapter.startDiscovery();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        loginHandler.addFireBaseAuthListener();

    }

    @Override
    protected void onStop() {
        if(loginHandler != null){
            loginHandler.stopListeningForFingerprints();
            loginHandler.removeFireBaseAuthListener();
        }
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(fingerPrintDialog != null && fingerPrintDialog.isShowing()){
            fingerPrintDialog.dismiss();
        }
        if(pinDialog != null && pinDialog.isShowing()){
            pinDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(adapter.isDiscovering()){
            Log.d("Bluetooth Adapter", "canceling discovery");
            adapter.cancelDiscovery();
        }
        finish();
    }
}
