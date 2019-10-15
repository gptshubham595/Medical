package com.iitg.reportscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.libizo.CustomEditText;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase,mDatabase;
    AppCompatButton login,register;
    EditText email,pswd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this,R.style.dialog);

        mUserDatabase = FirebaseDatabase.getInstance().getReference();
        mUserDatabase=mUserDatabase.child("Users");

        email=findViewById(R.id.email);
        pswd=findViewById(R.id.password);

        login=findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(email.getText().toString(),pswd.getText().toString());
            }
        });

        findViewById(R.id.register).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getApplicationContext(), Register.class);
                        startActivity(i);
                    }
                }
        );
        findViewById(R.id.forgot).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getApplicationContext(), Forgot.class);
                        startActivity(i);
                    }
                }
        );
    }


    private void loginUser(String email, String password) {
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
        try {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        mLoginProgress.dismiss();

                        String current_user_id = mAuth.getCurrentUser().getUid();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity2.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please Verify Email or enter correct details", Toast.LENGTH_SHORT).show();
                                }
                                finish();


                            }
                        });


                    } else {

                        mLoginProgress.hide();

                        String task_result = task.getException().getMessage().toString();

                        Toast.makeText(getApplicationContext(), "Error : " + task_result, Toast.LENGTH_LONG).show();

                    }

                }
            });
        }catch (Exception e){e.printStackTrace();}
    }

}
