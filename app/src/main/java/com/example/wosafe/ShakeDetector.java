package com.example.wosafe;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_COUNT = 3;
    private static final int SHAKE_TIME_LAPSE = 1000; // in milliseconds

    private int shakeCount = 0;
    private long lastShakeTime = 0;

    private final OnShakeListener shakeListener;

    public interface OnShakeListener {
        void onShakeDetected();
    }

    public ShakeDetector(OnShakeListener listener) {
        this.shakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gForce = (float) Math.sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH;

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            long now = System.currentTimeMillis();

            if (lastShakeTime + SHAKE_TIME_LAPSE > now) return;

            lastShakeTime = now;
            shakeCount++;

            if (shakeCount >= SHAKE_COUNT) {
                shakeListener.onShakeDetected();
                shakeCount = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
