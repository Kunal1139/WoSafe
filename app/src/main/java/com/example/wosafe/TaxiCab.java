package com.example.wosafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wosafe.databinding.FragmentShortsBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaxiCab extends Fragment {

    private FragmentShortsBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentShortsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Image Picker Launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        binding.firebaseimage.setImageURI(imageUri);
                        Toast.makeText(getContext(), "Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Click listeners
        binding.imageView7.setOnClickListener(v -> openMapsActivity2("pickup"));
        binding.imageView9.setOnClickListener(v -> openMapsActivity2("destination"));
        binding.imageView8.setOnClickListener(v -> openMapsActivity2("stop"));

        binding.button2.setOnClickListener(v -> resetFields());

        binding.button5.setOnClickListener(v -> selectImage());

//        binding.button4.setOnClickListener(v -> uploadImage());


        loadSavedAddresses();
        return view;
    }

    private void openContactPicker(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadImage() {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading File...");
        progressDialog.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_dd_HH_mm_ss", Locale.CANADA);
        String fileName = formatter.format(new Date());

        storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    binding.firebaseimage.setImageURI(null);
                    imageUri = null;
                    Toast.makeText(getContext(), "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void openMapsActivity2(String locationType) {
        Intent intent = new Intent(getActivity(), MapsActivity2.class);
        intent.putExtra("location_type", locationType);
        startActivity(intent);
    }

    private void resetFields() {
        binding.editTextText5.setText(""); // Pickup
        binding.editTextText.setText("");  // Destination
        binding.editTextText4.setText(""); // Stop

        SharedPreferences.Editor editor = requireActivity()
                .getSharedPreferences("location_prefs", getContext().MODE_PRIVATE)
                .edit();
        editor.clear();
        editor.apply();

        Toast.makeText(getContext(), "Fields Reset", Toast.LENGTH_SHORT).show();
    }

    private void loadSavedAddresses() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("location_prefs", getContext().MODE_PRIVATE);

        binding.editTextText5.setText(prefs.getString("pickup_address", ""));
        binding.editTextText.setText(prefs.getString("destination_address", ""));
        binding.editTextText4.setText(prefs.getString("stop_address", ""));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSavedAddresses();  // Refresh address fields when returning from MapsActivity2
    }
}
