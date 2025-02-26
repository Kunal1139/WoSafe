package com.example.wosafe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class HomePage extends Fragment {

    private Button button1;
    private static final int SMS_PERMISSION_CODE = 101;
    private static final int LOCATION_PERMISSION_CODE = 102;

    private String contact1, contact2, contact3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve contacts passed from the Contacts fragment via Bundle
        if (getArguments() != null) {
            contact1 = getArguments().getString("contact_number1");
            contact2 = getArguments().getString("contact_number2");
            contact3 = getArguments().getString("contact_number3");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set ActionBar title to "WoSafe" when HomePageFragment is resumed
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("WoSafe");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        button1 = view.findViewById(R.id.button1);

        // Load saved contacts from SharedPreferences if they are not passed via Bundle
        if (contact1 == null || contact2 == null || contact3 == null) {
            loadSavedContacts();
        }

        // Start the background service for power button detection
        Intent serviceIntent = new Intent(requireContext(), BackgroundService.class);
        requireContext().startService(serviceIntent);

        // Set SOS button click listener
        button1.setOnClickListener(view1 -> {
            if (checkPermissions()) {
                sendSosMessage();
            }
        });

        return view;
    }

    // Check necessary permissions
    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION}, SMS_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSosMessage();
            } else {
                Toast.makeText(getActivity(), "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to send SMS with location
    private void sendSosMessage() {
        if (contact1 == null && contact2 == null && contact3 == null) {
            Toast.makeText(getActivity(), "No contacts saved", Toast.LENGTH_SHORT).show();
            return;
        }

        String locationMessage = getLocation();
        SmsManager smsManager = SmsManager.getDefault();
        String sosMessage = "🚨 Emergency SOS! Please help me! " + locationMessage;

        try {
            if (contact1 != null && !contact1.isEmpty()) {
                smsManager.sendTextMessage(contact1, null, sosMessage, null, null);
            }
            if (contact2 != null && !contact2.isEmpty()) {
                smsManager.sendTextMessage(contact2, null, sosMessage, null, null);
            }
            if (contact3 != null && !contact3.isEmpty()) {
                smsManager.sendTextMessage(contact3, null, sosMessage, null, null);
            }

            Toast.makeText(getActivity(), "SOS sent successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Failed to send SMS", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Get the user's current location (GPS coordinates or "Location not available" message)
    private String getLocation() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "Location not available (Permission denied)";
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            return "My Location: https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude();
        } else {
            return "Location not available";
        }
    }

    // Load contacts from SharedPreferences
    private void loadSavedContacts() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ContactsPrefs", Context.MODE_PRIVATE);
        contact1 = sharedPreferences.getString("contact1", null);
        contact2 = sharedPreferences.getString("contact2", null);
        contact3 = sharedPreferences.getString("contact3", null);

        if (contact1 == null && contact2 == null && contact3 == null) {
            Toast.makeText(getActivity(), "No saved contacts found", Toast.LENGTH_SHORT).show();
        }
    }
}
