package com.iitg.reportscanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
    private DatabaseReference mUserDatabase, mDatabase;
    TextView emailheader, ageheader, nameheader;
    ImageView edit;
    ArrayList<String> arrayListGLOB=new ArrayList<>();
    TextView units;

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
        edit = headerView.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),EditAcitivity.class);
                startActivity(i);
            }
        });
        //Also Email
        putValues("name");
        putValues("age");

        List<String> arrayList = new ArrayList<>();
        List<String> arrayDate = new ArrayList<>();
        List<String> array=new Vector<>();
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Medicines");

        final HashMap<String, Vector<Double>> Med = new HashMap<>();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    arrayDate.add(""+childDataSnapshot.getKey());
                    for (DataSnapshot childDataSnapshot2 : childDataSnapshot.getChildren()) {
                        // "MEDICINE NAME" childDataSnapshot2.getKey()

                        Vector<Double> vector;

                        if(Med.containsKey(childDataSnapshot2.getKey())) {
                            vector = Med.get(childDataSnapshot2.getKey());
                        } else {
                            vector = new Vector();
                            Med.put(childDataSnapshot2.getKey(), vector);
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
                        Toast.makeText(MainActivity2.this, "" + arrayDate.get(position) + " , " + dataPoint.getY() + " " + array.get(position), Toast.LENGTH_SHORT).show();

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
                } else {
                    ageheader.setText("Age : " + value + " Y");
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
                return true;

            case R.id.action_share:
                return true;

            case R.id.action_contactus:
                startActivity(new Intent(this, Contact.class));
                CustomIntent.customType(MainActivity2.this, "fadein-to-fadeout");
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
}
