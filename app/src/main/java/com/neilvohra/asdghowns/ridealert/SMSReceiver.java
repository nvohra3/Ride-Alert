package com.neilvohra.asdghowns.ridealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.neilvohra.asdghowns.ridealert.Tasks.GeocoderTask;
import com.neilvohra.asdghowns.ridealert.Tasks.GeocoderTaskCallback;

import java.util.ArrayList;
import java.util.List;

public class SMSReceiver extends BroadcastReceiver implements GeocoderTaskCallback {
    private final String TAG = "SMSReceiver";
    private AlertContactObject obj;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        // extract SMS data from bundle
        Object[] pdus = (Object[]) extras.get("pdus");
        for (int i = 0; i < pdus.length; i++)
        {
            SmsMessage SMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = getNonFormattedNumber(SMessage.getOriginatingAddress());
            String address = SMessage.getMessageBody();
            // if there's an SMS from this number then send
            obj = RideAlertApplication.phoneNumbersWaitingOnAddressesFrom.get(sender);
            if (obj != null) // True if waiting for an address from this sender
            {
                GeocoderTask task = new GeocoderTask(context, this, address);
                task.execute();
                return;
            }
        }
    }

    public void onTaskCompletion(final boolean success, List<Address> addresses){
        if (!success)
            return;

        if (addresses.size() == 0)
        {
            invalidAddress();
            return;
        } else if (addresses.size() > 1)
        {
            multipleAddressResults();
            return;
        }

        String contactName = obj.getContactName();
        String contactNumber = obj.getContactNumber();

        ArrayList<AlertContactObject> activeAlerts = RideAlertApplication.activeAlerts;
        activeAlerts.add(new AlertContactObject(contactName, contactNumber, addresses.get(0)));
        LocationTrackerService service = RideAlertApplication.service;
        if (service == null)
        {
            Intent intent = new Intent(context, LocationTrackerService.class);
            context.startService(intent);
        }

        RideAlertApplication.phoneNumbersWaitingOnAddressesFrom.remove(contactNumber);
        Toast.makeText(context, context.getString(R.string.alert_setup_successful),
                Toast.LENGTH_LONG).show();
    }

    private String getNonFormattedNumber(String formattedNumber) {
        String contactNumber = "";
        for (int i = 0; i < formattedNumber.length(); i++)
            if (Character.isDigit(formattedNumber.charAt(i)))
                contactNumber += formattedNumber.charAt(i);
        return contactNumber;
    }

    private void invalidAddress() {
        String message =  "ERROR: Could not recognize address. Perhaps there was a typo?";
        SmsManager.getDefault().sendTextMessage(obj.getContactNumber(), null, message, null, null);
        Log.d(TAG, "Invalid address text message sent to " + obj.getContactNumber());
    }

    private void multipleAddressResults() {
        String message =  "Hmm, I got multiple results for that address.  " +
                "Can you be a little more specific?";
        SmsManager.getDefault().sendTextMessage(obj.getContactNumber(), null, message, null, null);
        Log.d(TAG, "Multiple addresses text message sent to " + obj.getContactNumber());
    }
}