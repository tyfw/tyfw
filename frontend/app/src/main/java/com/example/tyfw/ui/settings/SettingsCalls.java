package com.example.tyfw.ui.settings;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;

public class SettingsCalls {
    static class GetName implements Runnable {
        private String value;
        private String email;
        private final FirstOrLast firstOrLast;
        private String url = "http://34.105.106.85:8081/user/";

        public GetName(String email, FirstOrLast firstOrLast) {
            this.email = email;
            this.firstOrLast = firstOrLast;
        }

        public void run() {
            String req_url;
            if (this.firstOrLast == FirstOrLast.FIRST) {
                req_url = url + "getfirstname/";
            } else {
                req_url = url + "getlastname/";
            }
            ANRequest request = AndroidNetworking.get(req_url)
                    .addHeaders("email", email)
                    .setPriority(Priority.MEDIUM)
                    .build();

            ANResponse<String> response = request.executeForString();

            if (response.isSuccess()) {
                value = response.getResult();
            } else {
                // handle error
                ANError error = response.getError();
            }
        }

        public String getValue() {
            return value;
        }
    }

    static class ChangeName implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private Integer value;
        private String name;
        private String email;
        private String newName;

        public ChangeName(String name, String email, String newName) {
            this.name = name;
            this.email = email;
            this.newName = newName;

        }

        public void run() {
            String url = "http://34.105.106.85:8081/user/changename";
            ANRequest request = AndroidNetworking.post(url)
                    .addBodyParameter("name", name)
                    .addBodyParameter("email", email)
                    .addBodyParameter("newName", newName)
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
