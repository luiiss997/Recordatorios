package com.example.recordatorios.Usuario;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

public class UserPreferences {
    private Context context;
    private UserSettings userSettings;

    public UserPreferences(Context context) {
        this.context = context;
    }

    public void saveUserPreferences(){
        SharedPreferences userPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = userPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(this.userSettings);
        editor.putString("UserPreferences", json);
        editor.commit();

    }

    public void loadUserPreferences(){
        SharedPreferences userPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = userPrefs.getString("UserPreferences", "");
        this.userSettings = gson.fromJson(json, UserSettings.class);
        if (userSettings==null){
            userSettings = new UserSettings();
        }
    }

    public UserSettings getUserSettings() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean v=pref.getBoolean("format", true);

        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }
}

