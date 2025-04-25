//package com.example.wosafe;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.FragmentActivity;
//
//import android.Manifest;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.content.res.ColorStateList;
//import android.graphics.Color;
//import android.location.Address;
//import android.location.Geocoder;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import com.google.android.gms.location.Geofence;
//import com.google.android.gms.location.GeofencingClient;
//import com.google.android.gms.location.GeofencingRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.Circle;
//import com.google.android.gms.maps.model.CircleOptions;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.example.wosafe.databinding.ActivityMapsBinding;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMapLongClickListener {
//
//    private GoogleMap mMap;
//    private GeofencingClient geofencingClient;
//    private GeofenceHelper geofenceHelper;
//
//    private ActivityMapsBinding binding;
//    private int FINE_LOCATION_ACCESS_REQUEST_CODE;
//    public float GEOFENCE_RADIUS;
//    public Button backButton, confirmBtn;
//    Spinner spin;
//    private LatLng selectedLatLng;
//    private Marker currentMarker;
//    private Circle currentCircle;
//    private String GEOFENCE_ID ;
//    private String locationType;
//    private int geofenceCount = 0;
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
//    private List<Circle> geofenceCircles = new ArrayList<>();
//
//// Track number of geofences
//
//
//     // Store selected location
//
//
//    private static final String TAG = "MapsActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Declaring views
//        backButton = findViewById(R.id.backButton);
//        spin = findViewById(R.id.spin);
//        confirmBtn = findViewById(R.id.confirmBtn);
//        confirmBtn.setEnabled(false); // Initially disable confirm button
//
//        backButton.setOnClickListener(view -> finish());
//
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerList, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//        spin.setAdapter(adapter);
//
//        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String data = adapterView.getItemAtPosition(i).toString();
//                switch (data) {
//                    case "Select":
//                        GEOFENCE_RADIUS = 0; // Reset the radius
//                        confirmBtn.setEnabled(false); // Disable confirm button
//                        Log.d(TAG, "No radius value selected");
//                        break;
//                    case "10 meter":
//                        GEOFENCE_RADIUS = 50;
//                        break;
//                    case "25 meter":
//                        GEOFENCE_RADIUS = 100;
//                        break;
//                    case "50 meter":
//                        GEOFENCE_RADIUS = 150;
//                        break;
//                    case "100 meter":
//                        GEOFENCE_RADIUS = 200;
//                        break;
//                    case "200 meter":
//                        GEOFENCE_RADIUS = 250;
//                        break;
//                }
//
//                // If a location has already been selected, update the circle
//
//
//
//
//                checkIfReadyToConfirm();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {}
//        });
//
//
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }
//        geofencingClient = LocationServices.getGeofencingClient(this);
//        geofenceHelper =new GeofenceHelper(this);
//
//
//        confirmBtn.setOnClickListener(view -> {
//
//            // Add geofence only when the user confirms
//            Toast.makeText(this,"Processing to add Geofences",Toast.LENGTH_SHORT).show();
//
//                if (selectedLatLng != null) {
//                    locationType = (geofenceCount == 1) ? "home" : "work";
//                    saveLocation(selectedLatLng);
//                } else {
//                    Toast.makeText(this, "Please select a location.", Toast.LENGTH_SHORT).show();
//                }
//
////            Toast.makeText(this,"Geofence triggered successfully",Toast.LENGTH_SHORT).show();
//
//            // Send back the selected location
////            finish();
//        });
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//        }
//
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        LatLng Stjohn = new LatLng(19.7063485, 72.7835132);
//        mMap.addMarker(new MarkerOptions().position(Stjohn).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Stjohn, 18));
//        enableUserLocation();
//        mMap.setOnMapLongClickListener(this);
//        SharedPreferences prefs = getSharedPreferences("location_prefs", MODE_PRIVATE);
//        showSavedMarker(prefs, "home", "Home Location", BitmapDescriptorFactory.HUE_RED);
//        showSavedMarker(prefs, "work", "Workplace Location", BitmapDescriptorFactory.HUE_GREEN);
//    }
//
//    private void enableUserLocation() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
//                PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
//        } else {
//            // Ask for permission
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                // We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        FINE_LOCATION_ACCESS_REQUEST_CODE);
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        FINE_LOCATION_ACCESS_REQUEST_CODE);
//            }
//        }
//    }
//
//    private void showSavedMarker(SharedPreferences prefs, String key, String label, float hue) {
//        double lat = prefs.getFloat(key + "_lat", 0);
//        double lng = prefs.getFloat(key + "_lng", 0);
//        if (lat != 0 && lng != 0) {
//            LatLng position = new LatLng(lat, lng);
//            Marker marker = mMap.addMarker(new MarkerOptions()
//                    .position(position)
//                    .title(label)
//                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));
//            marker.showInfoWindow();
//        }
//    }
//
//    private void saveLocation(LatLng latLng) {
//        String address = getAddressFromLatLng(latLng);
//        if (address == null) {
//            Toast.makeText(this, "Could not fetch address.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        SharedPreferences prefs = getSharedPreferences("location_prefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(locationType + "_address", address);
//        editor.putFloat(locationType + "_lat", (float) latLng.latitude);
//        editor.putFloat(locationType + "_lng", (float) latLng.longitude);
//        editor.apply();
//
//        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
//
//        finish(); // return to TaxiCab
//    }
//
//    private String getAddressFromLatLng(LatLng latLng) {
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            List<Address> list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//            if (!list.isEmpty()) {
//                return list.get(0).getAddressLine(0);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    @Override
//
//    public void onMapLongClick(@NonNull LatLng latLng) {
//
//        if (GEOFENCE_RADIUS <= 0) {
//            Toast.makeText(this, "Please select a radius before adding a location!", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        if (currentMarker != null) currentMarker.remove();
//
////        if (geofenceCount >= 2) {  // Prevent adding more than two geofences
////            Toast.makeText(this, "You can only add one geofence!", Toast.LENGTH_SHORT).show();
////            return;
////        }
//
//        // ✅ Check if the new geofence overlaps with an existing one
//        if (isOverlapping(latLng, GEOFENCE_RADIUS)) {
//            Toast.makeText(this, "New geofence overlaps with an existing one! Choose a different location.", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        geofenceCount++;  // Increase count when a new geofence is added
//        selectedLatLng = latLng;
//        addMarker(latLng);
//        selectedLatLng = latLng;
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
//        if (selectedLatLng != null && GEOFENCE_RADIUS >= 0 && geofenceCount<=2) {
//            // mMap.clear(); // Clear previous markers and circles
//            //addMarker(selectedLocation); // Add marker again
//            addCircle(selectedLatLng, GEOFENCE_RADIUS);
//            GEOFENCE_ID = (geofenceCount == 0) ? "HOME_GEOFENCE" : "WORK_GEOFENCE";
//            addGeofence(selectedLatLng,GEOFENCE_RADIUS,GEOFENCE_ID);
//
//            // Draw circle at new radius
//        }
//
//        checkIfReadyToConfirm();
//    }
//
//    // ✅ Helper method to check geofence overlap
//    private boolean isOverlapping(LatLng newLocation, float newRadius) {
//        for (Circle circle : geofenceCircles) {
//            float[] distance = new float[1];
//            android.location.Location.distanceBetween(
//                    newLocation.latitude, newLocation.longitude,
//                    circle.getCenter().latitude, circle.getCenter().longitude,
//                    distance);
//
//            if (distance[0] < (circle.getRadius() + newRadius)) {
//                return true; // Overlapping found
//            }
//        }
//        return false; // No overlap
//    }
//
//
//
//
//
//    private void addGeofence(LatLng latlng, float radius, String geofenceId) {
//        // Create the geofence using the provided geofenceId (not the class-level GEOFENCE_ID)
//        Geofence geofence = geofenceHelper.getGeofence(geofenceId, latlng, radius,
//                Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_DWELL |
//                        Geofence.GEOFENCE_TRANSITION_EXIT);
//
//        // Ensure multiple geofences are handled correctly
//        List<Geofence> geofenceList = new ArrayList<>();
//        geofenceList.add(geofence);
//
//        // Add new geofence to the list
//        if (geofenceCount == 1) {
//            geofenceList.add(geofenceHelper.getGeofence("HOME_GEOFENCE", latlng, radius,
//                    Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT));
//        } else if (geofenceCount == 2) {
//            geofenceList.add(geofenceHelper.getGeofence("WORK_GEOFENCE", latlng, radius,
//                    Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT));
//        }
//
//        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofenceList);
//
//        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return; // Return if permission is not granted
//        }
//
//        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.d(TAG, "OnSuccess: Geofence Added successfully: " + geofenceId);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        String errorMessage = geofenceHelper.getErrorString(e);
//                        Log.d(TAG, "OnFailure: " + errorMessage);
//                    }
//                });
//    }
//
//
//
//
//    private void checkIfReadyToConfirm() {
//        boolean isReady = selectedLatLng != null && GEOFENCE_RADIUS > 0;
//        confirmBtn.setEnabled(isReady);
//        confirmBtn.setAlpha(isReady ? 1.0f : 0.5f); // Make button appear disabled when not ready
//
//        if (isReady) {
//            confirmBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF3974"))); // Original button color
//        } else {
//            confirmBtn.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY)); // Light grey color
//        }
//    }
//
//
//    public void addMarker(LatLng latLng) {
//        currentMarker = mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title("Selected: " + getReadableLocationType(locationType))
//                .icon(geofenceCount == 1
//                        ? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
//                        : BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//    }
//
//
//    private String getReadableLocationType(String key) {
//        switch (key) {
//            case "home":
//                return "Home Location";
//            case "work":
//                return "Workplace Location";
//            default:
//                return "Location";
//        }
//    }
//
//
//    public void addCircle(LatLng latLng, float radius) {
//        int strokeColor = (geofenceCount == 1) ? Color.RED : Color.GREEN;
//        int fillColor = (geofenceCount == 1) ? Color.argb(64, 255, 0, 0) : Color.argb(64, 0, 255, 0);
//
//        CircleOptions circleOptions = new CircleOptions()
//                .center(latLng)
//                .radius(radius)
//                .strokeColor(strokeColor)
//                .fillColor(fillColor)
//                .strokeWidth(2);
//
//        Circle circle = mMap.addCircle(circleOptions);
//        geofenceCircles.add(circle); // Store the circle in the list
//    }
//
//
//    // Handle permission result
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
//                enableUserLocation();  // ✅ Enable location after permission is granted
//            } else {
//                Toast.makeText(this, "Location permission required", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//
//
//}

