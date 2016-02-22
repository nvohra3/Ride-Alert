package com.neilvohra.asdghowns.ridealert.Tasks;

import android.location.Address;

import java.util.List;

public interface GeocoderTaskCallback {
    void onTaskCompletion(final boolean success, List<Address> addresses);
}
