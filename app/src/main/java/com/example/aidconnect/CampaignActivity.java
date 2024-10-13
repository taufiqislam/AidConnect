package com.example.aidconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class CampaignActivity extends BaseActivity {

    TabLayout tabLayout;
    RecyclerView rvCampaigns;
    CampaignAdapter campaignAdapter;
    List<Campaign> allCampaigns = new ArrayList<>();
    List<Campaign> filteredCampaigns = new ArrayList<>();
    List<String> campaignIds = new ArrayList<>();
    TextView currentSelectedFilter;
    View underlineIndicator;
    String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            setupDrawer();
        }



        tabLayout = findViewById(R.id.tabLayout);
        rvCampaigns = findViewById(R.id.rvCampaigns);
        rvCampaigns.setLayoutManager(new LinearLayoutManager(this));

        // Create a Calendar instance to set campaign creation date and deadline
        Calendar calendar = Calendar.getInstance();

        // Set campaign creation date (e.g., today)
        Date creationDate = calendar.getTime();

        // Set campaign deadline (e.g., 30 days from today)
        calendar.add(Calendar.DAY_OF_YEAR, 30);
        Date deadline = calendar.getTime();


        // Add a campaign with updated constructor
        allCampaigns.add(new Campaign(
                "Build School",               // title
                "Help build a school in rural areas", // description
                creationDate,                 // campaign creation date
                deadline,                     // campaign deadline
                100000,                       // donation target (in currency units)
                R.drawable.school,            // image resource ID
                "Newly Added",                    // category (e.g., Popular, Urgency, etc.)
                "w8gxD3Qpi0acrZkdSOCozk1aoaN2" // creator ID (user ID)
        ));
        campaignIds.add("number1");

        // Add another campaign (dummy data)
        calendar = Calendar.getInstance();  // Reset calendar for new date
        creationDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 15);  // Campaign ends in 15 days
        deadline = calendar.getTime();

        allCampaigns.add(new Campaign(
                "Flood at Feni",     // title
                "Save the people of Feni from massive flooding", // description
                creationDate,                 // campaign creation date
                deadline,                     // campaign deadline
                50000,                        // donation target
                R.drawable.flood,             // image resource ID
                "Popular",                    // category
                "w8gxD3Qpi0acrZkdSOCozk1aoaN2" // creator ID (same user)
        ));
        campaignIds.add("number2");

        // Set campaign creation date (e.g., 7 days ago for "Forest Revival")
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);  // Campaign started 7 days ago
        Date forestCreationDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 7);   // Campaign will end in 7 days
        Date forestDeadline = calendar.getTime();

// Add "Forest Revival" campaign with updated constructor
        allCampaigns.add(new Campaign(
                "Forest Revival",                 // title
                "Help restore deforested lands in critical regions", // description
                forestCreationDate,               // campaign creation date
                forestDeadline,                   // campaign deadline (7 days left)
                200000,                           // donation target
                R.drawable.forest,                // image resource ID
                "Newly Added",                        // category
                "w8gxD3Qpi0acrZkdSOCozk1aoaN2"    // creator ID
        ));
        campaignIds.add("number3");

// Set campaign creation date (e.g., 27 days ago for "Save Hameem")
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -27);  // Campaign started 27 days ago
        Date hameemCreationDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 3);    // Campaign will end in 3 days
        Date hameemDeadline = calendar.getTime();

// Add "Save Hameem" campaign with updated constructor
        allCampaigns.add(new Campaign(
                "Save Hameem",                    // title
                "Raise funds for Hameem's cancer treatment", // description
                hameemCreationDate,               // campaign creation date
                hameemDeadline,                   // campaign deadline (3 days left)
                150000,                           // donation target
                R.drawable.cancer,                // image resource ID
                "Ending Soon",                    // category
                "w8gxD3Qpi0acrZkdSOCozk1aoaN2"    // creator ID
        ));
        campaignIds.add("number4");


        // Initially show all campaigns
        filteredCampaigns.addAll(allCampaigns);
        campaignAdapter = new CampaignAdapter(filteredCampaigns,campaignIds,this);
        rvCampaigns.setAdapter(campaignAdapter);

        // Add tabs to the TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Newly Added"));
        tabLayout.addTab(tabLayout.newTab().setText("Popular"));
        tabLayout.addTab(tabLayout.newTab().setText("Ending Soon"));

        // Set up tab selection listener
        // Set up tab selection listener with sorting algorithms
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        // Newly Added: Sort by creation date (most recent first)
                        sortCampaignsByCreationDate();
                        break;
                    case 1:
                        // Popular: Sort by donor count (descending)
                        sortCampaignsByDonorCount();
                        break;
                    case 2:
                        // Ending Soon: Sort by deadline (ascending)
                        sortCampaignsByDeadline();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });



        Button signInButton = findViewById(R.id.btnSignIn);


        if (currentUser != null) {
            // User is logged in, hide the sign in button
            signInButton.setVisibility(View.GONE);
        } else {
            // User is not logged in, show the sign in button
            signInButton.setVisibility(View.VISIBLE);

            // Set up the Sign In button click listener
            signInButton.setOnClickListener(v -> {
                Intent intent = new Intent(CampaignActivity.this, LoginActivity.class);
                startActivity(intent);
            });
        }


    }



    // Sorting method for popular campaigns (by donor count)
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
        Collections.sort(filteredCampaigns, (c1, c2) -> c2.getCampaignDeadline().compareTo(c1.getCampaignDeadline()));
        campaignAdapter.notifyDataSetChanged();
    }

}
