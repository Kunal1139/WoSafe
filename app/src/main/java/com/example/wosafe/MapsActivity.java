package com.example.wosafe;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.wosafe.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private ActivityMapsBinding binding;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE;
    public float GEOFENCE_RADIUS;
    public Button backButton, confirmBtn;
    Spinner spin;
    public LatLng currentMarker;
    private Circle currentCircle;
    private String GEOFENCE_ID ="G1";

    public LatLng selectedLocation = null; // Store selected location


    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Declaring views
        backButton = findViewById(R.id.backButton);
        spin = findViewById(R.id.spin);
        confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setEnabled(false); // Initially disable confirm button

        backButton.setOnClickListener(view -> finish());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String data = adapterView.getItemAtPosition(i).toString();
                switch (data) {
                    case "Select":
                        GEOFENCE_RADIUS = 0; // Reset the radius
                        confirmBtn.setEnabled(false); // Disable confirm button
                        Log.d(TAG, "No radius value selected");
                        break;
                    case "10 meter":
                        GEOFENCE_RADIUS = 50;
                        break;
                    case "25 meter":
                        GEOFENCE_RADIUS = 100;
                        break;
                    case "50 meter":
                        GEOFENCE_RADIUS = 150;
                        break;
                    case "100 meter":
                        GEOFENCE_RADIUS = 200;
                        break;
                    case "200 meter":
                        GEOFENCE_RADIUS = 250;
                        break;
                }

                // If a location has already been selected, update the circle
                if (selectedLocation != null && GEOFENCE_RADIUS >= 0) {
                    mMap.clear(); // Clear previous markers and circles
                    addMarker(selectedLocation); // Add marker again
                    addCircle(selectedLocation, GEOFENCE_RADIUS);

                    // Draw circle at new radius
                }

                checkIfReadyToConfirm();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper =new GeofenceHelper(this);


        confirmBtn.setOnClickListener(view -> {
            if (selectedLocation == null) {
                Toast.makeText(MapsActivity.this, "Please select a location first!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (GEOFENCE_RADIUS <= 0) {
                Toast.makeText(MapsActivity.this, "Please select a radius first!", Toast.LENGTH_SHORT).show();
                return;
            }

            addGeofence(selectedLocation,GEOFENCE_RADIUS);

            // If all conditions are met, proceed
            Intent intent = new Intent();
            intent.putExtra("latitude", selectedLocation.latitude);
            intent.putExtra("longitude", selectedLocation.longitude);
            setResult(RESULT_OK, intent);
            finish();
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng Stjohn = new LatLng(19.7063485, 72.7835132);
        mMap.addMarker(new MarkerOptions().position(Stjohn).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Stjohn, 18));
        enableUserLocation();
        mMap.setOnMapLongClickListener(this);
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear();
        currentMarker = latLng;
        addMarker(latLng);
        selectedLocation = latLng;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        if (GEOFENCE_RADIUS > 0) {
            addCircle(latLng, GEOFENCE_RADIUS);
        }
        checkIfReadyToConfirm();
    }

    private void addGeofence(LatLng latlng,float radius){

        Geofence geofence=geofenceHelper.getGeofence(GEOFENCE_ID,latlng,radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest=geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent =geofenceHelper.getPendingIntent();
        geofencingClient.addGeofences(geofencingRequest,pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG,"onFailure :" + errorMessage);

                    }
                });


    }

    private void checkIfReadyToConfirm() {
        boolean isReady = selectedLocation != null && GEOFENCE_RADIUS > 0;
        confirmBtn.setEnabled(isReady);
        confirmBtn.setAlpha(isReady ? 1.0f : 0.5f); // Make button appear disabled when not ready

        if (isReady) {
            confirmBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF3974"))); // Original button color
        } else {
            confirmBtn.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY)); // Light grey color
        }
    }


    public void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    public void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
        circleOptions.fillColor(Color.argb(64, 255, 0, 0));
        circleOptions.strokeWidth(2);
        mMap.addCircle(circleOptions);
    }
}
