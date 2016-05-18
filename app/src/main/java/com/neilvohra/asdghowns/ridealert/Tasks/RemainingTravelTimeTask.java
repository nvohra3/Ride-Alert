package com.neilvohra.asdghowns.ridealert.Tasks;

import android.location.Address;
import android.os.AsyncTask;

import com.neilvohra.asdghowns.ridealert.LocationTrackerService;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemainingTravelTimeTask extends AsyncTask<Void, Void, Boolean> {
    private LocationTrackerService callback;
    private Address userAddress, contactAddress;
    private double remainingTravelTimeMinutes;
    private int activeAlertsIndex;

    /**
     * @param callback
     * @param contactAddress
     * @param userAddress
     */
    public RemainingTravelTimeTask(LocationTrackerService callback, Address userAddress,
                                   Address contactAddress, int activeAlertsIndex) {
        this.callback = callback;
        this.userAddress = userAddress;
        this.contactAddress = contactAddress;
        this.activeAlertsIndex = activeAlertsIndex;

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String response = "";
        double userLatitude = userAddress.getLatitude();
        double userLongitude = userAddress.getLongitude();
        double contactLatitude = contactAddress.getLatitude();
        double contactLongitude = contactAddress.getLongitude();
        try
        {
            StringBuilder stringBuilderURL = new StringBuilder();
            stringBuilderURL.append("https://maps.googleapis.com/maps/api/distancematrix/json?origins=");
            stringBuilderURL.append(String.valueOf(userLatitude));
            stringBuilderURL.append(",");
            stringBuilderURL.append(String.valueOf(userLongitude));
            stringBuilderURL.append("&destinations=");
            stringBuilderURL.append(String.valueOf(contactLatitude));
            stringBuilderURL.append(",");
            stringBuilderURL.append(String.valueOf(contactLongitude));
            System.out.println(stringBuilderURL.toString());

            URL url = new URL(stringBuilderURL.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try
            {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                response = readStream(in);
            }
            finally {
                urlConnection.disconnect();
            }

            JSONObject json = new JSONObject(response);
            // travelTimeString represents the remaining travel time in seconds
            double travelTimeSeconds = (double) json.getJSONArray("rows").getJSONObject(0).
                    getJSONArray("elements").getJSONObject(0).getJSONObject("duration").get("value");
            remainingTravelTimeMinutes = travelTimeSeconds / 60;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        callback.onTaskCompletion(success, remainingTravelTimeMinutes, activeAlertsIndex);
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}