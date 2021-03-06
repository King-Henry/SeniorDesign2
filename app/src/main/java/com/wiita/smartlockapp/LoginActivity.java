package com.wiita.smartlockapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity implements
AuthenticationListener, TextWatcher, View.OnClickListener{

    private ImageButton fingerPrintButton;
    private LoginHandler loginHandler;
    private AlertDialog alertDialog;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText pinEditText;
    private Button loginButton;
    private ImageButton userpassButton;
    private ImageButton pinButton;
    private CardView userNameEditTextContainer;
    private CardView passwordEditTextContainer;
    private CardView pinEditTextContainer;
    private FrameLayout rootlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rootlayout = (FrameLayout)findViewById(R.id.login_screen_root);
        userNameEditText = (EditText)findViewById(R.id.login_activity_username_edittext);
        passwordEditText = (EditText)findViewById(R.id.login_activity_password_edittext);
        loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setEnabled(false);
        userpassButton = (ImageButton)findViewById(R.id.userpass_button);
        pinButton = (ImageButton)findViewById(R.id.pin_button);
        fingerPrintButton = (ImageButton)findViewById(R.id.fingerprint_button);
        userNameEditTextContainer = (CardView)findViewById(R.id.login_activity_username_cardview_container);
        passwordEditTextContainer = (CardView)findViewById(R.id.login_activity_password_cardview_container);
        pinEditTextContainer = (CardView)findViewById(R.id.login_activity_pin_cardview_container);
        pinEditText = (EditText)findViewById(R.id.login_activity_pin_edittext);

        userpassButton.setOnClickListener(this);
        pinButton.setOnClickListener(this);
        fingerPrintButton.setOnClickListener(this);
        userNameEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);
        pinEditText.addTextChangedListener(this);
        loginButton.setOnClickListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String signInMethod = preferences.getString("preferred_pin","");
        if(signInMethod.length() < 1){
            pinButton.setVisibility(GONE);
        }
        loginHandler = new LoginHandler(this);
        if(LoginHandler.fingerPrintIsCompatible){
            fingerPrintButton.setVisibility(VISIBLE);
            addFingerPrintDialog();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(loginHandler != null) {
            loginHandler.startListeningForFingerprints();
            loginHandler.addFireBaseAuthListener();
        }
    }

    @Override
    protected void onStop() {
        if(loginHandler != null){
            loginHandler.stopListeningForFingerprints();
            loginHandler.removeFireBaseAuthListener();
        }
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        if(alertDialog != null) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        }
        super.onDestroy();
    }


    private void addFingerPrintDialog(){
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Sign in")
                .setView(R.layout.fingerprint_alertdialog_layout)
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        loginHandler.stopListeningForFingerprints();
                    }
                }).show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(activateSignInButton()){
            loginButton.setEnabled(true);
        }else{
            loginButton.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onSuccess(int moduleTag) {
        Toast.makeText(this,"SUCCESS",Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
        navigateToMainActivity();
    }

    private void prepareScreenForPin(){
        userNameEditText.getText().clear();
        passwordEditText.getText().clear();
        userNameEditTextContainer.setVisibility(GONE);
        passwordEditTextContainer.setVisibility(GONE);
        userpassButton.setVisibility(VISIBLE);
        pinEditTextContainer.setVisibility(VISIBLE);
        pinButton.setVisibility(GONE);
    }

    private void prepareScreenForUserPass(){
        pinEditText.getText().clear();
        userNameEditTextContainer.setVisibility(VISIBLE);
        passwordEditTextContainer.setVisibility(VISIBLE);
        userpassButton.setVisibility(GONE);
        pinEditTextContainer.setVisibility(GONE);
        pinButton.setVisibility(VISIBLE);
    }

    @Override
    public void onFailure(AuthenticationFailureReason failureReason, boolean fatal, CharSequence errorMessage, int moduleTag, int errorCode) {
        Toast.makeText(this,failureReason.name(),Toast.LENGTH_SHORT).show();
        Toast.makeText(this,errorMessage,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v == pinButton) {
            prepareScreenForPin();
        } else if(v == fingerPrintButton){
            addFingerPrintDialog();
            loginHandler.startListeningForFingerprints();
        } else if (v == loginButton){
            if(userNameEditTextContainer.getVisibility() == VISIBLE) {
                loginHandler.attemptSignIn(userNameEditText.getText(), passwordEditText.getText());
            }else{
                loginHandler.attemptPinSignIn(pinEditText.getText().toString());
            }
        } else{
            prepareScreenForUserPass();
        }
    }

    private boolean activateSignInButton(){
        return (loginHandler.stringHasValue(userNameEditText.getText().toString()) &&
                loginHandler.stringHasValue(passwordEditText.getText().toString()))
                || loginHandler.stringHasValue(pinEditText.getText().toString());
    }

    public void navigateToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
