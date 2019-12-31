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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import maes.tech.intentanim.CustomIntent;

public class Register extends AppCompatActivity {
    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase, mDatabase;
    AppCompatButton register;
    EditText email, pswd, name, age, cnfpswd;
    private ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this, R.style.dialog);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        loginProgressBar = findViewById(R.id.progressLogin);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        pswd = findViewById(R.id.password);
        age = findViewById(R.id.age);
        cnfpswd = findViewById(R.id.confirmpassword);

        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(Register.this, "Enter mail id", Toast.LENGTH_SHORT).show();
                    email.setError("Enter email");
                } else if (TextUtils.isEmpty(name.getText().toString())) {
                    Toast.makeText(Register.this, "Enter name", Toast.LENGTH_SHORT).show();
                    name.setError("Enter name!!");
                } else if (TextUtils.isEmpty(age.getText().toString())) {
                    Toast.makeText(Register.this, "Enter age", Toast.LENGTH_SHORT).show();
                    age.setError("Enter age");
                } else if (pswd.getText().toString().length() < 6) {
                    Toast.makeText(Register.this, "Min 6 digit", Toast.LENGTH_SHORT).show();
                    pswd.setError("Min 6 digit");
                } else if (TextUtils.isEmpty(pswd.getText().toString())) {
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                    pswd.setError("Enter password");
                } else if (TextUtils.isEmpty(cnfpswd.getText().toString())) {
                    Toast.makeText(Register.this, "Enter confirmation password", Toast.LENGTH_SHORT).show();
                    cnfpswd.setError("Enter Confirmation Password");
                } else if (!pswd.getText().toString().equals(cnfpswd.getText().toString())) {
                    cnfpswd.setError("Password not matched!!");
                    Toast.makeText(Register.this, "Password not matched", Toast.LENGTH_SHORT).show();

                } else {
                    loginProgressBar.setVisibility(View.VISIBLE);
                    register_user(email.getText().toString(), pswd.getText().toString(), name.getText().toString(), age.getText().toString());
                    cnfpswd.setError(null);
                    pswd.setError(null);
                    email.setError(null);
                    age.setError(null);
                    name.setError(null);


                }
            }
        });


    }

    private void register_user(String email, String password, final String name, final String age) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {


                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("status", "Hi there I'm using iReport App.");
                    userMap.put("image", "default");
                    userMap.put("age", "" + age);
                    userMap.put("address", "default");
                    userMap.put("mobile", "default");
                    userMap.put("thumb_image", "default");
                    userMap.put("device_token", device_token);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                mLoginProgress.dismiss();
                                sendEmailVerification();
                                loginProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Registered!! Please verify Email Confirmation and Login", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(), Login.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                CustomIntent.customType(Register.this, "fadein-to-fadeout");

                            }

                        }
                    });


                } else {

                    mLoginProgress.hide();
                    Toast.makeText(getApplicationContext(), "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();
                    loginProgressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

    }


    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Check your Email", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR OCCURED TRY AGAIN!!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

}
