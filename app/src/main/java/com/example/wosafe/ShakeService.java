package com.example.wosafe;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class ShakeService extends Service {

    private SensorManager sensorManager;
    private ShakeDetector shakeDetector;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ShakeServiceChannel",
                    "Shake Detection Service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(this, "ShakeServiceChannel")
                    .setContentTitle("Shake detection active")
                    .setContentText("We'll send SOS if you shake your phone")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build();

            startForeground(1, notification);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        shakeDetector = new ShakeDetector(() -> {
            Log.d("ShakeService", "Shake detected!");
            sendSosFromService();
        });

        if (accelerometer != null)
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }


    private void sendSosFromService() {
        SharedPreferences sharedPreferences = getSharedPreferences("ContactsPrefs", MODE_PRIVATE);
        String contact1 = sharedPreferences.getString("contact1", null);
        String contact2 = sharedPreferences.getString("contact2", null);
        String contact3 = sharedPreferences.getString("contact3", null);

        if (contact1 == null && contact2 == null && contact3 == null) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            String locationMessage = (location != null) ?
                    "My Location: https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude() :
                    "Location not available";

            String sosMessage = "🚨 Emergency SOS! Please help me! " + locationMessage;
            SmsManager smsManager = SmsManager.getDefault();

            try {
                if (contact1 != null && !contact1.isEmpty()) {
                    smsManager.sendTextMessage(contact1, null, sosMessage, null, null);
                }
                if (contact2 != null && !contact2.isEmpty()) {
                    smsManager.sendTextMessage(contact2, null, sosMessage, null, null);
                }
                if (contact3 != null && !contact3.isEmpty()) {
                    smsManager.sendTextMessage(contact3, null, sosMessage, null, null);
                }

                Log.d("ShakeService", "SOS sent via shake.");
            } catch (Exception e) {
                Log.e("ShakeService", "Failed to send SOS", e);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(shakeDetector);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
