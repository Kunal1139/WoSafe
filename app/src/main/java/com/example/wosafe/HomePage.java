package com.example.wosafe;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomePage extends Fragment{

    private Button button1;
    private static final String TAG = "HomePage";
    LocationManager locationManager;

    private String contact1, contact2, contact3;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String address;
    private ActivityResultLauncher<String> locationPermissionLauncher;

    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private LocationCallback locationCallback;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            contact1 = getArguments().getString("contact_number1");
            contact2 = getArguments().getString("contact_number2");
            contact3 = getArguments().getString("contact_number3");
        }

        if (contact1 == null || contact2 == null || contact3 == null) {
            loadSavedContacts();
        }

//        locationPermissionLauncher = registerForActivityResult(
//                new ActivityResultContracts.RequestPermission(),
//                isGranted -> {
//                    if (isGranted) {
//                        Toast.makeText(getContext(), "Location Permission Granted", Toast.LENGTH_SHORT).show();
//                        // Start your location logic here
//                    } else {
//                        Toast.makeText(getContext(), "Location Permission Denied", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS
            },100);
        }

//         Check and request permission right here
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            Toast.makeText(getContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
            // Start your location logic here
        }


        // Handle Android 14+ Foreground location permission (if required)

    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("WoSafe");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        button1 = view.findViewById(R.id.button1);



        // SOS button click
        button1.setOnClickListener(view1 -> {
            Toast.makeText(getActivity(), "Button Clicked", Toast.LENGTH_SHORT).show();
            if (checkPermissions()) {
                Log.d(TAG,"buttonclicked");
                getLocation();


                vibrateDevice(2000);
            } else {
                Log.d(TAG, "Requesting permissions...");
//                ActivityCompat.requestPermissions(requireActivity(),new String[](Manifest.permission.SEND_SMS);
            }
        });

        return view;
    }

//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//        Log.d(TAG ,"location changed entered");
//        Toast.makeText(getActivity(), ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
//        try {
//            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
//            String address = addresses.get(0).getAddressLine(0);
//
//            Log.d(TAG ,"latitude" + location.getLatitude());
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
////        latitude = location.getLatitude();
////        longitude = location.getLongitude();
////        Log.d(TAG, "Location updated: " + latitude + ", " + longitude);
////        Toast.makeText(getActivity(), "Location Changed entered", Toast.LENGTH_SHORT).show();
////        locationManager.removeUpdates(this);
////
////        try{
////            Toast.makeText(getActivity(), "Try block entered", Toast.LENGTH_SHORT).show();
////            Geocoder geocoder=new Geocoder(requireContext(), Locale.getDefault());
////            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
////            address = addresses.get(0).getAddressLine(0);
////
////            if ((latitude !=0.0) && (longitude != 0.0)) {
////                Log.d(TAG, "Location to send: " + latitude + ", " + longitude);
////                Log.d(TAG, "Address to send: " + address);
////                Toast.makeText(getActivity(), "Long Lat printed", Toast.LENGTH_SHORT).show();
////                Toast.makeText(getActivity(), "latitude :" + latitude, Toast.LENGTH_SHORT).show();
////                new Handler().postDelayed(this::sendSosMessage, 3000);
////            }else {
////                Toast.makeText(getActivity(), "Nothing found", Toast.LENGTH_SHORT).show();
////            }
////
////        }catch (Exception e){
////            e.printStackTrace();
////
////        }
////        new Handler().postDelayed(this::sendSosMessage, 10000);
//
//        // You can send this updated location via SMS if needed
//    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }






    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Check for permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        // Check if internet is available
        if (isInternetAvailable()) {
            // Set up location request for real-time location updates
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(60000) // 60 seconds
                    .setFastestInterval(1000) // 1 second
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // Location callback to handle location updates
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult != null && locationResult.getLocations() != null) {
                        for (android.location.Location location : locationResult.getLocations()) {
                            // Handle location updates here
                            Log.d(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
                            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String address = addresses != null ? addresses.get(0).getAddressLine(0) : "Unknown address";
                            sendSosMessage(location.getLatitude(), location.getLongitude(), address);
                        }
                    }
                }
            };

            // Request location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            Log.d(TAG, "getLocation success");
        } else {
            // If no internet, use the last known location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            Log.d(TAG, "Last known location: " + location.getLatitude() + ", " + location.getLongitude());
                            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String address = addresses != null ? addresses.get(0).getAddressLine(0) : "Unknown address";
                            sendSosMessage(location.getLatitude(), location.getLongitude(), address);
                        } else {
                            Log.e(TAG, "No location data available.");
                        }
                    });
        }
    }


    private void vibrateDevice(long milliseconds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            Vibrator vibrator = vibratorManager.getDefaultVibrator();
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(milliseconds);
            }
        }
    }





    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }


    private void sendSosMessage(double lat, double lng,String address) {
        if (contact1 == null && contact2 == null && contact3 == null) {
            Toast.makeText(getActivity(), "No contacts saved", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkPermissions()) {
            Log.d(TAG, "Permissions not granted.");
            return;
        }

        Log.d(TAG, "Location to send: " + lat + ", " + lng);
        Log.d(TAG, "Address to send: " + address);

        SmsManager smsManager=SmsManager.getDefault();
        String message = "🚨 Emergency SOS! I need help. " + lat + lng;

        try {


            if (contact1 != null && !contact1.isEmpty()) {
                smsManager.sendTextMessage(contact1, null, message, null, null);
            }
            if (contact2 != null && !contact2.isEmpty()) {
                smsManager.sendTextMessage(contact2, null, message, null, null);
            }
            if (contact3 != null && !contact3.isEmpty()) {
                smsManager.sendTextMessage(contact3, null, message, null, null);
            }
            Toast.makeText(getActivity(), "SOS sent successfully", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "SOS SMS attempt made");
        }
        catch(Exception e){
            Toast.makeText(getActivity(), "SOS request Unsuccessful", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "SMS failed", e);
        };
    }

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