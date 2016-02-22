package com.neilvohra.asdghowns.ridealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.neilvohra.asdghowns.ridealert.Tasks.GeocoderTask;
import com.neilvohra.asdghowns.ridealert.Tasks.GeocoderTaskCallback;

import java.util.ArrayList;
import java.util.List;

public class SMSReceiver extends BroadcastReceiver implements GeocoderTaskCallback {
    private AlertContactObject obj;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // get SMS data, if bundle is null then there is no data so return
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
            for (int j = 0; j < RideAlertApplication.phoneNumbersWaitingOnAddressesFrom.size(); j++)
            {
                obj = RideAlertApplication.phoneNumbersWaitingOnAddressesFrom.get(i);
                String contactNumber = obj.getContactNumber();
                if (PhoneNumberUtils.compare(sender, contactNumber))
                {
                    RideAlertApplication.phoneNumbersWaitingOnAddressesFrom.remove(obj);
                    GeocoderTask task = new GeocoderTask(context, this, address);
                    task.execute();
                    return;
                }
            }
        }
    }

    public void onTaskCompletion(final boolean success, List<Address> addresses){
        if (!success)
            return;

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
}