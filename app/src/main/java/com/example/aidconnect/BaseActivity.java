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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.widget.Toolbar;

public class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    protected void setupDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        if (drawerLayout != null) {
            toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        } else {
            Log.e("BaseActivity", "DrawerLayout is null! Check your layout file.");
        }

        if (navigationView != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView tvUserName = headerView.findViewById(R.id.tvUserName);
            setUserName(tvUserName);

            navigationView.setNavigationItemSelectedListener(item -> {
                if(item.getItemId() == R.id.nav_home)
                {
                    openHome();
                }
                else if(item.getItemId() == R.id.nav_profile)
                {
                    openProfile();
                }
                else if(item.getItemId() == R.id.nav_donations)
                {
                    openDonations();
                }
                else if(item.getItemId() == R.id.nav_campaigns)
                {
                    openCampaigns();
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

    private void openHome() {
        Intent intent = new Intent(this, CampaignActivity.class);
        startActivity(intent);
    }


    private void setUserName(TextView tvUserName) {
        String currentUserId = auth.getCurrentUser().getUid();

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");

                        // Set the full name in the TextView
                        tvUserName.setText(firstName + " " + lastName);
                    } else {
                        Log.e("BaseActivity", "User document does not exist");
                        tvUserName.setText("Unknown User");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("BaseActivity", "Error fetching user details", e);
                    tvUserName.setText("Error Loading User");
                });
    }

    // Common method to open profile
    private void openProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void openDonations() {
        Intent intent = new Intent(this, MyDonationsActivity.class);
        startActivity(intent);
    }

    private void openCampaigns() {
        Intent intent = new Intent(this, MyCampaignsActivity.class);
        startActivity(intent);
    }

    protected void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, CampaignActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle != null && toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
