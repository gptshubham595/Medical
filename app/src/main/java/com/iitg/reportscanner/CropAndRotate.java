package com.iitg.reportscanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iitg.reportscanner.R;
import com.theartofdev.edmodo.cropper.CropImageView;


public class CropAndRotate extends AppCompatActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private Toolbar toolbar;
    private FloatingActionButton mFab;
    public static Bitmap croppedImage;
    private String message;
    CropImageView cropImageView;
    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase,mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_and_rotate);
        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this,R.style.dialog);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewCompat.setElevation(toolbar, 10);
        toolbar.setOnMenuItemClickListener(this);
        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);

        cropImageView.setImageUriAsync(Uri.parse(message));
        mFab = (FloatingActionButton) findViewById(R.id.nextStep);
        mFab.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.nextStep) {
            cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
                @Override
                public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                    croppedImage = result.getBitmap();
                    Intent intent = new Intent(CropAndRotate.this, Binarization.class);
                    startActivity(intent);
                }
            });
            cropImageView.getCroppedImageAsync();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rotate, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rotate_left:
                cropImageView.rotateImage(-90);
                break;
            case R.id.rotate_right:
                cropImageView.rotateImage(90);
                break;
        }
        return false;
    }
}
