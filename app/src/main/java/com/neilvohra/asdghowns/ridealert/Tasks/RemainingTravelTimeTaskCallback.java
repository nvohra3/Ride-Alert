package com.neilvohra.asdghowns.ridealert.Tasks;

public interface RemainingTravelTimeTaskCallback {
    void onTaskCompletion(final boolean success, double remainingTravelTime, int activeAlertsIndex);
}
