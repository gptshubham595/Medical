package com.iitg.reportscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Analyzer extends AppCompatActivity {
    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase, mDatabase;
TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyzer);
        String text = getIntent().getExtras().get("ocr").toString().toLowerCase();
        txt=findViewById(R.id.text);
        mAuth =FirebaseAuth.getInstance();
        mLoginProgress =new ProgressDialog(this,R.style.dialog);

        mUserDatabase =FirebaseDatabase.getInstance().
                getReference().
                child("Users");

        try{ int index=0;
            for(int i=0;i<text.length();i++)
            {
                if(Character.isDigit(text.charAt(i)))
                {index=i;
                    int first = text.indexOf(i);
                    int till = text.indexOf(0, first+3);
                    String name = text.substring(first , till);
                    txt.setText(name);
                    Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
                    break;
                }
            }


        }catch (Exception e){e.printStackTrace();}






    }


}
