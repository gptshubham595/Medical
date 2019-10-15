package com.iitg.reportscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity2 extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase,mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this,R.style.dialog);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        navigationView = findViewById(R.id.nav);
        navigationView.setNavigationItemSelectedListener(this);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menulog, menu);


            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            if(toggle.onOptionsItemSelected(item)){
                return true;
            }
            switch (item.getItemId()){

                case R.id.action_logout:{
                    FirebaseAuth.getInstance().signOut();
                    Intent i=new Intent(this, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } return true;
                case R.id.action_upload:startActivity(new Intent(this, MainActivity.class)); return true;

            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.action_dashboard: return true;

                case R.id.action_export: return true;

                case R.id.action_share:return true;

                case R.id.action_contactus:startActivity(new Intent(this, Contact.class)); return true;

                case R.id.action_help:startActivity(new Intent(this, Help.class)); return true;

                case R.id.action_about:startActivity(new Intent(this, About.class)); return true;

            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    }
