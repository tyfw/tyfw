package com.example.tyfw;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;

public class AuthCalls {
    public class GetAuth implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private Integer value;
        private String email;
        private String googleIdToken;
        public GetAuth(String email, String googleIdToken) {
            this.email = email;
            this.googleIdToken = googleIdToken;
        }

        public void run() {
            Log.e("a", this.email);
            Log.e("a", this.googleIdToken);
            String url = "http://34.105.106.85:8081/user/authenticate/";
            ANRequest request= AndroidNetworking.post(url)
                    .addBodyParameter("email", this.email)
                    .addBodyParameter("googleIdToken", this.googleIdToken)
                    .setPriority(Priority.MEDIUM)
                    .build();
            Log.e("A", request.toString());
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
