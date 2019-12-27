package com.iitg.reportscanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iitg.reportscanner.spla.Splash1;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private ProgressDialog mLoginProgress;
    GraphView graph;
    private FirebaseAuth mAuth;
    Spinner spinner;
    private DatabaseReference mUserDatabase, mDatabase;
    TextView emailheader, ageheader, nameheader;
    ImageView edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this, R.style.dialog);
        spinner = findViewById(R.id.spinner);
        graph = findViewById(R.id.graph);
        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

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

        final ArrayList<String> arrayList = new ArrayList<>();
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Medicines");


        //GET ALL DATES
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

//                    Toast.makeText(MainActivity2.this, "DATES:"+dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();

                    for (DataSnapshot childDataSnapshot2 : childDataSnapshot.getChildren()) {
//                        Toast.makeText(MainActivity2.this, "MEDICINE NAME"+childDataSnapshot2.getKey(), Toast.LENGTH_SHORT).show();
                        arrayList.add(childDataSnapshot2.getKey());
//                        Toast.makeText(MainActivity2.this, "VALUES"+childDataSnapshot2.getValue(), Toast.LENGTH_SHORT).show();
                    }
                    }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String key = parent.getItemAtPosition(position).toString();
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                        new DataPoint(0, 1)
                });
                graph.addSeries(series);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
