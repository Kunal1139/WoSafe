package com.example.wosafe;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

public class LocationService extends Service implements LocationListener {

    private static final String TAG = "LocationService";
    private LocationManager locationManager;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String address = "";

    private String contact1, contact2, contact3;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service started");

        loadSavedContacts();
        vibrateDevice(5000); // vibrate for 5 seconds
        getLocation();
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else {
                Log.e(TAG, "Location permission not granted.");
                stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    private void vibrateDevice(long milliseconds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager) getSystemService(VIBRATOR_MANAGER_SERVICE);
            Vibrator vibrator = vibratorManager.getDefaultVibrator();
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(milliseconds);
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        Log.d(TAG, "Location: " + latitude + ", " + longitude);
        locationManager.removeUpdates(this); // stop further updates

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(this::sendSosMessage, 2000);
    }

    private void sendSosMessage() {
        if (!hasPermission()) {
            Log.e(TAG, "Missing permission to send SMS or get location.");
            stopSelf();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        String message = "🚨 Emergency SOS! I need help. My location: https://maps.google.com/?q=" +
                latitude + "," + longitude + " Address: " + address;

        try {
            if (contact1 != null && !contact1.isEmpty()) smsManager.sendTextMessage(contact1, null, message, null, null);
            if (contact2 != null && !contact2.isEmpty()) smsManager.sendTextMessage(contact2, null, message, null, null);
            if (contact3 != null && !contact3.isEmpty()) smsManager.sendTextMessage(contact3, null, message, null, null);

            Toast.makeText(this, "SOS sent from Service", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "SMS failed", e);
            Toast.makeText(this, "Failed to send SOS", Toast.LENGTH_SHORT).show();
        } finally {
            stopSelf();
        }
    }

    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void loadSavedContacts() {
        SharedPreferences prefs = getSharedPreferences("ContactsPrefs", MODE_PRIVATE);
        contact1 = prefs.getString("contact1", null);
        contact2 = prefs.getString("contact2", null);
        contact3 = prefs.getString("contact3", null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
