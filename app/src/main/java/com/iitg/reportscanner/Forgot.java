package com.iitg.reportscanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import maes.tech.intentanim.CustomIntent;

public class Forgot extends AppCompatActivity {
    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;
    private ProgressBar loginProgressBar;
    private DatabaseReference mUserDatabase, mDatabase;
    AppCompatButton forgot;
    EditText email, pswd, name, age, cnfpswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forogt);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this, R.style.dialog);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        loginProgressBar = findViewById(R.id.progressLogin);
        email = findViewById(R.id.email);

        forgot = findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgressBar.setVisibility(View.VISIBLE);
                String emailis = email.getText().toString();
                if (!TextUtils.isEmpty(emailis)) {
                    email.setError(null);
                    loginProgressBar.setVisibility(View.VISIBLE);
                    mAuth.sendPasswordResetEmail(emailis).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loginProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Check Your mail and reset your password", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(), Login.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                CustomIntent.customType(Forgot.this, "fadein-to-fadeout");
                            }
                        }
                    });
                } else {
                    email.setError("Enter email !");
                    loginProgressBar.setVisibility(View.INVISIBLE);

                    Toast.makeText(getApplicationContext(), "Please enter a email..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
