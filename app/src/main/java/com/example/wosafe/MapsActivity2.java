package com.example.wosafe;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.wosafe.databinding.ActivityMaps2Binding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    private LatLng selectedLatLng;
    private Marker currentMarker;
    private String locationType;
    private Button saveButton;
    private static final int FINE_LOCATION_ACCESS_REQUEST_CODE = 1004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationType = getIntent().getStringExtra("location_type");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            if (selectedLatLng != null) {
                saveLocation(selectedLatLng);
            } else {
                Toast.makeText(this, "Please select a location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLocation = new LatLng(19.7063485, 72.7835132);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 16));
        enableUserLocation();

        SharedPreferences prefs = getSharedPreferences("location_prefs", MODE_PRIVATE);

        showSavedMarker(prefs, "pickup", "Pickup Location", BitmapDescriptorFactory.HUE_BLUE);
        showSavedMarker(prefs, "destination", "Drop Location", BitmapDescriptorFactory.HUE_RED);
        showSavedMarker(prefs, "stop", "Stop Location", BitmapDescriptorFactory.HUE_YELLOW);

        double lat = prefs.getFloat(locationType + "_lat", 0);
        double lng = prefs.getFloat(locationType + "_lng", 0);
        if (lat != 0 && lng != 0) {
            selectedLatLng = new LatLng(lat, lng);
            currentMarker = mMap.addMarker(new MarkerOptions()
                    .position(selectedLatLng)
                    .title("Selected: " + getReadableLocationType(locationType)));
            currentMarker.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 17));
        }

        mMap.setOnMapLongClickListener(latLng -> {
            if (currentMarker != null) currentMarker.remove();
            selectedLatLng = latLng;
            currentMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Selected: " + getReadableLocationType(locationType)));
            currentMarker.showInfoWindow();
        });

        drawRouteIfNeeded();
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

    private String getReadableLocationType(String key) {
        switch (key) {
            case "pickup":
                return "Pickup Location";
            case "destination":
                return "Drop Location";
            case "stop":
                return "Stop Location";
            default:
                return "Location";
        }
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_ACCESS_REQUEST_CODE);
        }
    }

    private void saveLocation(LatLng latLng) {
        String address = getAddressFromLatLng(latLng);
        if (address == null) {
            Toast.makeText(this, "Could not fetch address.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("location_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(locationType + "_address", address);
        editor.putFloat(locationType + "_lat", (float) latLng.latitude);
        editor.putFloat(locationType + "_lng", (float) latLng.longitude);
        editor.apply();

        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
        drawRouteIfNeeded();
        finish(); // return to Shorts
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

    private void drawRouteIfNeeded() {
        SharedPreferences prefs = getSharedPreferences("location_prefs", MODE_PRIVATE);
        float pickupLat = prefs.getFloat("pickup_lat", 0);
        float pickupLng = prefs.getFloat("pickup_lng", 0);
        float destLat = prefs.getFloat("destination_lat", 0);
        float destLng = prefs.getFloat("destination_lng", 0);
        float stopLat = prefs.getFloat("stop_lat", 0);
        float stopLng = prefs.getFloat("stop_lng", 0);

        if (pickupLat != 0 && pickupLng != 0 && destLat != 0 && destLng != 0) {
            LatLng origin = new LatLng(pickupLat, pickupLng);
            LatLng destination = new LatLng(destLat, destLng);
            LatLng stop = (stopLat != 0 && stopLng != 0) ? new LatLng(stopLat, stopLng) : null;
            String url = getDirectionsUrl(origin, destination, stop);
            fetchRouteInBackground(url);

        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng destination, LatLng stop) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
        String waypoints = (stop != null) ? "&waypoints=" + stop.latitude + "," + stop.longitude : "";
        String mode = "&mode=driving";
        String parameters = str_origin + "&" + str_dest + waypoints + mode;
        return "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=AIzaSyD_NrbSRYv1Q_SecgVL7H3-S0ZI7N73s7k";
    }

    private void fetchRouteInBackground(String urlString) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<LatLng> path = new ArrayList<>();
            List<String> directionsList = new ArrayList<>();
            String distanceText = "", durationText = "";

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JSONObject jsonObject = new JSONObject(json.toString());
                JSONArray routes = jsonObject.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONArray legs = route.getJSONArray("legs");

                    for (int l = 0; l < legs.length(); l++) {
                        JSONObject leg = legs.getJSONObject(l);
                        if (l == 0) {
                            distanceText = leg.getJSONObject("distance").getString("text");
                            durationText = leg.getJSONObject("duration").getString("text");
                        }

                        JSONArray steps = leg.getJSONArray("steps");
                        for (int i = 0; i < steps.length(); i++) {
                            JSONObject step = steps.getJSONObject(i);
                            JSONObject startLoc = step.getJSONObject("start_location");
                            JSONObject endLoc = step.getJSONObject("end_location");
                            directionsList.add(android.text.Html.fromHtml(step.getString("html_instructions")).toString());
                            path.add(new LatLng(startLoc.getDouble("lat"), startLoc.getDouble("lng")));
                            path.add(new LatLng(endLoc.getDouble("lat"), endLoc.getDouble("lng")));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String finalDistanceText = distanceText;
            String finalDurationText = durationText;
            handler.post(() -> {
                if (!path.isEmpty()) {
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(path)
                            .width(10)
                            .color(ContextCompat.getColor(MapsActivity2.this, R.color.lavender))
                            .geodesic(true);
                    mMap.addPolyline(polylineOptions);

                    StringBuilder directions = new StringBuilder("Distance: " + finalDistanceText + "\nDuration: " + finalDurationText + "\nSteps:\n");
                    for (String step : directionsList) {
                        directions.append("• ").append(step).append("\n");
                    }

                    Toast.makeText(MapsActivity2.this, directions.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MapsActivity2.this, "No route found.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}
