package com.example.tyfw.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.tyfw.R;

import org.json.JSONException;
import org.json.JSONObject;

enum FirstOrLast {
    FIRST, LAST
}

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        String firstName = "John";
        String lastName = "Doe";

        App config = (App) getContext().getApplicationContext();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", config.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GetName getFirstname = new GetName(jsonObject, FirstOrLast.FIRST);
        Thread getFirstNameThread = new Thread(getFirstname);
        getFirstNameThread.start();
        try {
            getFirstNameThread.join();
            firstName = getFirstname.getValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        GetName getLastName = new GetName(jsonObject, FirstOrLast.LAST);
        Thread getLastNameThread = new Thread(getLastName);
        getLastNameThread.start();
        try {
            getLastNameThread.join();
            lastName = getLastName.getValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // https://stackoverflow.com/questions/44727552/access-text-of-edittextpreference-from-fragment
        final EditTextPreference firstNamePreference = getPreferenceManager().findPreference("preference_first_name");
        firstNamePreference.setText(firstName);
        firstNamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", "first");
                    jsonObject.put("email", config.getEmail());
                    jsonObject.put("newName", newValue.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ChangeName changeName = new ChangeName(jsonObject);
                Thread changeNameThread = new Thread(changeName);
                changeNameThread.start();
                try {
                    changeNameThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Unable to change name", Toast.LENGTH_SHORT);
                }
                return true;
            }
        });

        final EditTextPreference lastNamePreference = getPreferenceManager().findPreference("preference_last_name");
        lastNamePreference.setText(lastName);
        lastNamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", "last");
                    jsonObject.put("email", config.getEmail());
                    jsonObject.put("newName", newValue.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ChangeName changeName = new ChangeName(jsonObject);
                Thread changeNameThread = new Thread(changeName);
                changeNameThread.start();
                try {
                    changeNameThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Unable to change name", Toast.LENGTH_SHORT);
                }
                return true;
            }
        });
    }

    class GetName implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private FirstOrLast firstOrLast;
        private String value;
        private String url = "http://34.105.106.85:8081/user/";
        private JSONObject jsonObject;

        public GetName(JSONObject jsonObject, FirstOrLast firstOrLast) {
            this.jsonObject = jsonObject;
            this.firstOrLast = firstOrLast;
        }

        public void run() {
            String req_url;
            if (this.firstOrLast == FirstOrLast.FIRST) {
                req_url = this.url + "getfirstname/";
            } else {
                req_url = this.url + "getlastname/";
            }
            try {
                ANRequest request = AndroidNetworking.get(req_url)
                        .addHeaders("email", jsonObject.getString("email"))
                        .setPriority(Priority.MEDIUM)
                        .build();

                ANResponse<String> response = request.executeForString();

                if (response.isSuccess()) {
                    value = response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
                    Log.d(TAG, error.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getValue() {
            return value;
        }
    }

    class ChangeName implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private FirstOrLast firstOrLast;
        private Integer value;
        private String url = "http://34.105.106.85:8081/user/changename";
        private JSONObject jsonObject;

        public ChangeName(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            ANRequest request = AndroidNetworking.post(url)
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build();

            ANResponse response = request.executeForOkHttpResponse();

            if (response.isSuccess()) {
                value = response.getOkHttpResponse().code();
            } else {
                // handle error
                ANError error = response.getError();
                Log.d(TAG, error.toString());
            }
        }

        public Integer getValue() {
            return value;
        }
    }
}
