package com.iitg.reportscanner;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

//import com.google.android.gms.ads.AdView;

public class Recognizer extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    private Toolbar toolbar;
    private EditText search;
    private TextView textView;
    String FINALTOSTORE;
    private String textScanned;
    FloatingActionButton next;
    static boolean check = false;
    ProgressDialog progressCopy, progressOcr;
    TessBaseAPI baseApi;
    AsyncTask<Void, Void, Void> copy = new copyTask();
    AsyncTask<Void, Void, Void> ocr = new ocrTask();
    //private AdView mAdView;
    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase, mDatabase;

    private static final String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.iitg.reportscanner/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recognizer);
        next = findViewById(R.id.next);
        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this, R.style.dialog);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        Toast.makeText(Recognizer.this, "WAIT CHECK IF Texts are Correct", Toast.LENGTH_LONG).show();
        next.setFocusable(false);
        next.setClickable(true);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        ViewCompat.setElevation(toolbar, 10);
        ViewCompat.setElevation(findViewById(R.id.extension), 10);
        textView = findViewById(R.id.textExtracted);
        textView.setMovementMethod(new ScrollingMovementMethod());
        search = findViewById(R.id.search_text);
        // Setting progress dialog for copy job.
        progressCopy = new ProgressDialog(Recognizer.this);
        progressCopy.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressCopy.setIndeterminate(true);
        progressCopy.setCancelable(false);
        progressCopy.setTitle("Dictionaries");
        progressCopy.setMessage("Copying dictionary files");
        // Setting progress dialog for ocr job.
        progressOcr = new ProgressDialog(this);
        progressOcr.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressOcr.setIndeterminate(true);
        progressOcr.setCancelable(false);
        progressOcr.setTitle("OCR");
        progressOcr.setMessage("Extracting text, please wait");
        textScanned = "";

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String ett = search.getText().toString().replaceAll("\n", " ");
                String tvt = textView.getText().toString().replaceAll("\n", " ");

                FINALTOSTORE = textView.getText().toString();

                textView.setText(FINALTOSTORE);


                if (!ett.isEmpty()) {
                    int ofe = tvt.toLowerCase().indexOf(ett.toLowerCase());
                    Spannable WordtoSpan = new SpannableString(textView.getText());
                    for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
                        ofe = tvt.toLowerCase().indexOf(ett.toLowerCase(), ofs);
                        if (ofe == -1)
                            break;
                        else {
                            WordtoSpan.setSpan(new BackgroundColorSpan(ContextCompat.getColor(Recognizer.this, R.color.colorAccent)), ofe, ofe + ett.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            textView.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
                        }

                    }
                }
            }
        });

        copy.execute();
        ocr.execute();


    }


    boolean isdig(char str) {
        return str >= '0' && str <= '9';
    }

    private void sendDataToFirebase(String finaltostore) {

        int f = finaltostore.indexOf("\n");
        int s;
        int count = 0;

        for (int i = 0; i < finaltostore.length(); i++)
            if (finaltostore.charAt(i) == '\n') count++;

        String data;

        finaltostore += "\n";
        HashMap<String, String> userMap = new HashMap<>();

        for (int i = 0; i < count; i++) {
            s = finaltostore.indexOf("\n", f + 1);
            data = finaltostore.substring(f, s);
            f = s;
            try {
                StringBuilder datarev = new StringBuilder(data);
                datarev.reverse().toString();
                int j = 0;
                for (j = 0; j < datarev.toString().length(); j++) {
                    if (isdig(datarev.toString().charAt(j))) {
                        break;
                    }
                }
                for (; j < datarev.toString().length(); j++) {
                    if (datarev.toString().charAt(j) == ' ') {
                        break;
                    }
                }
                String glucose = data.split("[0-9]")[0];
                String glucoseamt = data.substring(glucose.length());
                StringBuilder glu = new StringBuilder(datarev.toString().substring(j));
                glu.reverse().toString().trim();
                final String g = glu.toString().substring(1);
                final String key;
                Log.e("WRITING VALUES", data);
                if (!glucoseamt.equals("")) {
//                Toast.makeText(this, g + " : "+glucoseamt , Toast.LENGTH_SHORT).show();
                    key = g.replaceAll("\\(", "")
                            .replaceAll("\\)", "")
                            .replaceAll(" ", "")
                            .replaceAll(",", "")
                            .replaceAll("\\[", "")
                            .replaceAll("\\]", "")
                            .replaceAll("@", "")
                            .replaceAll("\\{", "")
                            .replaceAll("\\}", "");


                    userMap.put(key.toUpperCase() + "", "" + glucoseamt);

                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed for " + i, Toast.LENGTH_SHORT).show();
            }


        }

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Medicines").child("" + date);
        try {
            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        Toast.makeText(Recognizer.this, "Done", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(Recognizer.this, MainActivity2.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);


                    }

                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void recognizeText() {
        String language = "";
        if (Binarization.language == 0)
            language = "eng";
        else
            language = "spa";

        baseApi = new TessBaseAPI();
        baseApi.init(DATA_PATH, language, TessBaseAPI.OEM_TESSERACT_ONLY);
        baseApi.setImage(Binarization.umbralization);
        textScanned = baseApi.getUTF8Text();

    }


    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("trainneddata");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for (String filename : files) {
            Log.i("files", filename);
            InputStream in = null;
            OutputStream out = null;
            String dirout = DATA_PATH + "tessdata/";
            File outFile = new File(dirout, filename);
            if (!outFile.exists()) {
                try {
                    in = assetManager.open("trainneddata/" + filename);
                    (new File(dirout)).mkdirs();
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (IOException e) {
                    Log.e("tag", "Error creating files", e);
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private class copyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressCopy.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressCopy.cancel();
            try {
                progressOcr.show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Recognizer.this, "Closed", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("CopyTask", "copying..");
            copyAssets();
            return null;
        }
    }

    private class ocrTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressOcr.cancel();
            textView.setText(textScanned);
            next.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendDataToFirebase(textScanned);
                        }
                    }
            );


            check = true;
            next.setFocusable(true);


        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("OCRTask", "extracting..");
            recognizeText();
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copy_text:
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("TextScanner", textView.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Recognizer.this, "Text has been copied to clipboard", Toast.LENGTH_LONG).show();
                break;
            case R.id.new_scan:
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
        }
        return false;
    }
}
