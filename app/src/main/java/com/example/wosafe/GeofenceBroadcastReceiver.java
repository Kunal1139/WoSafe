package com.example.wosafe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG ="MapsActivity";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"GeoFence Triggered ",Toast.LENGTH_SHORT).show();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()){
            Log.d(TAG,"onReceive : Error receiving geofence event ");
            return;
        }

        List<Geofence> geofenceList=geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList){
            Log.d(TAG,"onReceive :" + geofence.getRequestId());
        }
        int transitonType = geofencingEvent.getGeofenceTransition();

        switch (transitonType){
            case  Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context,"GEOFENCE_TRANSITION_ENTER",Toast.LENGTH_SHORT).show();
                break;
            case  Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context,"GEOFENCE_TRANSITION_DWELL",Toast.LENGTH_SHORT).show();
                break;
            case  Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context,"GEOFENCE_TRANSITION_EXIT",Toast.LENGTH_SHORT).show();
                break;

        }
    }


}
