package com.example.aidconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.widget.Toolbar;

public class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Set up the drawer and common actions for profile, settings, and logout
    protected void setupDrawer() {
        // Find the toolbar and set it as the action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // This sets the toolbar as the ActionBar

        // Now get the ActionBar (after setting the toolbar) and enable the home button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the back/home button
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Set up the drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        if (drawerLayout != null) {
            toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        } else {
            Log.e("BaseActivity", "DrawerLayout is null! Check your layout file.");
        }

        // Handle navigation item clicks (profile, settings, logout)
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                if(item.getItemId() == R.id.nav_profile)
                {
                    openProfile();
                }
                else if(item.getItemId() == R.id.nav_settings)
                {
                    openSettings();
                }
                else if(item.getItemId() == R.id.nav_logout)
                {
                    logout();
                }
                drawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            });
        }
    }

    // Common method to open profile
    protected void openProfile() {
        // Handle the profile logic here, e.g., start the ProfileActivity
        //Intent intent = new Intent(this, ProfileActivity.class);
        //startActivity(intent);
    }

    // Common method to open settings
    protected void openSettings() {
        // Handle the settings logic here, e.g., start the SettingsActivity
        //Intent intent = new Intent(this, SettingsActivity.class);
        //startActivity(intent);
    }

    // Common method to handle logout
    protected void logout() {
        // Handle the logout logic, e.g., FirebaseAuth.getInstance().signOut();
        // Redirect to login activity
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, CampaignActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Handle the ActionBar toggle
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle != null && toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
