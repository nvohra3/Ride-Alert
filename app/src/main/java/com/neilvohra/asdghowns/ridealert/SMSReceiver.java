package com.neilvohra.asdghowns.ridealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;

import java.util.List;

public class SMSReceiver extends BroadcastReceiver implements GeocoderTaskCallback {
    private AlertContactObject obj;

    @Override
    public void onReceive(Context context, Intent intent) {
        // get SMS data, if bundle is null then there is no data so return
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        // extract SMS data from bundle
        Object[] pdus = (Object[]) extras.get("pdus");
        for (int i = 0; i < pdus.length; i++)
        {
            SmsMessage SMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = getNonFormattedNumber(SMessage.getOriginatingAddress());
            System.out.println(sender);
            String body = SMessage.getMessageBody();
            // if there's an SMS from this number then send
            for (int j = 0; j < RideAlertApplication.phoneNumbersWaitingOnAddressesFrom.size(); j++)
            {
                obj = RideAlertApplication.phoneNumbersWaitingOnAddressesFrom.get(i);
                String contactNumber = obj.getContactNumber();
                if (PhoneNumberUtils.compare(sender, contactNumber))
                {
                    GeocoderTask task = new GeocoderTask(RideAlertApplication.getContext(), this, body);
                    task.execute();
                }
            }
        }
    }

    public void onTaskCompletion(final boolean success, List<Address> addresses){
        if (!success)
            return;

        Intent intent = new Intent(RideAlertApplication.getContext(), LocationTrackerService.class);
        intent.putExtra("AlertFriendObj", new AlertContactObject(obj.getContactName(), obj.getContactNumber(), addresses.get(0)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        RideAlertApplication.getContext().startService(intent);
        RideAlertApplication.phoneNumbersWaitingOnAddressesFrom.remove(obj.getContactNumber());
    }

    private String getNonFormattedNumber(String formattedNumber) {
        String contactNumber = "";
        for (int i = 0; i < formattedNumber.length(); i++)
            if (Character.isDigit(formattedNumber.charAt(i)))
                contactNumber += formattedNumber.charAt(i);
        return contactNumber;
    }
}