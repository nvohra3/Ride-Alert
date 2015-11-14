package com.neilvohra.asdghowns.ridealert.activities;

import android.app.Activity;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.neilvohra.asdghowns.ridealert.R;

public class AlertInfoActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {

    private final double METERS_PER_MILE = 0.000621371;

    private final String TAG = "AlertInfoActivity";

    private GoogleApiClient client;
    private LocationRequest locationRequest;

    private Address contactAddress;
    private String contactNumber, contactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_info);

        contactName = (String) getIntent().getExtras().get("contactName");
        contactAddress = (Address) getIntent().getExtras().get("contactAddress");
        contactNumber = (String) getIntent().getExtras().get("contactNumber");


        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (true)
        {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "Connection failed.");
        if (!client.isConnecting())
        {
            client.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
        {
            return;
        }



        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT
        // THIS CLASS SHOULD BE CHANGED TO SHOWING DETAILS OF AN ALERT



//        TextView view = (TextView) findViewById(R.id.hello_world_1);
//        TextView view2 = (TextView) findViewById(R.id.hello_world_2);
//        TextView view3 = (TextView) findViewById(R.id.hello_world_3);
//        TextView view4 = (TextView) findViewById(R.id.hello_world_4);
//        TextView view5 = (TextView) findViewById(R.id.hello_world_5);
//        TextView view6 = (TextView) findViewById(R.id.hello_world_6);
//        float[] results = new float[1];
//        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
//                33.769561, -84.390951, results);
//        view.setText("Miles from destination: " + results[0] * METERS_PER_MILE);
//        view2.setText(location.getLongitude() + "");
//        view3.setText(location.getLatitude() + "");
//        view4.setText(contactNumber);
//        view6.setText(33.769561 + "");
//        view5.setText(-84.390951 + "");

    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        client.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (client.isConnected() && !mRequestingLocationUpdates) {
        if (client.isConnected())
        {
            startLocationUpdates();
        }
    }
}
