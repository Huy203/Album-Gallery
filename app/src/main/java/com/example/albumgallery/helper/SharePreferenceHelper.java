package com.example.albumgallery.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharePreferenceHelper {
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String DARK_MODE_KEY = "darkMode";
    private static final String GRID_LAYOUT_KEY = "gridLayout";
    private static final String LOAD_CLOUD_KEY = "loadCloud";

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

    public static boolean isGridLayoutEnabled(Context context) {
        return getSharedPreferences(context).getBoolean(GRID_LAYOUT_KEY, false);
    }

    public static void setGridLayoutEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(GRID_LAYOUT_KEY, enabled);
        editor.apply();
    }

    public static boolean isLoadCloudEnabled(Context context){
        return getSharedPreferences(context).getBoolean(LOAD_CLOUD_KEY, false);
    }

    public static void setLoadCloudEnabled(Context context, boolean enabled) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(LOAD_CLOUD_KEY, enabled);
        editor.apply();
    }
}