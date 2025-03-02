package com.example.wosafe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import java.util.List;

public class GeoFence extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final float GEOFENCE_RADIUS = 100; // 100 meters
    private static final String TAG = "GeoFenceFragment";
    private static final int REQUEST_CODE_MAP = 1;

    private Button homeLocation, workLocation;
    private Button saveButton;
    private EditText homeText,workplaceText;

    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList;
    private double homeLat, homeLng, workLat, workLng;

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
        workLocation = view.findViewById(R.id.workplace);
        saveButton = view.findViewById(R.id.save_form);
        homeText = view.findViewById(R.id.homeText);
        workplaceText = view.findViewById(R.id.workplaceText);








        //Onclick listener for home location button

        homeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the MapsActivity when the button is clicked
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }


}
