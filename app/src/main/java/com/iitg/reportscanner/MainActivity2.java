package com.iitg.reportscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iitg.reportscanner.qr.GeneratorActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;


public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    SwipeRefreshLayout swipe;
    private ProgressDialog mLoginProgress;
    GraphView graph;
    private FirebaseAuth mAuth;
    NiceSpinner spinner;
    private DatabaseReference mUserDatabase, mDatabase,mdatakey;
    TextView emailheader, ageheader, nameheader;
    CircleImageView imageheader;
    ImageView edit;
    ArrayList<String> arrayListGLOB=new ArrayList<>();
    TextView units;
    final HashMap<String, Vector<Double>> Med = new HashMap<>();
    List<String> arrayList = new ArrayList<>();
    List<String> arrayDate = new ArrayList<>();
    List<String> array=new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mAuth = FirebaseAuth.getInstance();
        swipe=findViewById(R.id.swipe);
        mLoginProgress = new ProgressDialog(this, R.style.dialog);
        spinner = findViewById(R.id.spinner);
        graph = findViewById(R.id.graph);
        graph.setTitle("Chart");
        graph.getGridLabelRenderer().setVerticalAxisTitle("units");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("DAYS");

        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        units=findViewById(R.id.units);
        navigationView = findViewById(R.id.nav);
        navigationView.setNavigationItemSelectedListener(this);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View headerView = navigationView.getHeaderView(0);
        nameheader = headerView.findViewById(R.id.name);
        emailheader = headerView.findViewById(R.id.email);
        ageheader = headerView.findViewById(R.id.age);
        imageheader=headerView.findViewById(R.id.profile_pic);
        edit = headerView.findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),EDITActivity.class);
                startActivity(i);
            }
        });
        //Also Email
        putValues("name");
        putValues("age");
        putValues("thumb_image");

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Medicines");



        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    arrayDate.add(""+childDataSnapshot.getKey().toUpperCase());
                    for (DataSnapshot childDataSnapshot2 : childDataSnapshot.getChildren()) {
                        // "MEDICINE NAME" childDataSnapshot2.getKey()

                        Vector<Double> vector;

                        if(Med.containsKey(childDataSnapshot2.getKey().toUpperCase())) {
                            vector = Med.get(childDataSnapshot2.getKey().toUpperCase());
                        } else {
                            vector = new Vector();
                            Med.put(childDataSnapshot2.getKey().toUpperCase(), vector);
                        }

                        int space=childDataSnapshot2.getValue().toString().indexOf(" ");

                        array.add(childDataSnapshot2.getValue().toString().substring(space));

                        String doubl=childDataSnapshot2.getValue().toString().substring(0,space)
                                .replaceAll("-",".")
                                .replaceAll("_",".")
                                .replaceAll(",",".")
                                .replaceAll("\'",".")
                                .replaceAll("[a-z]","")
                                .replaceAll("[A-Z]","")
                                .replaceAll("\\(", ".")
                                .replaceAll("\\)", ".")
                                .replaceAll(" ", ".")
                                .replaceAll(",", ".")
                                .replaceAll("\\[", ".")
                                .replaceAll("\\]", ".")
                                .replaceAll("@", ".")
                                .replaceAll("‘", "")
                                .replaceAll("\\{", ".")
                                .replaceAll("\\}", ".");
                        Double dou=0.0;
                        try{ dou=Double.parseDouble(doubl);}catch (Exception e){
                            Toast.makeText(MainActivity2.this, "Unable", Toast.LENGTH_SHORT).show();
                        }finally {
                            vector.add(dou);
                        }

                    }
                }

                //Here Med contains all the data
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Vector<Vector<Vector<Double>>> vectorall = new Vector<>();

        Vector<Vector<Double>> vector=new Vector<>();


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                vector.clear();
                arrayList.clear();
                vectorall.clear();

                for(Map.Entry<String,Vector<Double>> entry : Med.entrySet()) {
                    arrayList.add(entry.getKey());
                    vector.add(entry.getValue());
                }
                vectorall.add(vector);

                spinner.attachDataSource(arrayList);

                if (swipe.isRefreshing()) {
                    swipe.setRefreshing(false);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vector.clear();
                arrayList.clear();
                vectorall.clear();

                for(Map.Entry<String,Vector<Double>> entry : Med.entrySet()) {
                    arrayList.add(entry.getKey());
                    vector.add(entry.getValue());
                }
                vectorall.add(vector);

                spinner.attachDataSource(arrayList);

            }
        },3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vector.clear();
                arrayList.clear();
                vectorall.clear();

                for(Map.Entry<String,Vector<Double>> entry : Med.entrySet()) {
                    arrayList.add(entry.getKey());
                    vector.add(entry.getValue());
                }
                vectorall.add(vector);

                spinner.attachDataSource(arrayList);

            }
        },6000);
        spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                graph.removeAllSeries();

                try{
                ((TextView) view).setTextColor(Color.RED);
//                units.setText("( "+array.get(position)+" )");
                String item = parent.getItemAtPosition(position).toString();

                Toast.makeText(MainActivity2.this, item + ""
                        + vector.get(position), Toast.LENGTH_SHORT).show();

                DataPoint[] dataPoints = new DataPoint[vector.get(position).size()];
                for (int i = 0; i < vector.get(position).size(); i++) {
                    dataPoints[i] = new DataPoint(i, vector.get(position).get(i));
                }

                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints); // This one should be obvious right? :)
                Double dou = Collections.max(vector.get(position));

                graph.getViewport().setMinX(0);
                graph.getViewport().setMaxX(vector.get(position).size() + 10);
                graph.getViewport().setMinY(0.0);
                graph.getViewport().setMaxY(dou + 15.0);
                graph.getViewport().setScrollable(true); // enables horizontal scrolling
                graph.getViewport().setScrollableY(true); // enables vertical scrolling
                graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
                graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

                graph.setTitle("Chart");
                graph.getGridLabelRenderer().setVerticalAxisTitle("( " + array.get(position) + " )");
                graph.getGridLabelRenderer().setHorizontalAxisTitle("DAYS");

                series.setDrawBackground(true);
                series.setDrawDataPoints(true);
                series.setDataPointsRadius(10);
                series.setThickness(2);
                series.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        if (dataPoint.getX() == 1)
                            Toast.makeText(MainActivity2.this, "1st Day" + " , " + dataPoint.getY() + " " + array.get(position), Toast.LENGTH_SHORT).show();
                        else if (dataPoint.getX() == 2)
                            Toast.makeText(MainActivity2.this, "2nd Day" + " , " + dataPoint.getY() + " " + array.get(position), Toast.LENGTH_SHORT).show();
                        else if (dataPoint.getX() == 3)
                            Toast.makeText(MainActivity2.this, "3rd Day" + " , " + dataPoint.getY() + " " + array.get(position), Toast.LENGTH_SHORT).show();
                        else {
                            int x=(int) dataPoint.getX();
                            Toast.makeText(MainActivity2.this, "" + x + "th Day" + " , " + dataPoint.getY() + " " + array.get(position), Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(MainActivity2.this, "" + arrayDate.get((int)dataPoint.getX()) + " , " + dataPoint.getY() + " " + array.get(position), Toast.LENGTH_SHORT).show();

                    }
                });
                series.setColor(Color.parseColor("#4fc9dd"));
                series.setBackgroundColor(Color.parseColor("#154fc9dd"));


                graph.addSeries(series);
            }catch (Exception e){e.printStackTrace();
                    Toast.makeText(MainActivity2.this, "Failed for "+arrayList.get(position), Toast.LENGTH_SHORT).show();}
            }
        });

    }


    private void putValues(final String name) {
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        String email = current_user.getEmail();
        emailheader.setText(email);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child(name);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (name.equals("name")) {
                    nameheader.setText(value);
                }
                if(name.equals("age"))
                {
                    ageheader.setText("Age : " + value + " Y");
                }
                if(name.equals("thumb_image")){
                    if(!value.equals("default")) {

                        Picasso.get().load(value).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.mipmap.user).into(imageheader, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(value).placeholder(R.mipmap.user).into(imageheader);
                            }

                        });

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menulog, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {

            case R.id.action_logout: {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(this, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                CustomIntent.customType(MainActivity2.this, "fadein-to-fadeout");
            }
            return true;
            case R.id.action_upload:
                startActivity(new Intent(this, MainActivity.class));
                CustomIntent.customType(MainActivity2.this, "fadein-to-fadeout");
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_dashboard:
                return true;

            case R.id.action_export:
                String output = MapUtils.mapToString(Med)+"[[DATES="+arrayDate.toString()+"]]"+"[[UNITS="+array.toString()+"]]";
                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = current_user.getUid();
                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Share");
                uid=uid.substring(0,6);
                String finalUid1 = uid;
                mUserDatabase.setValue(finalUid1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(MainActivity2.this, "Done", Toast.LENGTH_SHORT).show();
                    }
                });
                mdatakey = FirebaseDatabase.getInstance().getReference().child("Users").child("Share").child(uid);
                String finalUid = uid;
                mdatakey.setValue(output).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
