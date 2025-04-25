package com.example.wosafe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeoFence extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final float GEOFENCE_RADIUS = 100; // 100 meters
    private static final String TAG = "GeoFenceFragment";
    private static final int REQUEST_CODE_MAP = 1;

    private Button homeLocation, workLocation;
    private Button saveButton;
    private EditText homeText,workplaceText,contact1,contact2;
    private static final int PICK_CONTACT1 = 1;
    private static final int PICK_CONTACT2 = 2;
    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList;
    private double homeLat, homeLng, workLat, workLng;
    Set<String> selectedContacts = new HashSet<>();

    private final ActivityResultLauncher<Intent> mapActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    double latitude = result.getData().getDoubleExtra("latitude", 0.0);
                    double longitude = result.getData().getDoubleExtra("longitude", 0.0);

                    // Update EditText with formatted coordinates
                    homeText.setText(String.format("%.2f, %.2f", latitude, longitude));
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geo_fence, container, false);

        // Initialize UI elements
        homeLocation = view.findViewById(R.id.home);

        saveButton = view.findViewById(R.id.save_form);
        homeText = view.findViewById(R.id.homeText);
        contact1 = view.findViewById(R.id.contact1);
        contact2 = view.findViewById(R.id.contact2);

        homeText.setFocusable(false);





        loadSavedContacts();



        //Onclick listener for home location button

        homeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the MapsActivity when the button is clicked
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String c1 = contact1.getText().toString().trim();
                String c2 = contact2.getText().toString().trim();
                saveContacts(c1,c2);
            }
        });




        return view;
    }

    private void openContactPicker(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }


    private void loadSavedContacts() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ContactsPrefs", Context.MODE_PRIVATE);
        String savedContact1 = sharedPreferences.getString("contact1", "");
        String savedContact2 = sharedPreferences.getString("contact2", "");

        if (!savedContact1.isEmpty()) {
            contact1.setText(savedContact1);
        }
        if (!savedContact2.isEmpty()) {
            contact2.setText(savedContact2);
        }

    }

    private void saveContacts(String c1, String c2) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ContactsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("contact1", c1);
        editor.putString("contact2", c2);

        editor.apply();
        Intent intent = new Intent(getActivity(), GeofenceBroadcastReceiver.class);


        // Add contacts to intent extras
        intent.putExtra("contact1", c1);
        intent.putExtra("contact2", c2);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.getData() != null) {
            Uri contactUri = data.getData();
            String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
            Cursor cursor = getActivity().getContentResolver().query(contactUri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId}, null);

                    if (phoneCursor != null && phoneCursor.moveToFirst()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        // Check if contact is already selected
                        if (selectedContacts.contains(contactName + " (" + phoneNumber + ")")) {
                            Toast.makeText(getActivity(), "Contact already selected for another field", Toast.LENGTH_SHORT).show();
                        } else {
                            selectedContacts.add(contactName + " (" + phoneNumber + ")");

                            // Set contact to appropriate EditText based on requestCode
                            switch (requestCode) {
                                case PICK_CONTACT1:
                                    contact1.setText(contactName + " (" + phoneNumber + ")");
                                    break;
                                case PICK_CONTACT2:
                                    contact2.setText(contactName + " (" + phoneNumber + ")");
                                    break;

                            }
                        }
                        phoneCursor.close();
                    }
                }
                cursor.close();
            }
        }
    }


}