package com.example.wosafe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SmsSentReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsSentReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int resultCode = getResultCode();
        Log.d(TAG, "SMS Sending Result Code: " + resultCode);

        switch (resultCode) {
            case android.app.Activity.RESULT_OK:
                Toast.makeText(context, "✅ SMS Sent Successfully!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "✅ SMS Sent Successfully!");
                break;

            case android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "❌ SMS Failed: Generic Failure", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "❌ SMS Failed: Generic Failure");
                break;

            case android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "❌ No Network Service!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "❌ No Network Service!");
                break;

            case android.telephony.SmsManager.RESULT_ERROR_NULL_PDU:
                Toast.makeText(context, "❌ SMS Failed: Null PDU", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "❌ SMS Failed: Null PDU");
                break;

            case android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF:
                Toast.makeText(context, "❌ SMS Failed: Radio Off", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "❌ SMS Failed: Radio Off");
                break;

            default:
                Toast.makeText(context, "❌ Unknown SMS Failure", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "❌ Unknown SMS Failure, Result Code: " + resultCode);
                break;
        }
    }
}
