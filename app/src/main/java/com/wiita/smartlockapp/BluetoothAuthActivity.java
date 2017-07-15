package com.wiita.smartlockapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;

public class BluetoothAuthActivity extends AppCompatActivity implements AuthenticationListener {

    private LoginProvider loginProvider;
    private AlertDialog fingerPrintDialog;
    private AlertDialog pinDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_auth);
        loginProvider = new LoginProvider(this);
        addPinDialog();
        if(loginProvider == null){
            loginProvider = new LoginProvider(this);
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
                        loginProvider.stopListeningForFingerprints();
                    }
                }).show();
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
                        Log.d("addPinDialog","Submit pressed");
                    }
                }).show();
    }

    @Override
    public void onSuccess(int moduleTag) {
        BluetoothReceiver.writeToStreamAndClose("unlock");
        finish();
    }

    @Override
    public void onFailure(AuthenticationFailureReason failureReason, boolean fatal, CharSequence errorMessage, int moduleTag, int errorCode) {
        Toast.makeText(this,errorMessage,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginProvider.startListeningForFingerprints();
        loginProvider.addFireBaseAuthListener();

    }

    @Override
    protected void onStop() {
        if(loginProvider != null){
            loginProvider.stopListeningForFingerprints();
            loginProvider.removeFireBaseAuthListener();
        }
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
}