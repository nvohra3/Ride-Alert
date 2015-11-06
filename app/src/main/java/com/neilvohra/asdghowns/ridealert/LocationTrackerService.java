package com.neilvohra.asdghowns.ridealert;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationTrackerService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = "LocationTrackerService";
    private final double METERS_PER_MILE = 0.000621371;

    private GoogleApiClient client;

    private LocationRequest locationRequest;

    private AlertContactObject alertFriendObj;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            alertFriendObj = (AlertContactObject) bundle.get("AlertFriendObj");
        }
        RideAlertApplication.activeServices.add(this);
        System.out.println("Size: " + RideAlertApplication.activeServices.size());
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        RideAlertApplication.activeServices.remove(this);
        System.out.println("Service Destroyed");
    }

    protected void initialize() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
        client.connect();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        startLocationUpdates();
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
        Log.d(TAG, "Location changed.");
        if (location == null)
            return;

        Address contactAddress = alertFriendObj.getContactAddress();
        float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                contactAddress.getLatitude(), contactAddress.getLongitude(), results);
        if (results[0] * METERS_PER_MILE < 1)
        {
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
            String message = appSharedPrefs.getString("rideAlertMessage", null);
            SmsManager.getDefault().sendTextMessage(alertFriendObj.getContactNumber(), null, message, null, null);
            // Remove AlertFriendObject once the contact has been sent a text
            onDestroy();
            Log.d("LocationTrackerService", "Text message sent to " + alertFriendObj.getContactNumber());
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        client.connect();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
    }

    public AlertContactObject getAlertObject() {
        return alertFriendObj;
    }
}
