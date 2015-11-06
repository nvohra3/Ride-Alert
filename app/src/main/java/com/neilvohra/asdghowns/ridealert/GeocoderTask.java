package com.neilvohra.asdghowns.ridealert;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeocoderTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private GeocoderTaskCallback callback;
    private List<Address> addresses = new ArrayList<Address>();
    private String contactAddress;

    /**
     * @param context
     * @param callback
     * @param contactAddress
     */
    public GeocoderTask(Context context, GeocoderTaskCallback callback, String contactAddress ) {
        this.context = context;
        this.callback = callback;
        this.contactAddress = contactAddress;

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocationName(contactAddress, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * On success, execute task to locate nearby stores
     * @param success if task successfully executed
     */
    @Override
    protected void onPostExecute(final Boolean success) {
        callback.onTaskCompletion(success, addresses);
    }
}