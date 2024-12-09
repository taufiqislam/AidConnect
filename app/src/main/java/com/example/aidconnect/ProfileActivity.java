package com.example.aidconnect;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends BaseActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private BarChart donationChart;
    private TextView firstNameTextView, lastNameTextView, emailTextView, phoneTextView;

    private Button campaignButton,donationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            setupDrawer();
        }

        // Initialize UI components
        firstNameTextView = findViewById(R.id.textFirstName);
        lastNameTextView = findViewById(R.id.textLastName);
        emailTextView = findViewById(R.id.textEmail);
        phoneTextView = findViewById(R.id.textPhone);

        getUserDetails();

        // Find the BarChart
        donationChart = findViewById(R.id.donationChart);

        // Fetch and display donations in the chart
        fetchAndDisplayDonations();
    }

    private void fetchAndDisplayDonations() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("donations")
                .whereEqualTo("donorId", userId)  // Match donorId
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Use a map to store the total donation amount for each month
                    Map<Integer, Float> monthlyDonations = new HashMap<>();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        // Retrieve donationAmount and donationTime
                        Integer donationAmount = doc.getLong("donationAmount") != null ? doc.getLong("donationAmount").intValue() : null;
                        Timestamp donationTime = doc.getTimestamp("donationTime");

                        if (donationAmount != null && donationTime != null) {
                            // Convert timestamp to milliseconds
                            long timestamp = donationTime.getSeconds() * 1000;

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(timestamp);
                            int month = calendar.get(Calendar.MONTH) + 1; // 1-indexed month

                            // Add the donationAmount to the existing total for the given month
                            monthlyDonations.put(month, monthlyDonations.getOrDefault(month, 0f) + donationAmount.floatValue());
                        } else {
                            Log.w("ProfileActivity", "Missing donation data for document with ID: " + doc.getId());
                        }
                    }

                    // Convert the map to a list of BarEntry objects
                    List<BarEntry> entries = new ArrayList<>();
                    for (Map.Entry<Integer, Float> entry : monthlyDonations.entrySet()) {
                        entries.add(new BarEntry(entry.getKey(), entry.getValue()));
                    }

                    if (entries.isEmpty()) {
                        Log.d("ProfileActivity", "No donations found for user.");
                    }

                    setupBarChart(entries);
                })
                .addOnFailureListener(e -> Log.e("ProfileActivity", "Error fetching donations", e));
    }




    private void setupBarChart(List<BarEntry> entries) {
        BarDataSet dataSet = new BarDataSet(entries, "Donations per Month");
        dataSet.setColor(Color.parseColor("#FFA726")); // Set bar color
        dataSet.setValueTextColor(Color.BLACK); // Set value color
        dataSet.setValueTextSize(12f);
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);

        donationChart.setData(barData);

        // X-Axis customization
        XAxis xAxis = donationChart.getXAxis();
        xAxis.setGranularity(1f); // Ensure 1 unit granularity
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // Custom formatter for month names
        xAxis.setValueFormatter(new ValueFormatter() {
            private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            @Override
            public String getFormattedValue(float value) {
                int index = (int) value - 1;
                if (index >= 0 && index < months.length) {
                    return months[index];
                }
                return "";
            }
        });

        xAxis.setAxisMinimum(1f);
        xAxis.setAxisMaximum(12f);

        // Disable chart description
        donationChart.getDescription().setEnabled(false);

        // Animate the chart
        donationChart.animateY(1000);

        donationChart.invalidate(); // Refresh chart
    }


    private void getUserDetails() {
        String currentUserId = auth.getCurrentUser().getUid();  // Get the current user's ID

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("phoneNumber");

                        firstNameTextView.setText(firstName);
                        lastNameTextView.setText(lastName);
                        emailTextView.setText(email);
                        phoneTextView.setText(phone);
                    } else {
                        Log.d("ProfileActivity", "No such user data found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("ProfileActivity", "Error getting user details", e);
                });
    }
}
