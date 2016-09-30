package io.hypertrack.example_android.driver.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by piyush on 30/09/16.
 */
public class SharedPreferenceStore {
    private static final String PREF_NAME = "io.hypertrack.example_android.driver";

    private static final String DRIVER_ID_KEY = "driver_id";

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
}
