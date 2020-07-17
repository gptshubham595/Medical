package com.iitg.reportscanner;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        ProgressDialog mLoginProgress = new ProgressDialog(this, R.style.dialog);
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    }
}
