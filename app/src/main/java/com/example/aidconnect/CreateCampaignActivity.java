package com.example.aidconnect;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateCampaignActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etTitle, etDescription, etDeadline, etDonationTarget, etDonationNumber;
    private Spinner spCategory, spDonationMedium;
    private ImageView ivSelectedImage;
    private Button btnCreateCampaign, btnUploadImage;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String selectedCategory = "";
    private Map<String, String> paymentMethods = new HashMap<>();
    private Uri imageUri;  // To store the image URI for later use
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_campaign);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            setupDrawer();
        }

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDeadline = findViewById(R.id.etDeadline);
        etDonationTarget = findViewById(R.id.etDonationTarget);
        etDonationNumber = findViewById(R.id.etDonationNumber); // Initially hidden
        spCategory = findViewById(R.id.spCategory);
        spDonationMedium = findViewById(R.id.spDonationMedium);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnCreateCampaign = findViewById(R.id.btnCreateCampaign);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Set up the spinner for categories
        String[] campaignCategories = getResources().getStringArray(R.array.campaign_categories);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, campaignCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        // Handle Category Spinner selection
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCategory = spCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        // Set up date picker for campaign deadline
        etDeadline.setOnClickListener(v -> showDatePickerDialog());

        // Handle Payment Method Spinner selection
        etDonationNumber.setVisibility(View.GONE);

        // Load the donation mediums from strings.xml
        String[] donationMediums = getResources().getStringArray(R.array.donation_mediums);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, donationMediums);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDonationMedium.setAdapter(adapter);

        // Handle Donation Medium Spinner selection
        spDonationMedium.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedMedium = spDonationMedium.getSelectedItem().toString();
                // Check the selected item and set visibility accordingly
                if (selectedMedium.equals("None")) {
                    etDonationNumber.setVisibility(View.GONE); // Hide EditText if "none" is chosen
                } else {
                    etDonationNumber.setVisibility(View.VISIBLE); // Show EditText for valid selections
                    etDonationNumber.setHint("Enter " + selectedMedium + " number");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                etDonationNumber.setVisibility(View.GONE); // Hide if nothing is selected
            }
        });

        // Handle Upload Image Button click
        btnUploadImage.setOnClickListener(v -> openFileChooser());

        // Handle Create Campaign Button click
        btnCreateCampaign.setOnClickListener(v -> createCampaign());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Get the URI of the selected image
            ivSelectedImage.setImageURI(imageUri);
            ivSelectedImage.setVisibility(View.VISIBLE);
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            etDeadline.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void createCampaign() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String deadlineStr = etDeadline.getText().toString();
        int donationTarget = Integer.parseInt(etDonationTarget.getText().toString());
        String donationMedium = spDonationMedium.getSelectedItem().toString();
        String donationNumber = etDonationNumber.getText().toString();

        // Validate inputs
        if (title.isEmpty() || description.isEmpty() || deadlineStr.isEmpty() || donationNumber.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill in all fields and upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add payment method to the map
        paymentMethods.put(donationMedium, donationNumber);

        // Convert deadline to Date object
        Date campaignDeadline = parseDate(deadlineStr);

        if (campaignDeadline == null) {
            Toast.makeText(this, "Please enter a valid deadline", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the image file reference in Firebase Storage
        StorageReference imageRef = storage.getReference("campaign_images/" + System.currentTimeMillis() + ".jpg");

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            // Get the download URL of the uploaded image
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                // Create Campaign object with the image URL
                String creatorId = mAuth.getCurrentUser().getUid();
                Campaign campaign = new Campaign(title, description, new Date(), campaignDeadline, donationTarget, 0,
                        selectedCategory, creatorId, paymentMethods, imageUrl);

                // Add campaign to Firestore
                db.collection("campaigns").add(campaign).addOnSuccessListener(documentReference -> {
                    Toast.makeText(CreateCampaignActivity.this, "Campaign created successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateCampaignActivity.this, CampaignActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(CreateCampaignActivity.this, "Failed to create campaign", Toast.LENGTH_SHORT).show();
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(CreateCampaignActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(CreateCampaignActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    private Date parseDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Define the format of the date
        Date date = null;
        try {
            date = dateFormat.parse(dateStr); // Parse the string to a Date object
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
