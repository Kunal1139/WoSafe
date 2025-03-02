package com.example.wosafe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e("GeofenceReceiver", "Error in geofencing event.");
            return;
        }

        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        for (Geofence geofence : triggeringGeofences) {
            String requestId = geofence.getRequestId();

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                if ("WORKPLACE".equals(requestId)) {
                    sendNotification(context, "You have entered your Workplace Location!");
                } else if ("HOME".equals(requestId)) {
                    sendNotification(context, "You have entered your Home Location!");
                }
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                if ("HOME".equals(requestId)) {
                    sendNotification(context, "You have exited your Home Location!");
                } else if ("WORKPLACE".equals(requestId)) {
                    sendNotification(context, "You have exited your Workplace Location!");
                }
            }
        }
    }

    private void sendNotification(Context context, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "geoFenceChannel")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Geofence Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1001, builder.build());
    }
}
