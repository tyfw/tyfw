package com.example.tyfw.ui.login;

import android.util.Log;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName) {
        Log.d("LOGGED IN USER", displayName);
    }
}