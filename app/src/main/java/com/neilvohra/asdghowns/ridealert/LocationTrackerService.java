package com.neilvohra.asdghowns.ridealert;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.neilvohra.asdghowns.ridealert.Tasks.RemainingTravelTimeTask;
import com.neilvohra.asdghowns.ridealert.Tasks.RemainingTravelTimeTaskCallback;

import java.util.ArrayList;
import java.util.Locale;

public class LocationTrackerService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, RemainingTravelTimeTaskCallback {

    private final String TAG = "LocationTrackerService";
    private final double METERS_PER_MILE = 0.000621371;

    private GoogleApiClient client;

    private LocationRequest locationRequest;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize();
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
    }

    protected void initialize() {
        if (client == null)
        {
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        createLocationRequest();
        client.connect();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        // Get location updates at around every block
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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
        Address userAddress = new Address(Locale.ENGLISH);
        double userLatitude = location.getLatitude();
        double userLongitude = location.getLongitude();
        // Only need user longitude/latitude for now, so not going to do a reverse geocode for full address
        userAddress.setLatitude(userLatitude);
        userAddress.setLongitude(userLongitude);
        ArrayList<AlertContactObject> activeAlerts = RideAlertApplication.activeAlerts;
        for (int i = 0; i < activeAlerts.size(); i++)
        {
            Address contactAddress = activeAlerts.get(i).getContactAddress();
            float[] results = new float[1];
            double contactLatitude = contactAddress.getLatitude();
            double contactLongitude = contactAddress.getLongitude();
            Location.distanceBetween(userLatitude, userLongitude,
                    contactLatitude, contactLongitude, results);
            // When the driver is about 2 miles away, start seeing if driving distance < 5 minutes
            if (results[0] * METERS_PER_MILE < 2)
            {
                RemainingTravelTimeTask task = new RemainingTravelTimeTask(this,
                        userAddress, contactAddress, i);
                task.execute();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        client.connect();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
    }

    public void onTaskCompletion(final boolean success, int remainingTravelTime, int activeAlertsIndex) {
        // If the user is 5 minutes away from the contact, send the alert
        if (remainingTravelTime <= 5)
        {
            ArrayList<AlertContactObject> activeAlerts = RideAlertApplication.activeAlerts;
            String message = getString(R.string.come_outside);
            SmsManager.getDefault().sendTextMessage(
                    activeAlerts.get(activeAlertsIndex).getContactNumber(), null, message, null, null);
            Log.d(TAG, "Text message sent to " + activeAlerts.get(activeAlertsIndex).getContactNumber());
            // Remove service from list of current services once the contact has been sent a text
            activeAlerts.remove(activeAlertsIndex);
            if (activeAlerts.size() == 0)
            {
                onDestroy();
            }
        }
    }
}
