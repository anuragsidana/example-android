package com.hypertrack.example_android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by piyush on 30/09/16.
 */
public class SharedPreferenceStore {
    private static final String PREF_NAME = "io.hypertrack.example_android.driver";

    private static final String DRIVER_ID_KEY = "driver_id";
    private static final String TRIP_ID_KEY = "trip_id";
    private static final String SHIFT_ID_KEY = "shift_id";

    public static void setDriverID(Context context, String driverID) {
        if (TextUtils.isEmpty(driverID))
            return;

        SharedPreferences.Editor editor = getEditor(context);

        editor.putString(DRIVER_ID_KEY, driverID);
        editor.commit();
    }

    public static String getDriverID(Context context) {
        return getSharedPreferences(context).getString(DRIVER_ID_KEY, null);
    }

    public static void clearDriverID(Context context) {
        SharedPreferences.Editor editor = getEditor(context);

        editor.remove(DRIVER_ID_KEY);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    public static void setTripID(Context context, String tripID) {
        if (TextUtils.isEmpty(tripID))
            return;

        SharedPreferences.Editor editor = getEditor(context);

        editor.putString(TRIP_ID_KEY, tripID);
        editor.commit();
    }

    public static String getTripID(Context context) {
        return getSharedPreferences(context).getString(TRIP_ID_KEY, null);
    }

    public static void clearTripID(Context context) {
        SharedPreferences.Editor editor = getEditor(context);

        editor.remove(TRIP_ID_KEY);
        editor.apply();
    }

    public static void setShiftID(Context context, String shiftID) {
        if (TextUtils.isEmpty(shiftID))
            return;

        SharedPreferences.Editor editor = getEditor(context);

        editor.putString(SHIFT_ID_KEY, shiftID);
        editor.commit();
    }

    public static String getShiftID(Context context) {
        return getSharedPreferences(context).getString(SHIFT_ID_KEY, null);
    }

    public static void clearShiftID(Context context) {
        SharedPreferences.Editor editor = getEditor(context);

        editor.remove(SHIFT_ID_KEY);
        editor.apply();
    }
}
