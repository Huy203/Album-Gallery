package com.example.albumgallery.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharePreferenceHelper {
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String DARK_MODE_KEY = "darkMode";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isDarkModeEnabled(Context context) {
        Log.v("SharePreferenceHelper", "isDarkModeEnabled");
        return getSharedPreferences(context).getBoolean(DARK_MODE_KEY, false);
    }

    public static void setDarkModeEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(DARK_MODE_KEY, enabled);
        editor.apply();
    }
}