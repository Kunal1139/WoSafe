package com.example.wosafe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class PowerButtonReceiver extends BroadcastReceiver {
    private static int pressCount = 0;
    private static long lastPressTime = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        long currentTime = System.currentTimeMillis();

        if (lastPressTime == 0 || currentTime - lastPressTime > 5000) {
            // Reset count if 5 seconds have passed
            pressCount = 0;
        }

        lastPressTime = currentTime;
        pressCount++;

        Log.d("PowerButtonReceiver", "Press count: " + pressCount);

        if (pressCount >= 5) {
            // Reset the counter
            pressCount = 0;

            // Trigger SOS or any action
            Toast.makeText(context, "SOS Triggered!", Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(context, LocationService.class);
            context.startService(new Intent(context, LocationService.class));

            // context.startActivity(new Intent(context, YourSOSActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
