package com.fazlagida.burak.smartscaleusb;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    private static SharedPref myPreference;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private final String PREF_NAME = "ENTRYBOOKS";

    private SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPref getPreferences(Context context) {
        if (myPreference == null)
            myPreference = new SharedPref(context);
        return myPreference;
    }

    // Put String Value
    public void setStringData(String Key, String Value) {
        editor.putString(Key, Value);
        editor.apply();
        editor.commit();
    }

    // Put Integer Value
    public void setIntData(String Key, int Value) {
        editor.putInt(Key, Value);
        editor.apply();
        editor.commit();
    }

    // Get String Value
    public String getStringData(String Key, String Fallback) {
        return sharedPreferences.getString(Key, Fallback);
    }

    // Get Integer Value
    public int getIntData(String Key, int Fallback) {
        return sharedPreferences.getInt(Key, Fallback);
    }

    // Clear All Existing Room
    public void setEmpty() {
        editor.clear();
        editor.commit();
    }
}
