package com.example.aidconnect;

import android.os.Bundle;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MyCampaignsActivity extends BaseActivity {
    private RecyclerView rvMyCampaigns;
    private MyCampaignAdapter myCampaignAdapter;
    private FirebaseFirestore db;
    private List<Campaign> userCampaigns = new ArrayList<>();
    private List<String> campaignIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_campaigns);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            setupDrawer();
        }

        rvMyCampaigns = findViewById(R.id.rvMyCampaigns);
        rvMyCampaigns.setLayoutManager(new LinearLayoutManager(this));

        fetchUserCampaigns(currentUser != null ? currentUser.getUid() : null);
    }

    private void fetchUserCampaigns(String userId) {
        if (userId == null) {
            Toast.makeText(this, "Please log in to view your campaigns", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference campaignsRef = db.collection("campaigns");
        campaignsRef.whereEqualTo("creatorId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userCampaigns.clear();
                        campaignIds.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Campaign campaign = document.toObject(Campaign.class);
                            String campaignId = document.getId();

                            userCampaigns.add(campaign);
                            campaignIds.add(campaignId);
                        }
                        userCampaigns.sort((c1, c2) -> c2.getCampaignDeadline().compareTo(c1.getCampaignDeadline()));
                        myCampaignAdapter = new MyCampaignAdapter(userCampaigns, campaignIds, this);
                        rvMyCampaigns.setAdapter(myCampaignAdapter);
                    } else {
                        Toast.makeText(MyCampaignsActivity.this, "Error fetching your campaigns", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
