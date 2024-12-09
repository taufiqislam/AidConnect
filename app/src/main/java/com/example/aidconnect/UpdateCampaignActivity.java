package com.example.aidconnect;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdateCampaignActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etTitle, etDescription, etDeadline, etDonationTarget, etDonationNumber;
    private Spinner spCategory, spDonationMedium;
    private ImageView ivSelectedImage;
    private Button btnUpdateCampaign, btnUploadImage;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String selectedCategory;
    private Map<String, String> paymentMethods = new HashMap<>();
    private Uri imageUri;
    private String campaignId;
    private Campaign campaign;
    private boolean isImageUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_campaign);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            setupDrawer();
        }

        setupViews();

        // Fetch campaign details from intent
        Intent intent = getIntent();
        campaignId = intent.getStringExtra("campaignId");
        fetchCampaignDetails(campaignId);

        btnUploadImage.setOnClickListener(v -> openFileChooser());
        btnUpdateCampaign.setOnClickListener(v -> updateCampaign());
    }

    private void setupViews() {
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDeadline = findViewById(R.id.etDeadline);
        etDonationTarget = findViewById(R.id.etDonationTarget);
        etDonationNumber = findViewById(R.id.etDonationNumber);
        spCategory = findViewById(R.id.spCategory);
        spDonationMedium = findViewById(R.id.spDonationMedium);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnUpdateCampaign = findViewById(R.id.btnUpdateCampaign);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        etDeadline.setOnClickListener(v -> showDatePickerDialog());

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCategory = spCategory.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void fetchCampaignDetails(String campaignId) {
        db.collection("campaigns").document(campaignId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                campaign = documentSnapshot.toObject(Campaign.class);
                if (campaign != null) {
                    populateCampaignData(campaign);
                }
            } else {
                Toast.makeText(this, "Campaign not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch campaign details", Toast.LENGTH_SHORT).show());
    }

    private void populateCampaignData(Campaign campaign) {
        etTitle.setText(campaign.getTitle());
        etDescription.setText(campaign.getDescription());
        etDeadline.setText(new SimpleDateFormat("dd/MM/yyyy").format(campaign.getCampaignDeadline()));
        etDonationTarget.setText(String.valueOf(campaign.getDonationTarget()));
        imageUri = Uri.parse(campaign.getImageUrl());

        Glide.with(this)
                .load(campaign.getImageUrl())
                .placeholder(R.drawable.sample)
                .into(ivSelectedImage);

        // Set Category Spinner selection
        Log.d(TAG, "category: " + campaign.getCategory());
        Log.d("Campaign Data", "Payment Methods: " + campaign.getPaymentMethods());
        int categoryPosition = ((ArrayAdapter<String>) spCategory.getAdapter()).getPosition(campaign.getCategory());
        spCategory.setSelection(categoryPosition);

        // Set Payment Method Spinner selection
        if (!campaign.getPaymentMethods().isEmpty()) {
            String selectedMethod = campaign.getPaymentMethods().keySet().iterator().next();
            etDonationNumber.setText(campaign.getPaymentMethods().get(selectedMethod));

            // Make sure payment methods are populated in the spinner before selecting the payment method
            int paymentMethodPosition = ((ArrayAdapter<String>) spDonationMedium.getAdapter()).getPosition(selectedMethod);
            spDonationMedium.setSelection(paymentMethodPosition);
        }
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
            imageUri = data.getData();
            ivSelectedImage.setImageURI(imageUri);
            isImageUpdated = true;
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            etDeadline.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateCampaign() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String deadlineStr = etDeadline.getText().toString();
        int donationTarget = Integer.parseInt(etDonationTarget.getText().toString());
        Date deadline = parseDate(deadlineStr);

        // Retrieve payment methods
        String selectedPaymentMethod = spDonationMedium.getSelectedItem().toString();
        String donationNumber = etDonationNumber.getText().toString();

        // Ensure payment methods map is populated
        if (!selectedPaymentMethod.isEmpty() && !donationNumber.isEmpty()) {
            paymentMethods.put(selectedPaymentMethod, donationNumber);
        }

        // Validate required fields
        if (title.isEmpty() || description.isEmpty() || deadlineStr.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("campaigns").document(campaignId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                campaign.setTitle(title);
                campaign.setDescription(description);
                campaign.setCampaignDeadline(deadline);
                campaign.setDonationTarget(donationTarget);
                campaign.setCategory(selectedCategory);
                campaign.setPaymentMethods(paymentMethods);

                if(isImageUpdated)
                {
                    StorageReference imageRef = storage.getReference("campaign_images/" + System.currentTimeMillis() + ".jpg");
                    imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        campaign.setImageUrl(imageUrl);

                        db.collection("campaigns").document(campaignId).set(campaign)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Campaign updated successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }).addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());

                    })).addOnFailureListener(e -> Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show());
                }
                else
                {
                    String imageUrl = imageUri.toString();
                    campaign.setImageUrl(imageUrl);
                    db.collection("campaigns").document(campaignId).set(campaign)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Campaign updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }).addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(this, "Campaign not found", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private Date parseDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
