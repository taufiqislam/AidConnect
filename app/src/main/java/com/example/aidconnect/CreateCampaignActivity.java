package com.example.aidconnect;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateCampaignActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etDeadline, etDonationTarget, etDonationNumber;
    private Spinner spCategory, spDonationMedium;
    private Button btnUploadImage, btnCreateCampaign;
    private ImageView ivSelectedImage;
    private Uri imageUri;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

//    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_campaign);

        // Initialize Firebase instances
//        auth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        storageRef = FirebaseStorage.getInstance().getReference("campaign_images");

        // Initialize UI elements
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDeadline = findViewById(R.id.etDeadline);
        etDonationTarget = findViewById(R.id.etDonationTarget);
        spCategory = findViewById(R.id.spCategory);
        spDonationMedium = findViewById(R.id.spDonationMedium);
        etDonationNumber = findViewById(R.id.etDonationNumber);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnCreateCampaign = findViewById(R.id.btnCreateCampaign);

        // Set up DatePicker for deadline
        etDeadline.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateCampaignActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etDeadline.setText(selectedDate);
                    }, year, month, day);

            datePickerDialog.show();
        });

        // Handle donation medium selection
        spDonationMedium.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMedium = parent.getItemAtPosition(position).toString();
                if (selectedMedium.equals("Bkash") || selectedMedium.equals("Nagad") || selectedMedium.equals("Debit card")) {
                    etDonationNumber.setVisibility(View.VISIBLE);
                    etDonationNumber.setHint("Enter your " + selectedMedium + " number");
                } else {
                    etDonationNumber.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                etDonationNumber.setVisibility(View.GONE);
            }
        });

        // Initialize image picker
        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        ivSelectedImage.setImageURI(imageUri);
                    }
                }
        );

        btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
        });

        // Create campaign button
//        btnCreateCampaign.setOnClickListener(v -> createCampaign());
    }

//    private void createCampaign() {
//        String title = etTitle.getText().toString().trim();
//        String description = etDescription.getText().toString().trim();
//        String campaignDeadline = etDeadline.getText().toString().trim();
//        String donationtarget  = etDonationTarget.getText().toString().trim();
//        String category = spCategory.getSelectedItem().toString();
//        String donationMedium = spDonationMedium.getSelectedItem().toString();
//        String creatorId = auth.getCurrentUser().getUid();
//
//        if (title.isEmpty() || description.isEmpty() || campaignDeadline.isEmpty() || donationtarget.isEmpty()) {
//            Toast.makeText(this, "Please fill all the required fields.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Convert donation target to integer
//        int donationTarget = Integer.parseInt(donationtarget);
//
//        // Store the image and campaign info in Firebase
//        if (imageUri != null) {
//            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
//            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                String imageUrl = uri.toString();
//                saveCampaignToFirestore(title, description, campaignDeadline, donationTarget, category, donationMedium, creatorId, imageUrl);
//            })).addOnFailureListener(e -> Toast.makeText(CreateCampaignActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
//        } else {
//            // If no image is selected, save campaign without image URL
//            saveCampaignToFirestore(title, description, campaignDeadline, donationTarget, category, donationMedium, creatorId, null);
//        }
//    }
//
//    private void saveCampaignToFirestore(String title, String description, String deadline, int donationTarget, String category,
//                                         String donationMedium, String creatorId, @Nullable String imageUrl) {
//        Map<String, Object> campaign = new HashMap<>();
//        campaign.put("title", title);
//        campaign.put("description", description);
//        campaign.put("campaignDeadline", deadline);
//        campaign.put("donationTarget", donationTarget);
//        campaign.put("donorCount", 0);
//        campaign.put("currentDonation", 0);
//        campaign.put("category", category);
//        campaign.put("creatorId", creatorId);
//        campaign.put("image", imageUrl);  // Image URL or null
//
//        db.collection("campaigns").add(campaign)
//                .addOnSuccessListener(documentReference -> Toast.makeText(CreateCampaignActivity.this, "Campaign created successfully", Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(e -> Toast.makeText(CreateCampaignActivity.this, "Failed to create campaign", Toast.LENGTH_SHORT).show());
//    }
}
