package com.iitg.reportscanner.qr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.iitg.reportscanner.MainActivity2;
import com.iitg.reportscanner.R;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GeneratorActivity extends AppCompatActivity {
    TextView text;
    Button gen_btn;
    ImageView image;
    String text2Qr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);
        image = findViewById(R.id.image);
        text=findViewById(R.id.code);
        String qr="",Share="";
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
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                image.setImageBitmap(bitmap);
                text.setText(Share);
            }
            catch (WriterException e){
                e.printStackTrace();
            }
        }



    }
}
