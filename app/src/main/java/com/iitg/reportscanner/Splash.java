package com.iitg.reportscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iitg.reportscanner.spla.Splash1;

public class Splash extends AppCompatActivity {
    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;
    SharedPreferences prefs=null;
    Boolean isFirstRun=false;
    private DatabaseReference mUserDatabase,mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);
        prefs= getSharedPreferences("com.iitg.reportscanner", MODE_PRIVATE);

        isFirstRun=prefs.getBoolean("isFirstRun", true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isFirstRun){
                    Toast.makeText(Splash.this, "First time", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(getApplicationContext(), Splash1.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                 }else{
                Intent i=new Intent(getApplicationContext(), Login.class);
              //  Intent i=new Intent(getApplicationContext(), Splash1.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }}
        }, 5000);
    }
}