package com.example.wosafe;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.wosafe.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private String GEOFENCE_ID ;
    private int geofenceCount = 0;
    private String locationType;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private List<Circle> geofenceCircles = new ArrayList<>();

// Track number of geofences


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



        locationType = getIntent().getStringExtra("location_type");

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
                        GEOFENCE_RADIUS = 5;
                        break;
                    case "25 meter":
                        GEOFENCE_RADIUS = 40;
                        break;
                    case "50 meter":
                        GEOFENCE_RADIUS = 80;
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

            // Generate unique Geofence ID
            String requestID = geofenceCount == 0 ? "HOME_GEOFENCE" : "WORK_GEOFENCE";
            GEOFENCE_ID = requestID;


            // Add geofence only when the user confirms
            Toast.makeText(this,"Processing to add Geofences",Toast.LENGTH_SHORT).show();

            addGeofence(selectedLocation, GEOFENCE_RADIUS, GEOFENCE_ID);
            Toast.makeText(this,"Geofence triggered successfully",Toast.LENGTH_SHORT).show();

            // Send back the selected location
            Intent intent = new Intent();
            intent.putExtra("latitude", selectedLocation.latitude);
            intent.putExtra("longitude", selectedLocation.longitude);
            setResult(RESULT_OK, intent);
            saveLocation(selectedLocation);
