package com.example.locker.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class SharedPreferencesHelper {
    private static String SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME";
    private final SharedPreferences mSharedPreferences;
    private static SharedPreferencesHelper instance;

    public static SharedPreferencesHelper getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new SharedPreferencesHelper(context);
        }
        return instance;
    }

    private SharedPreferencesHelper(@NonNull Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
    }

    private SharedPreferences.Editor editor() {
        return mSharedPreferences.edit();
    }

    public String getPasscode() {
        return mSharedPreferences.getString(Constant.PASSCODE, null);
    }

    public void setPasscode(String value) {
        editor().putString(Constant.PASSCODE, value).apply();
    }

    public String getPattern() {
        return mSharedPreferences.getString(Constant.PATTERN, null);
    }

    public void setPattern(String value) {
        editor().putString(Constant.PATTERN, value).apply();
    }

    public String getLockType() {
        return mSharedPreferences.getString(Constant.LOCK_TYPE,"DEFAULT");
    }

    public void setLockType(String value) {
        editor().putString(Constant.LOCK_TYPE, value).apply();
    }

    public String getSettingType() {
        return mSharedPreferences.getString(Constant.IS_SETTING,"SETTING");
    }

    public void setSettingType(String value) {
        editor().putString(Constant.IS_SETTING, value).apply();
    }
}