//                            Toast.makeText(MainActivity2.this, output, Toast.LENGTH_SHORT).show();
                            Intent i =new Intent(MainActivity2.this, GeneratorActivity.class);
                            i.putExtra("qr",output);
                            i.putExtra("Share", finalUid);
                            startActivity(i);
                        }else{
                            Toast.makeText(MainActivity2.this, "Failed to export", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                 return true;

            case R.id.action_import:
//                Intent i=new Intent(this, ReaderActivity.class);
//                startActivity(i);
                drawerLayout.closeDrawer(Gravity.LEFT);
                showDialog(this);
                return true;

            case R.id.action_shareapp:
                shareapp();
                return true;

            case R.id.action_help:
                startActivity(new Intent(this, Help.class));
                CustomIntent.customType(MainActivity2.this, "fadein-to-fadeout");
                return true;

            case R.id.action_about:
                startActivity(new Intent(this, About.class));
                CustomIntent.customType(MainActivity2.this, "fadein-to-fadeout");
                return true;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareapp() {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
            File srcFile = new File(ai.publicSourceDir);
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/vnd.android.package-archive");
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(srcFile));
            startActivity(Intent.createChooser(share, "iReport"));
        } catch (Exception e) {
            Log.e("ShareApp", e.getMessage());
        }
        }

    private void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_scan_import);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button scan=dialog.findViewById(R.id.scan);
        Button codecheck=dialog.findViewById(R.id.codecheck);
        EditText qrcode=dialog.findViewById(R.id.qrcode);
        dialog.show();

        codecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=qrcode.getText().toString().trim();

                mdatakey = FirebaseDatabase.getInstance().getReference().child("Users").child("Share");
                mdatakey.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                            if(code.equals(childDataSnapshot.getKey().trim())) {

                                Toast.makeText(activity, "FOUND", Toast.LENGTH_SHORT).show();
                                String value=childDataSnapshot.getValue(String.class);
                                int dates=0,units=0;
                                try{dates=value.indexOf("[[DATES=");
                                units=value.indexOf("[[UNITS=");}catch (Exception e){
                                    Toast.makeText(activity, "FAILED at DATES AND UNITS", Toast.LENGTH_SHORT).show();
                                }

                                String[] keyValuePairs = value.substring(0,dates).split("&");
                                String[] keyDATEPairs = value.substring(dates+1,units-1).split(",");
                                String[] keyUNITPairs = value.substring(units+1,value.length()-1).split(",");

                                for(int i=0;i<keyValuePairs.length;i++)
                                {   int eq=keyValuePairs[i].indexOf("=");
                                    String medicine=keyValuePairs[i].substring(0,eq);
                                    String encodedvector=keyValuePairs[i].substring(eq+1);
                                    String decodedvector="";
                                    try{
                                        //[12,10,15]
                                        decodedvector= URLDecoder.decode(encodedvector, StandardCharsets.UTF_8.name());
                                    }
                                    catch (Exception e){e.printStackTrace();}
                                    finally{
                                        HashMap<String ,String> MedImportMap=new HashMap<>();
                                    //I HAVE TO ADD CODE FOR ADDING IT AS STRING WITH
                                        //MedImportMap.put("medicine","VALUE");
                                    }

                                }

                            break;
                            }
                        }
                        //Here Med contains all the data
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent i = new Intent(MainActivity2.this, QrCodeActivity.class);
                startActivityForResult( i,101);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Log.d("", "COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity2.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if (requestCode == 101) {
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d("QR", "Have scan result in your app activity :" + result);
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity2.this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        }
    }
}
