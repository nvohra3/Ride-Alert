package com.neilvohra.asdghowns.ridealert;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores global variables
 */
public class RideAlertApplication extends Application {
    // Key - Unformatted phone number
    public static Map<String, AlertContactObject> phoneNumbersWaitingOnAddressesFrom = new HashMap<>();
    public static ArrayList<AlertContactObject> activeAlerts = new ArrayList<>();
    public static LocationTrackerService service;
}
