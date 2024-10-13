package com.example.aidconnect;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyDonationsActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private DonationAdapter donationAdapter;
    private List<Donation> donationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donations);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            setupDrawer();
        }

        recyclerView = findViewById(R.id.recyclerViewDonations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        donationList = new ArrayList<>();
        donationAdapter = new DonationAdapter(donationList, this);
        recyclerView.setAdapter(donationAdapter);

        // Fetch donations from Firestore
        fetchDonations();
    }

    private void fetchDonations() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query to get all donations made by the current user
        db.collection("donations")
                .whereEqualTo("donorId", userId)  // Filter by user ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Donation donation = document.toObject(Donation.class);
                            donationList.add(donation);  // Add to list
                        }
                        donationAdapter.notifyDataSetChanged();  // Update RecyclerView
                    } else {
                        Log.e("Firestore", "Error fetching donations", task.getException());
                    }
                });
    }
}
