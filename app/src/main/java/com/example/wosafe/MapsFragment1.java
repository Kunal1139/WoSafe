package com.example.wosafe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment1 extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker pickupMarker, destinationMarker, stopMarker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Default location (example: Mumbai)
        LatLng defaultLocation = new LatLng(19.0760, 72.8777);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        // Map click listener for pickup, destination, and stops
        mMap.setOnMapClickListener(latLng -> {
            if (pickupMarker == null) {
                pickupMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Pickup Location"));
                Toast.makeText(getContext(), "Pickup Location Selected", Toast.LENGTH_SHORT).show();
            } else if (destinationMarker == null) {
                destinationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Destination Location"));
                Toast.makeText(getContext(), "Destination Location Selected", Toast.LENGTH_SHORT).show();
            } else if (stopMarker == null) {
                stopMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Stop Location"));
                Toast.makeText(getContext(), "Stop Location Selected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "All locations selected. Clear to reset.", Toast.LENGTH_SHORT).show();
            }
        });

        // Long press to clear all markers
        mMap.setOnMapLongClickListener(latLng -> {
            if (mMap != null) {
                mMap.clear();
                pickupMarker = null;
                destinationMarker = null;
                stopMarker = null;
                Toast.makeText(getContext(), "All locations cleared.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
