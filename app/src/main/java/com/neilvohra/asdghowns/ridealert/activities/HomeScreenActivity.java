package com.neilvohra.asdghowns.ridealert.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.neilvohra.asdghowns.ridealert.AlertContactObject;
import com.neilvohra.asdghowns.ridealert.GeocoderTask;
import com.neilvohra.asdghowns.ridealert.GeocoderTaskCallback;
import com.neilvohra.asdghowns.ridealert.LocationTrackerService;
import com.neilvohra.asdghowns.ridealert.R;
import com.neilvohra.asdghowns.ridealert.RideAlertApplication;

import java.util.List;

public class HomeScreenActivity extends DrawerBaseActivity implements GeocoderTaskCallback {
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;
    private String contactName, contactNumber, contactAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        boolean firstTimeLaunched = appSharedPrefs.getBoolean("hasBeenRun", false);
        if (!firstTimeLaunched)
        {
            initialize();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        addDrawerItems();
        setupDrawer();
    }

    public void selectContact (View btnSelectContact) {
        // using native contacts selection
        // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
        // startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_CONTACTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK)
        {
            uriContact = data.getData();
            contactName = retrieveContactName();
            contactNumber = retrieveContactNumber();
            contactAddress = retrieveContactAddress();
        }

        if (contactAddress == null) // When does this happen?
        {
            return;
        }

        if (contactAddress.equals(""))
        {
            textContact();
            return;
        }
        geocode(contactAddress);
    }

    private void textContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Could not find an address for the selected contact. Would you like to " +
                "text the selected contact and ask for their address?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(HomeScreenActivity.this);
                        String getContactAddress = appSharedPrefs.getString("getContactAddress", null);
                        String contactAddressFormat = appSharedPrefs.getString("contactAddressFormat", null);
                        SmsManager.getDefault().sendTextMessage(contactNumber, null, getContactAddress, null, null);
                        SmsManager.getDefault().sendTextMessage(contactNumber, null, contactAddressFormat, null, null);
                        AlertContactObject obj = new AlertContactObject(contactName, contactNumber, null);
                        Toast.makeText(getApplicationContext(), "Text sent.", Toast.LENGTH_LONG);
                        RideAlertApplication.phoneNumbersWaitingOnAddressesFrom.add(obj);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String retrieveContactNumber() {
        String formattedContactNumber = null;
        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst())
        {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
        cursorID.close();

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                        new String[]{contactID},
                        null);

        if (cursorPhone.moveToFirst())
        {
            formattedContactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        System.out.println(formattedContactNumber);
        String contactNumber = getNonFormattedNumber(formattedContactNumber);
        return contactNumber;
    }

    private String retrieveContactName() {
        String contactName = null;
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
        if (cursor.moveToFirst())
        {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();
        return contactName;
    }

    private String retrieveContactAddress() {
        Uri postal_uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        Cursor postal_cursor  = getContentResolver().query(postal_uri,null,  ContactsContract.Data.CONTACT_ID + "=" + contactID.toString(), null,null);
        String address = null;
        while(postal_cursor.moveToNext())
        {
            address = postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
            String city = postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
            if (city != null)
                address += " " + city;
        }
        postal_cursor.close();
        return address;
    }

    private void geocode(String address) {
        GeocoderTask task = new GeocoderTask(getApplicationContext(), this, address);
        task.execute();
    }

    public void onTaskCompletion(final boolean success, List<Address> addresses) {
//        startService(new Intent(getBaseContext(), LocationTrackerService.class));
        Intent intent = new Intent(getBaseContext(), LocationTrackerService.class);
        intent.putExtra("AlertFriendObj", new AlertContactObject(contactName, contactNumber, addresses.get(0)));
        startService(intent);
        Toast.makeText(HomeScreenActivity.this, "Alert has been successfully set up. " +
                "Go to View Alerts to see your active alerts.", Toast.LENGTH_LONG).show();
    }

    /**
     * Run the first time the app is opened
     */
    private void initialize() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        prefsEditor.putString("rideAlertMessage", "Hey, I'll be there in 5 minutes. Come outside.");
        prefsEditor.putString("getContactAddress", "Hey, can I get your address? I'll send you a " +
                "text when I'm a few minutes away from your house to give you a heads up.");
        prefsEditor.putString("contactAddressFormat", "Format it in the following form: " +
                "Street, City");
        prefsEditor.putBoolean("hasBeenRun", true);
        prefsEditor.commit();
    }

    private String getNonFormattedNumber(String formattedNumber) {
        String contactNumber = "";
        for (int i = 0; i < formattedNumber.length(); i++)
            if (Character.isDigit(formattedNumber.charAt(i)))
                contactNumber += formattedNumber.charAt(i);
        return contactNumber;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_active_alerts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}