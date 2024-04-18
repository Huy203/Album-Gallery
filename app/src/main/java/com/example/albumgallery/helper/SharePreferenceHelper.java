package com.example.albumgallery.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceHelper {
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String DARK_MODE_KEY = "darkMode";
    private static final String GRID_LAYOUT_KEY = "gridLayout";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isDarkModeEnabled(Context context) {
        return getSharedPreferences(context).getBoolean(DARK_MODE_KEY, false);
    }

    public static void setDarkModeEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(DARK_MODE_KEY, enabled);
        editor.apply();
    }

    public static String isGridLayoutEnabled(Context context) {
        return getSharedPreferences(context).getString(GRID_LAYOUT_KEY, "default");
    }

    public static void setGridLayoutEnabled(Context context, String enabled) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(GRID_LAYOUT_KEY, enabled);
        editor.apply();
    }
}