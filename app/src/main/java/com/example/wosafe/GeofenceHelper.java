package com.example.wosafe;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class GeofenceHelper extends ContextWrapper {

    private static final String TAG="GeofenceHelper";
    PendingIntent pendingIntent;


    public GeofenceHelper(Context base) {
        super(base);
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence) {
        // Create a new list of geofences
        List<Geofence> geofences = new ArrayList<>();

        // Add the passed geofence to the list
        geofences.add(geofence);

        // Return the geofencing request with the list of geofences
        return new GeofencingRequest.Builder()
                .addGeofences(geofences)  // Use the list that contains the single geofence
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }


    public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitionTypes) {
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude,latLng.longitude, radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    public PendingIntent getPendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);

        return pendingIntent;
    }


    public String getErrorString (Exception e){
        if(e instanceof ApiException){
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()){

                case GeofenceStatusCodes.GEOFENCE_INSUFFICIENT_LOCATION_PERMISSION:
                    return " GEOFENCE INSUFFICIENT PERMISSION !! PLEASE GRANT PERMISSION FOR ALL TIME";

                case GeofenceStatusCodes
                             .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE NOT AVAILABLE";
                case GeofenceStatusCodes
                             .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                             .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";



            }
        }
        return e.getLocalizedMessage();
    }

}
