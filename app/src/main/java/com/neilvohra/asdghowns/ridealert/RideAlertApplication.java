package com.neilvohra.asdghowns.ridealert;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

/**
 * Stores global variables
 */
public class RideAlertApplication extends Application {
    public static ArrayList<AlertContactObject> phoneNumbersWaitingOnAddressesFrom = new ArrayList<>();
    public static ArrayList<AlertContactObject> activeServices = new ArrayList<>();
    public static LocationTrackerService service;
    public static int currentDrawerListOption;

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
