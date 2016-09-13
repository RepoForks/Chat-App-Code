package com.tubiapp.demochatxmpp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by webclues on 5/20/2016.
 */
public class Utility {
    public static String KEY_USER_DATA = "userdata";
    public static String KEY_USER_EMAIL = "user_email";
    public static String KEY_USER_PASS = "user_pass";
    public static String KEY_USER_STATUS = "user_status";
    public static String KEY_BUDDYID = "buddyid";
    public static String KEY_USERT_ONLINE_OFFLINE_FLAG = "user_status_on_off";

    public static void writeSharedPreferencesBool(Context mContext, boolean status, String email, String pass) {
        SharedPreferences settings = mContext.getSharedPreferences(
                KEY_USER_DATA, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(KEY_USER_STATUS, status);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PASS, pass);

        editor.commit();
    }

    public static void writeSharedPreferences_BuddyID(Context mContext,
                                                      String value) {
        SharedPreferences settings = mContext.getSharedPreferences(
                KEY_USER_DATA, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_BUDDYID, value);
        editor.commit();
    }


    public static void writeSharedPreferences_User_On_Off(Context mContext,
                                                          String value) {
        SharedPreferences settings = mContext.getSharedPreferences(
                KEY_USER_DATA, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_USERT_ONLINE_OFFLINE_FLAG, value);
        editor.commit();
    }

    public static String getUSER_On_Off(Context mContext) {
        try {
            SharedPreferences settings = mContext.getSharedPreferences(
                    KEY_USER_DATA, 0);
            String value = settings.getString(KEY_USERT_ONLINE_OFFLINE_FLAG, "");
            return value;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static boolean getUSER_STATUS(Context mContext) {
        try {
            SharedPreferences settings = mContext.getSharedPreferences(
                    KEY_USER_DATA, 0);
            boolean value = settings.getBoolean(KEY_USER_STATUS, false);
            return value;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static String getBuddyID(Context mContext) {
        try {
            SharedPreferences settings = mContext.getSharedPreferences(
                    KEY_USER_DATA, 0);
            String value = settings.getString(KEY_BUDDYID, "");
            return value;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String getUser_Email(Context mContext) {
        try {
            SharedPreferences settings = mContext.getSharedPreferences(
                    KEY_USER_DATA, 0);
            String value = settings.getString(KEY_USER_EMAIL, "");
            return value;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String getUser_Pass(Context mContext) {
        try {
            SharedPreferences settings = mContext.getSharedPreferences(
                    KEY_USER_DATA, 0);
            String value = settings.getString(KEY_USER_PASS, "");
            return value;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}



