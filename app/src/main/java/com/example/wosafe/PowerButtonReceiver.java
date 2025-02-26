package com.example.wosafe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class PowerButtonReceiver extends BroadcastReceiver {

    private static int pressCount = 0;
    private static long lastPressTime = 0;
    private static final int THRESHOLD_TIME = 3000; // 3 seconds window

    @Override
    public void onReceive(Context context, Intent intent) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPressTime < THRESHOLD_TIME) {
            pressCount++;
        } else {
            pressCount = 1; // Reset count if too much time has passed
        }

        lastPressTime = currentTime;

        if (pressCount == 3) {
            pressCount = 0; // Reset the count after triggering
            Toast.makeText(context, "SOS Triggered via Power Button!", Toast.LENGTH_SHORT).show();

            // Start the SOS Function
            Intent sosIntent = new Intent(context, SOSService.class);
            context.startService(sosIntent);
        }
    }
}
