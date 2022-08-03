package com.example.tyfw.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.example.tyfw.App;
import com.example.tyfw.AuthActivity;
import com.example.tyfw.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

enum FirstOrLast {
    FIRST, LAST
}

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(requireContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Log.d("Settings", "Google Client connection status: " + googleApiClient.isConnected());

        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        Log.d("Settings", "Existing settings: " + screen.toString());

        // Temporary name placeholders
        String firstName = "John";
        String lastName = "Doe";

        App config = (App) getContext().getApplicationContext();

        SettingsCalls.GetName getFirstname = new SettingsCalls.GetName(config.getEmail(), FirstOrLast.FIRST);
        Thread getFirstNameThread = new Thread(getFirstname);
        getFirstNameThread.start();
        try {
            getFirstNameThread.join();
            firstName = getFirstname.getValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SettingsCalls.GetName getLastName = new SettingsCalls.GetName(config.getEmail(), FirstOrLast.LAST);
        Thread getLastNameThread = new Thread(getLastName);
        getLastNameThread.start();
        try {
            getLastNameThread.join();
            lastName = getLastName.getValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // For more information visit this link
        // https://stackoverflow.com/questions/44727552/access-text-of-edittextpreference-from-fragment
        final EditTextPreference firstNamePreference = getPreferenceManager().findPreference("preference_first_name");
        firstNamePreference.setText(firstName);
        firstNamePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            SettingsCalls.ChangeName changeName = new SettingsCalls.ChangeName("first", config.getEmail(), newValue.toString());
            Thread changeNameThread = new Thread(changeName);
            changeNameThread.start();
            try {
                changeNameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Unable to change name", Toast.LENGTH_SHORT);
            }
            return true;
        });

        final EditTextPreference lastNamePreference = getPreferenceManager().findPreference("preference_last_name");
        lastNamePreference.setText(lastName);
        lastNamePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            SettingsCalls.ChangeName changeName = new SettingsCalls.ChangeName("last", config.getEmail(), newValue.toString());
            Thread changeNameThread = new Thread(changeName);
            changeNameThread.start();
            try {
                changeNameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Unable to change name", Toast.LENGTH_SHORT);
            }
            return true;
        });

        final Preference signOutPreference = getPreferenceManager().findPreference("Logout");
        signOutPreference.setOnPreferenceClickListener(preference -> {
            GoogleSignInOptions gso1 = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    build();

            GoogleSignInClient googleSignInClient=GoogleSignIn.getClient(context, gso1);
            googleSignInClient.signOut();

            Toast.makeText(getContext(),"Logging out",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), AuthActivity.class);
            startActivity(intent);
            return true;
        });
    }
}
