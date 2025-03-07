package com.example.wosafe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        //Initialize map fragment
        SupportMapsFragment supportMapsFragment = (SupportMapsFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        //Async map
        supportMapsFragment.getMapAsync(new onMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //When map is loaded
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        //when clicked on map
                        //Initialize marker options
                        MarkerOptions markerOptions = new MarkerOptions();
                        //Set position of marker
                        markerOptions.position(latLng);
                        //Set title of marker
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        //Remove all marker
                        googleMap.clear();
                        //Animating to zoom the marker
                        googleMap.animateCamera(CameraUpdateFactory.newLatlngZoom(
                                latLng, 10
                        ));
                        //Add marker on map
                        googleMap.addMarker(markerOptions);
                    }

                });

            }
        });


        //Return view
        return view;
    }
}