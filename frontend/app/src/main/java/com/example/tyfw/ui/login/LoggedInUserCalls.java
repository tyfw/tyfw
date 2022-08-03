package com.example.tyfw.ui.login;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;

import org.json.JSONObject;

public class LoggedInUserCalls {
    static class RegisterUser implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private Integer value;
        private String email;
        private String username;
        private String firstName;
        private String lastName;
        private JSONObject walletAddress;
        private String googleIdToken;

        public RegisterUser(String email, String username, String firstName, String lastName, JSONObject walletAddress, String googleIdToken) {
            this.email = email;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.walletAddress = walletAddress;
            this.googleIdToken = googleIdToken;
        }

        public void run() {
            String url = "http://34.105.106.85:8081/user/register/";
            ANRequest request= AndroidNetworking.post(url)
                    .addBodyParameter("email", email)
                    .addBodyParameter("username", username)
                    .addBodyParameter("firstName", firstName)
                    .addBodyParameter("lastName", lastName)
                    .addJSONObjectBody(walletAddress)
                    .addBodyParameter("googleIdToken", googleIdToken)
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
