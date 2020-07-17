package com.iitg.reportscanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

import maes.tech.intentanim.CustomIntent;

public class Login extends AppCompatActivity {
    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    AppCompatButton login;
    EditText email, password;
    private DatabaseReference mUserDatabase;
    private ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this, R.style.dialog);
        loginProgressBar = findViewById(R.id.progressLogin);

        mUserDatabase = FirebaseDatabase.getInstance().getReference();
        mUserDatabase = mUserDatabase.child("Users");

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        login = findViewById(R.id.login);
        login.setOnClickListener(view -> {
            if (TextUtils.isEmpty(email.getText().toString())) email.setError("Enter Email");
            if (TextUtils.isEmpty(password.getText().toString()))
                password.setError("Enter Password");
            if (password.getText().toString().length() < 6) password.setError("Min 6 digits");
            if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString()) && password.getText().toString().length() >= 6) {
                loginProgressBar.setVisibility(View.VISIBLE);
                loginUser(email.getText().toString(), password.getText().toString());
            }
        });

        findViewById(R.id.register).setOnClickListener(v -> {
                    Intent i = new Intent(getApplicationContext(), Register.class);
                    startActivity(i);
                    CustomIntent.customType(Login.this, "fadein-to-fadeout");
                }
        );
        findViewById(R.id.forgot).setOnClickListener(v -> {
                    Intent i = new Intent(getApplicationContext(), Forgot.class);
                    startActivity(i);
                    CustomIntent.customType(Login.this, "fadein-to-fadeout");
                }
        );
    }


    private void loginUser(String email, String password) {
        //   Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
        try {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    mLoginProgress.dismiss();

                    String current_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(aVoid -> {
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            Intent mainIntent = new Intent(getApplicationContext(), MainActivity2.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            CustomIntent.customType(Login.this, "fadein-to-fadeout");
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Verify Email or enter correct details", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                        loginProgressBar.setVisibility(View.INVISIBLE);

                    });


                } else {
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    mLoginProgress.hide();

                    String task_result = Objects.requireNonNull(task.getException()).getMessage();

                    Toast.makeText(getApplicationContext(), "Error : " + task_result, Toast.LENGTH_LONG).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "ERR:" + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser curUser = mAuth.getCurrentUser();
        //If user already logged in send back to Main
        if (curUser != null) {
            sendToMain();
        }

    }

    private void sendToMain() {
        Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
        Intent main = new Intent(Login.this, MainActivity2.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        CustomIntent.customType(Login.this, "fadein-to-fadeout");
    }
}
