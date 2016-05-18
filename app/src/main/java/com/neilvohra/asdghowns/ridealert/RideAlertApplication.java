package com.neilvohra.asdghowns.ridealert;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores global variables
 */
public class RideAlertApplication extends Application {
    public static List<AlertContactObject> phoneNumbersWaitingOnAddressesFrom = new ArrayList<>();
    public static ArrayList<AlertContactObject> activeAlerts = new ArrayList<>();
    public static LocationTrackerService service;
}
