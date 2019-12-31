package com.iitg.reportscanner.qr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.iitg.reportscanner.BuildConfig;
import com.iitg.reportscanner.MainActivity2;
import com.iitg.reportscanner.R;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.seismic.ShakeDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GeneratorActivity extends AppCompatActivity implements ShakeDetector.Listener{

    TextView text;
    String qr="",Share="";
    ImageView image;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);
        image = findViewById(R.id.image);
        text=findViewById(R.id.code);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);

        try{qr=getIntent().getExtras().getString("qr");
            Share=getIntent().getExtras().getString("Share");}
        catch (Exception e){
            Toast.makeText(this, "Failed Try again!", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(this,MainActivity2.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }finally {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try{
                BitMatrix bitMatrix = multiFormatWriter.encode(qr, BarcodeFormat.QR_CODE,200,200);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.createBitmap(bitMatrix);
                image.setImageBitmap(bitmap);
                text.setText(Share);
            }
            catch (WriterException e){
                e.printStackTrace();
            }finally{
                sd.start(sensorManager);
            }
        }



    }

    @Override
    public void hearShake() {
        Toast.makeText(this, "Sharing this picture", Toast.LENGTH_SHORT).show();
        shareImageUri(saveImage(bitmap));
    }
    private void shareImageUri(Uri uri){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_TEXT,"CODE = "+Share);
        intent.setType("image/png");
        startActivity(intent);
    }

    private Uri saveImage(Bitmap image) {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.iitg.reportscanner", file);

        } catch (IOException e) {
            Log.d("", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
