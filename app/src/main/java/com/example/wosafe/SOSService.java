package com.example.wosafe;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class SOSService extends Service {

    FusedLocationProviderClient fusedLocationProviderClient;
    String contact1 = "1234567890";  // Replace with actual emergency numbers
    String contact2 = "0987654321";
    String contact3 = "1122334455";

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        sendSOS();
    }

    private void sendSOS() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    String sosMessage;
                    if (isInternetAvailable()) {
                        sosMessage = "Emergency SOS! Need Help! Live Location: https://maps.google.com/?q=" + latitude + "," + longitude;
                    } else {
                        sosMessage = "Emergency SOS! Need Help! My GPS Coordinates: Latitude: " + latitude + ", Longitude: " + longitude;
                    }

                    sendSms(sosMessage);
                }
            }
        });
    }

    private void sendSms(String message) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
            if (!contact1.isEmpty()) smsManager.sendTextMessage(contact1, null, message, null, null);
            if (!contact2.isEmpty()) smsManager.sendTextMessage(contact2, null, message, null, null);
            if (!contact3.isEmpty()) smsManager.sendTextMessage(contact3, null, message, null, null);
            Toast.makeText(this, "SOS Sent Successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SOS!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
