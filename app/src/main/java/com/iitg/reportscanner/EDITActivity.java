package com.iitg.reportscanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EDITActivity extends AppCompatActivity {
    EditText name, age, roll, hostel;
    Button btn;
    CircleImageView pic;
    ImageView edit;
    TextView email;


    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mImageStorage = FirebaseStorage.getInstance().getReference();

        roll = findViewById(R.id.mobile);
        hostel = findViewById(R.id.address);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        email = findViewById(R.id.email);
        btn = findViewById(R.id.btn);
        edit = findViewById(R.id.edit);
        pic = findViewById(R.id.pic);
        edit.setOnClickListener(v -> uploadimg());
        btn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(name.getText().toString().trim())) {
                name.setError("Enter Name!");
            }
            if (TextUtils.isEmpty(age.getText().toString().trim())) {
                age.setError("Enter Age!");
            }
            if (TextUtils.isEmpty(age.getText().toString().trim())) {
                age.setError("Enter Address!");
            }
            if (TextUtils.isEmpty(age.getText().toString().trim())) {
                age.setError("Enter Mobile!");
            }
            if (!TextUtils.isEmpty(name.getText().toString().trim())
                    && !TextUtils.isEmpty(age.getText().toString().trim())) {
                SendNewValues("name");
                SendNewValues("age");
                SendNewValues("address");
                SendNewValues("mobile");
            }
        });
        putall();
        email.setOnClickListener(v -> Toast.makeText(EDITActivity.this, "SORRY YOU CANNOT EDIT EMAIL", Toast.LENGTH_SHORT).show());
    }

    private void putall() {
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        assert current_user != null;
        String uid = current_user.getUid();
        String emailis = current_user.getEmail();
        email.setText(emailis);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String nameis = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                final String image = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                String ageis = Objects.requireNonNull(dataSnapshot.child("age").getValue()).toString();
                final String thumb_image = Objects.requireNonNull(dataSnapshot.child("thumb_image").getValue()).toString();
                String rollis = "default";
                String hostelis = "default";
                try {
                    hostelis = Objects.requireNonNull(dataSnapshot.child("address").getValue()).toString();
                    rollis = Objects.requireNonNull(dataSnapshot.child("mobile").getValue()).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hostel.setText(hostelis);
                roll.setText(rollis);
                name.setText(nameis);
                age.setText(ageis);

                if (!image.equals("default")) {

                    Picasso.get().load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.mipmap.user).into(pic, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(thumb_image).placeholder(R.mipmap.user).into(pic);
                        }

                    });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EDITActivity.this, "Cancelled:" + databaseError, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadimg() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);

            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {


                mProgressDialog = new ProgressDialog(EDITActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                Uri resultUri = result.getUri();

                File thumb_filePath = new File(Objects.requireNonNull(resultUri.getPath()));

                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();


                assert current_user != null;
                final String uid = current_user.getUid();


                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Objects.requireNonNull(thumb_bitmap).compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                StorageReference filepath = mImageStorage.child("profile_images").child(uid + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(uid + ".jpg");


                filepath.putFile(resultUri).addOnCompleteListener(task -> {
                    final String[] download_url = {null};
                    if (task.isSuccessful()) {
                        filepath.getDownloadUrl().addOnSuccessListener(uri -> download_url[0] = String.valueOf(uri));


                        UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                        uploadTask.addOnCompleteListener(thumb_task -> {
                            final String[] thumb_downloadUrl = {null};
                            thumb_filepath.getDownloadUrl().addOnSuccessListener(uri -> thumb_downloadUrl[0] = String.valueOf(uri));

                            Toast.makeText(EDITActivity.this, thumb_downloadUrl[0], Toast.LENGTH_SHORT).show();
                            if (thumb_task.isSuccessful()) {

                                Map<String, Object> update_hashMap = new HashMap<>();
                                update_hashMap.put("image", download_url[0]);
                                update_hashMap.put("thumb_image", thumb_downloadUrl[0]);
                                Toast.makeText(EDITActivity.this, "" + thumb_downloadUrl[0], Toast.LENGTH_SHORT).show();

                                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                mUserDatabase.keepSynced(true);

                                mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(task1 -> {

                                    if (task1.isSuccessful()) {

                                        mProgressDialog.dismiss();
                                        Toast.makeText(EDITActivity.this, "Success Uploading.", Toast.LENGTH_LONG).show();
                                        putall();
                                    }

                                });


                            } else {

                                Toast.makeText(EDITActivity.this, "Error in uploading thumbnail.", Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();

                            }


                        });


                    } else {

                        Toast.makeText(EDITActivity.this, "Error in uploading.", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();

                    }

                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error + "", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void SendNewValues(String str) {

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        assert current_user != null;
        String uid = current_user.getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child(str);
        if (str.equals("name"))
            mDatabase.setValue(name.getText().toString().trim()).addOnCompleteListener(task -> {
                Toast.makeText(EDITActivity.this, "SAVED", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(EDITActivity.this, MainActivity2.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            });
        if (str.equals("age"))
            mDatabase.setValue(age.getText().toString().trim()).addOnCompleteListener(task -> Toast.makeText(EDITActivity.this, "SAVED", Toast.LENGTH_SHORT).show());
        if (str.equals("address"))
            mDatabase.setValue(hostel.getText().toString().trim()).addOnCompleteListener(task -> Toast.makeText(EDITActivity.this, "SAVED", Toast.LENGTH_SHORT).show());
        if (str.equals("mobile"))
            mDatabase.setValue(roll.getText().toString().trim()).addOnCompleteListener(task -> Toast.makeText(EDITActivity.this, "SAVED", Toast.LENGTH_SHORT).show());
    }


}
