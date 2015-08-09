package com.grilla.hereseum.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bill on 8/7/15.
 */
public class SharedPrefsHelper {
    private static final String PREFS_KEY = "com.grilla.hereseum";

    public static final String KEY_ACCESS_TOKEN = "instagram_access_token";

    private static SharedPreferences getInstance(Context context) {
        return context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getInstance(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences prefs = getInstance(context);
        return prefs.getString(key, null);
    }
}
