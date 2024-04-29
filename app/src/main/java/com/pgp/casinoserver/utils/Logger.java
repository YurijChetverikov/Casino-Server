package com.pgp.casinoserver.utils;

import android.util.Log;

import java.util.Arrays;

public final class Logger {

    public static void LogError(String TAG, Exception ex){
        if (TAG == null) {
            TAG = "NOT TAGGED";
        }

        String res = ex.toString();
        res+= Arrays.toString(ex.getStackTrace());
        Log.e(TAG, res);
    }

    public static void LogError(String TAG, String exception){
        if (TAG == null) {
            TAG = "NOT TAGGED";
        }
        Log.e(TAG, exception);
    }
}
