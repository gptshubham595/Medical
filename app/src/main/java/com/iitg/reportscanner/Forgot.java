package com.iitg.reportscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.libizo.CustomEditText;

public class Forgot extends AppCompatActivity {
    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase,mDatabase;
    AppCompatButton forgot;
    EditText email,pswd,name,age,cnfpswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forogt);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this,R.style.dialog);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        email=findViewById(R.id.email);

        forgot=findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailis = email.getText().toString();
                if(!TextUtils.isEmpty(emailis)){
                    email.setError(null);
                    mAuth.sendPasswordResetEmail(emailis).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Check Your mail and reset your password",Toast.LENGTH_LONG).show();
                                Intent i=new Intent(getApplicationContext(),Login.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        }
                    });
                }
                else{ email.setError("Enter email !");


                    Toast.makeText(getApplicationContext(),"Please enter a email..",Toast.LENGTH_SHORT).show();}
            }
        });
    }
}
