package com.example.wosafe;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG ="MapsActivity";
    private String contact1,contact2;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "GeoFence Triggered ", Toast.LENGTH_SHORT).show();


//            contact1 = intent.getStringExtra("contact1");
//            contact2 = intent.getStringExtra("contact2");
//
//
//        if (contact1 == null || contact2 == null) {
//            loadSavedContacts(context);
//        }

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive : Error receiving geofence event ");
            return;
        }


        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }

        Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                break;

            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                //sendMessage(context);
                break;
        }
    }
//    private void sendMessage(Context context) {
//        if (contact1 == null && contact2 == null) {
//            Toast.makeText(context, "No contacts saved", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Log.d(TAG, "SMS triggered for exit");
//
//
//        SmsManager smsManager = SmsManager.getDefault();
//        String message = "🚨 User has stepped out of the location radius.";
//
//        try {
//            if (contact1 != null && !contact1.isEmpty()) {
//                smsManager.sendTextMessage(contact1, null, message, null, null);
//            }
//            if (contact2 != null && !contact2.isEmpty()) {
//                smsManager.sendTextMessage(contact2, null, message, null, null);
//            }
//            Toast.makeText(context, "Notification sent successfully", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "SMS attempt made");
//        } catch (Exception e) {
//            Toast.makeText(context, "SMS notification request unsuccessful", Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "SMS failed", e);
//        }
//    }
//
//    private void loadSavedContacts(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("ContactsPrefs", Context.MODE_PRIVATE);
//        contact1 = sharedPreferences.getString("contact1", null);
//        contact2 = sharedPreferences.getString("contact2", null);
//
//        if (contact1 == null && contact2 == null) {
//            Toast.makeText(context, "No saved contacts found", Toast.LENGTH_SHORT).show();
//        } else {
//            Log.d(TAG, "Loaded Contact 1: " + contact1);
//            Log.d(TAG, "Loaded Contact 2: " + contact2);
//        }
//    }





}
