package com.neilvohra.asdghowns.ridealert.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.neilvohra.asdghowns.ridealert.ActiveAlertsAdapter;
import com.neilvohra.asdghowns.ridealert.AlertContactObject;
import com.neilvohra.asdghowns.ridealert.GeocoderTask;
import com.neilvohra.asdghowns.ridealert.GeocoderTaskCallback;
import com.neilvohra.asdghowns.ridealert.LocationTrackerService;
import com.neilvohra.asdghowns.ridealert.R;
import com.neilvohra.asdghowns.ridealert.RideAlertApplication;

import java.util.List;

public class HomeScreenTabActivity extends ActionBarActivity implements ActionBar.TabListener,
        GeocoderTaskCallback {
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private final String TAG = "HomeScreenTabActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Uri uriContact;
    private String contactID;
    private String contactName, contactNumber, contactAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_tab);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        });

        // Add all of the tabs and a listener to each tab
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
        {
            actionBar.addTab(actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                return true;
            case R.id.action_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.app_description))
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void selectContact (View btnSelectContact) {
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

        // Handle these cases appropriately at some point
        if (contactNumber == null)
        {
            Toast.makeText(HomeScreenTabActivity.this, getString(R.string.no_number_exists),
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (contactAddress == null || contactAddress.equals(""))
        {
            textContact();
            return;
        }

        geocode(contactAddress);
    }

    private void textContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.text_contact))
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String message = getString(R.string.request_address);
                        SmsManager.getDefault().sendTextMessage(contactNumber, null, message, null, null);
                        AlertContactObject obj = new AlertContactObject(contactName, contactNumber, null);
                        Toast.makeText(getApplicationContext(), "Text sent.", Toast.LENGTH_LONG);
                        RideAlertApplication.phoneNumbersWaitingOnAddressesFrom.add(obj);
                        Log.d(TAG, "Sent text message to " + obj.getContactName());
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
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID}, null, null, null);
        if (cursorID.moveToFirst())
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        cursorID.close();

        Cursor cursorPhone = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{contactID}, null);

        if (cursorPhone.moveToFirst())
        {
            formattedContactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        String contactNumber = null;
        if (formattedContactNumber != null)
        {
            contactNumber = getNonFormattedNumber(formattedContactNumber);
        }

        return contactNumber;
    }

    private String retrieveContactName() {
        String contactName = null;
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
        if (cursor.moveToFirst())
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        cursor.close();
        return contactName;
    }

    private String retrieveContactAddress() {
        Uri postal_uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        Cursor postal_cursor  = getContentResolver().query(postal_uri,null,
                ContactsContract.Data.CONTACT_ID + "=" + contactID.toString(), null, null);
        String address = null;
        while (postal_cursor.moveToNext())
        {
            address = postal_cursor.getString(postal_cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.StructuredPostal.STREET));
            String city = postal_cursor.getString(postal_cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.StructuredPostal.CITY));
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
        if (!success)
        {
            // Handle appropriately
        }
        RideAlertApplication.activeServices.add(new AlertContactObject(
                contactName, contactNumber, addresses.get(0)));
        LocationTrackerService service = RideAlertApplication.service;
        if (service == null)
        {
            Intent intent = new Intent(getBaseContext(), LocationTrackerService.class);
            startService(intent);
        }
        Toast.makeText(HomeScreenTabActivity.this, getString(R.string.alert_setup_successful),
                Toast.LENGTH_LONG).show();
    }

    private String getNonFormattedNumber(String formattedNumber) {
        String contactNumber = "";
        for (int i = 0; i < formattedNumber.length(); i++)
            if (Character.isDigit(formattedNumber.charAt(i)))
                contactNumber += formattedNumber.charAt(i);
        return contactNumber;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /***********************************************************************************************
     ************************************* Fragment code below *************************************
     ***********************************************************************************************/

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final int NUM_SECTIONS = 2;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int sectionNumber = position + 1;
            switch (sectionNumber) {
                case 1:
                    return MainScreenFragment.newInstance(position + 1);
                case 2:
                    return ActiveAlertsFragment.newInstance(position + 1);
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return NUM_SECTIONS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position)
            {
                case 0:
                    return getString(R.string.title_home);
                case 1:
                    return getString(R.string.title_active_alerts);
                default:
                    return null;
            }
        }
    }

    /**
     * Fragment to represent the home screen of the application where a user can set up an alert
     */
    public static class MainScreenFragment extends Fragment {
        public static MainScreenFragment newInstance(int sectionNumber) {
            MainScreenFragment fragment = new MainScreenFragment();
            return fragment;
        }

        public MainScreenFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    /**
     * Fragment to represent second tab screen which shows alerts that are currently active
     */
    public static class ActiveAlertsFragment extends Fragment {
        public static ActiveAlertsFragment newInstance(int sectionNumber) {
            ActiveAlertsFragment fragment = new ActiveAlertsFragment();
            return fragment;
        }

        public ActiveAlertsFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_active_alerts, container, false);
            ListView activeAlerts = (ListView) rootView.findViewById(R.id.active_alerts_list);
            ActiveAlertsAdapter adapter = new ActiveAlertsAdapter(getActivity());
            activeAlerts.setAdapter(adapter);
            return rootView;
        }
    }
}
