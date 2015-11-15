package com.neilvohra.asdghowns.ridealert;

import android.app.Application;

import java.util.ArrayList;

/**
 * Stores global variables
 */
public class RideAlertApplication extends Application {
    public static ArrayList<AlertContactObject> phoneNumbersWaitingOnAddressesFrom = new ArrayList<>();
    public static ArrayList<AlertContactObject> activeAlerts = new ArrayList<>();
    public static LocationTrackerService service;
}
