package com.example.tcs.bskill.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 1241575(Azim Ansari) on 7/26/2016.
 * Handling user preferences
 */
public class PreferenceUtil {
    public static final String PREFERENCE_FILE_NAME = "bskill.pref";

    public static final String IS_FIRST_TIME = "firstTime";
    public static final String EMP_ID = "employeeId";
    public static final String EMP_NAME = "employeeName";
    public static final String EMP_EMAIL = "employeeEmail";

    public static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static void setEmpEmail(Context context, String empEmail) {
        getPreference(context).edit().putString(EMP_EMAIL, empEmail).apply();
    }

    public static void setEmpID(Context context, String empID) {
        getPreference(context).edit().putString(EMP_ID, empID).apply();
    }

    public static void setEmpName(Context context, String empName) {
        getPreference(context).edit().putString(EMP_NAME, empName).apply();
    }

    public static String getEmpEmail(Context context) {
        return getPreference(context).getString(EMP_EMAIL, null);
    }

    public static String getEmpID(Context context) {
        return getPreference(context).getString(EMP_ID, null);
    }

    public static String getEmpName(Context context) {
        return getPreference(context).getString(EMP_NAME, null);
    }
}
