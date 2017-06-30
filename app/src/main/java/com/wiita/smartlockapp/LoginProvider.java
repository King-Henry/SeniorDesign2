package com.wiita.smartlockapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.widget.Toast;

import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Wiita on 6/22/2017.
 */

public class LoginProvider implements FirebaseAuth.AuthStateListener,
        OnCompleteListener<AuthResult>{

    private Context context;
    Activity activity;
    public static boolean fingerPrintIsCompatible;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    public LoginProvider(Context context){
        this.context = context;
        Reprint.initialize(context);
        fingerPrintIsCompatible = checkFingerprintCompatibility();
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
    }

    public void startListeningForFingerprints(){
        if(fingerPrintIsCompatible) {
            Reprint.authenticate((AuthenticationListener)context);
        }
    }

    public void stopListeningForFingerprints(){
        if(fingerPrintIsCompatible) {
            Reprint.cancelAuthentication();
        }
    }

    public boolean checkFingerprintCompatibility(){
        return Reprint.hasFingerprintRegistered() && Reprint.isHardwarePresent();
    }

    public boolean stringHasValue(CharSequence charSequence){
        return charSequence != null && charSequence.length() > 0 ;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }

    public void addFireBaseAuthListener(){
        firebaseAuth.addAuthStateListener(this);
    }

    public void removeFireBaseAuthListener(){
        firebaseAuth.removeAuthStateListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful()){
            LoginActivity.navigateToMainActivity(context);
        } else{
            Toast.makeText(context,"WRONG CREDENTIALS",Toast.LENGTH_SHORT).show();
        }
    }

    public void attemptSignIn(Editable username, Editable password){
        firebaseAuth.signInWithEmailAndPassword(username.toString(),password.toString()).addOnCompleteListener((Activity)context,this);
    }

    public void activateSignInButton(){


    }

}
