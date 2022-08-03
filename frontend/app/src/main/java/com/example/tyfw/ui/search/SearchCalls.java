package com.example.tyfw.ui.search;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;

import org.json.JSONObject;

public class SearchCalls {
    static class GetSearch implements Runnable {
        private JSONObject value;
        private String queryString;
        private String email;
        private String googleIdToken;
        public GetSearch(String queryString, String email, String googleIdToken) {
            this.queryString = queryString;
            this.email = email;
            this.googleIdToken = googleIdToken;
        }

        public void run() {
            String url = "http://34.105.106.85:8081/user/search/";
            ANRequest request = AndroidNetworking.get(url)
                    .addHeaders("queryString", queryString)
                    .addHeaders("email", email)
                    .addHeaders("googleIdToken", googleIdToken)
                    .setPriority(Priority.MEDIUM)
                    .build();
            ANResponse<JSONObject> response = request.executeForJSONObject();

            if (response.isSuccess()) {
                value = response.getResult();
            } else {
                // handle error
                ANError error = response.getError();
                error.printStackTrace();
            }
        }

        public JSONObject getValue() {
            return value;
        }
    }
}