//            finish();
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }







    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (!list.isEmpty()) {
                return list.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    private void showSavedMarker(SharedPreferences prefs, String key, String label, float hue) {
        double lat = prefs.getFloat(key + "_lat", 0);
        double lng = prefs.getFloat(key + "_lng", 0);
        if (lat != 0 && lng != 0) {
            LatLng position = new LatLng(lat, lng);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(label)
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));
            marker.showInfoWindow();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng Stjohn = new LatLng(19.7063485, 72.7835132);
        mMap.addMarker(new MarkerOptions().position(Stjohn).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Stjohn, 18));
        SharedPreferences prefs2 = getSharedPreferences("location_prefs", MODE_PRIVATE);
        showSavedMarker(prefs2, "home", "Home Location", BitmapDescriptorFactory.HUE_RED);
        showSavedMarker(prefs2, "work", "Workplace Location", BitmapDescriptorFactory.HUE_GREEN);
        enableUserLocation();
        mMap.setOnMapLongClickListener(this);

    }

    private void saveLocation(LatLng latLng) {
        String address = getAddressFromLatLng(latLng);
        if (address == null) {
            Toast.makeText(this, "Could not fetch address.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs2 = getSharedPreferences("location_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs2.edit();
        editor.putString(locationType + "_address", address);
        editor.putFloat(locationType + "_lat", (float) latLng.latitude);
        editor.putFloat(locationType + "_lng", (float) latLng.longitude);
        editor.apply();

        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
        // return to TaxiCab
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }


    @Override

    public void onMapLongClick(@NonNull LatLng latLng) {
        if (geofenceCount >= 2) {  // Prevent adding more than two geofences
            Toast.makeText(this, "You can only add two geofences!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Check if the new geofence overlaps with an existing one
        if (isOverlapping(latLng, GEOFENCE_RADIUS)) {
            Toast.makeText(this, "New geofence overlaps with an existing one! Choose a different location.", Toast.LENGTH_LONG).show();
            return;
        }

        geofenceCount++;  // Increase count when a new geofence is added
        currentMarker = latLng;
        addMarker(latLng);
        selectedLocation = latLng;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

        if (GEOFENCE_RADIUS > 0) {
            addCircle(latLng, GEOFENCE_RADIUS);
        }

        checkIfReadyToConfirm();
    }

    // ✅ Helper method to check geofence overlap
    private boolean isOverlapping(LatLng newLocation, float newRadius) {
        for (Circle circle : geofenceCircles) {
            float[] distance = new float[1];
            android.location.Location.distanceBetween(
                    newLocation.latitude, newLocation.longitude,
                    circle.getCenter().latitude, circle.getCenter().longitude,
                    distance);

            if (distance[0] < (circle.getRadius() + newRadius)) {
                return true; // Overlapping found
            }
        }
        return false; // No overlap
    }





    private void addGeofence(LatLng latlng, float radius, String geofenceId) {
        // Create the geofence using the provided geofenceId (not the class-level GEOFENCE_ID)
        Geofence geofence = geofenceHelper.getGeofence(geofenceId, latlng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_DWELL |
                        Geofence.GEOFENCE_TRANSITION_EXIT);

        // Ensure multiple geofences are handled correctly
        List<Geofence> geofenceList = new ArrayList<>();
        geofenceList.add(geofence);

        // Add new geofence to the list
        if (geofenceCount == 1) {
            geofenceList.add(geofenceHelper.getGeofence("HOME_GEOFENCE", latlng, radius,
                    Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT));
        } else if (geofenceCount == 2) {
            geofenceList.add(geofenceHelper.getGeofence("WORK_GEOFENCE", latlng, radius,
                    Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT));
        }

        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofenceList);

        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // Return if permission is not granted
        }

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "OnSuccess: Geofence Added successfully: " + geofenceId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "OnFailure: " + errorMessage);
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
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(geofenceCount == 1 ? "Home" : "Workplace")  // Label markers
                .icon(geofenceCount == 1 ?
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) :
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMap.addMarker(markerOptions);
    }


    public void addCircle(LatLng latLng, float radius) {
        int strokeColor = (geofenceCount == 1) ? Color.RED : Color.GREEN;
        int fillColor = (geofenceCount == 1) ? Color.argb(64, 255, 0, 0) : Color.argb(64, 0, 255, 0);

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(strokeColor)
                .fillColor(fillColor)
                .strokeWidth(2);

        Circle circle = mMap.addCircle(circleOptions);
        geofenceCircles.add(circle); // Store the circle in the list
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Geofence Channel";
            String description = "Channel for geofence notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("geofence_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    }, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already granted, proceed with geofencing

        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
                enableUserLocation();  // ✅ Enable location after permission is granted
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_LONG).show();
            }
        }
    }



}
