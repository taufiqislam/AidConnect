package com.example.aidconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CampaignActivity extends BaseActivity {

    TabLayout tabLayout;
    RecyclerView rvCampaigns;
    CampaignAdapter campaignAdapter;
    List<Campaign> allCampaigns = new ArrayList<>();
    List<Campaign> filteredCampaigns = new ArrayList<>();
    FirebaseFirestore db;
    List<String> campaignIds = new ArrayList<>();
    View underlineIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            setupDrawer();
        }

        db = FirebaseFirestore.getInstance();

        tabLayout = findViewById(R.id.tabLayout);
        rvCampaigns = findViewById(R.id.rvCampaigns);
        rvCampaigns.setLayoutManager(new LinearLayoutManager(this));

        fetchCampaignsFromFirestore();


        tabLayout.addTab(tabLayout.newTab().setText("Popular"));
        tabLayout.addTab(tabLayout.newTab().setText("Newly Added"));
        tabLayout.addTab(tabLayout.newTab().setText("Ending Soon"));

        // Set up tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        sortCampaignsByDonorCount();
                        break;
                    case 1:
                        sortCampaignsByCreationDate();
                        break;
                    case 2:
                        sortCampaignsByDeadline();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        Button signInButton = findViewById(R.id.btnSignIn);
        if (currentUser != null) {
            signInButton.setVisibility(View.GONE);
        } else {
            signInButton.setVisibility(View.VISIBLE);
            signInButton.setOnClickListener(v -> {
                Intent intent = new Intent(CampaignActivity.this, LoginActivity.class);
                startActivity(intent);
            });
        }

        FloatingActionButton addCampaignButton = findViewById(R.id.addCampaignButton);
        addCampaignButton.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                // User is logged in, proceed to CreateCampaignActivity
                Intent intent = new Intent(CampaignActivity.this, CreateCampaignActivity.class);
                startActivity(intent);
            } else {
                // User is not logged in, redirect to LoginActivity
                Intent intent = new Intent(CampaignActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setBackgroundColor(getResources().getColor(R.color.search_color));
//        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
//        searchEditText.setTextColor(getResources().getColor(R.color.text_color));
//        searchEditText.setHintTextColor(getResources().getColor(R.color.text_color));
//        ImageView searchCloseButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
//        searchCloseButton.setColorFilter(getResources().getColor(R.color.text_color));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCampaigns(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    sortCampaignsByDonorCount();
                } else {
                    filterCampaigns(newText);
                }
                return false;
            }
        });

        return true;
    }

    private void filterCampaigns(String query) {
        filteredCampaigns.clear();
        if (query.isEmpty()) {
            filteredCampaigns.addAll(allCampaigns);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Campaign campaign : allCampaigns) {
                if (campaign.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        campaign.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredCampaigns.add(campaign);
                }
            }
        }
        campaignAdapter.notifyDataSetChanged();
    }

    private void fetchCampaignsFromFirestore() {
        CollectionReference campaignsRef = db.collection("campaigns");

        campaignsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                allCampaigns.clear();
                Date currentDate = new Date();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Campaign campaign = document.toObject(Campaign.class);
                    if (campaign.getCampaignDeadline().after(currentDate)) {
                        allCampaigns.add(campaign);
                    }
                }

                filteredCampaigns.addAll(allCampaigns);
                campaignAdapter = new CampaignAdapter(filteredCampaigns, this);
                rvCampaigns.setAdapter(campaignAdapter);
                sortCampaignsByDonorCount();
            } else {
                Toast.makeText(CampaignActivity.this, "Error getting campaigns", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortCampaignsByDonorCount() {
        filteredCampaigns.clear();
        filteredCampaigns.addAll(allCampaigns);
        Collections.sort(filteredCampaigns, (c1, c2) -> Integer.compare(c2.getDonorCount(), c1.getDonorCount()));
        campaignAdapter.notifyDataSetChanged();
    }

    // Sorting method for newly added campaigns (by creation date)
    private void sortCampaignsByCreationDate() {
        filteredCampaigns.clear();
        filteredCampaigns.addAll(allCampaigns);
        Collections.sort(filteredCampaigns, (c1, c2) -> c2.getCampaignCreationDate().compareTo(c1.getCampaignCreationDate()));
        campaignAdapter.notifyDataSetChanged();
    }

    // Sorting method for ending soon campaigns (by deadline)
    private void sortCampaignsByDeadline() {
        filteredCampaigns.clear();
        filteredCampaigns.addAll(allCampaigns);
        Collections.sort(filteredCampaigns, (c1, c2) -> c1.getCampaignDeadline().compareTo(c2.getCampaignDeadline()));
        campaignAdapter.notifyDataSetChanged();
    }
}
