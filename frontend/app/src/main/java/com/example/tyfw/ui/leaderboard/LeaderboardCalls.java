package com.example.tyfw.ui.leaderboard;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;

import org.json.JSONArray;

public class LeaderboardCalls {
    static class GetLeaderboard implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private JSONArray value;
        private String email;
        private String googleIdToken;
        public GetLeaderboard(String email, String googleIdToken) {

            this.email = email;
            this.googleIdToken = googleIdToken;
        }

        public void run() {
            String url = "http://34.105.106.85:8081/user/leaderboard/";
            ANRequest request = AndroidNetworking.get(url)
                    .addHeaders("email", email)
                    .addHeaders("googleIdToken", googleIdToken)
                    .setPriority(Priority.MEDIUM)
                    .build();

            ANResponse<JSONArray> response = request.executeForJSONArray();

            if (response.isSuccess()) {
                value = response.getResult();
            } else {
                // handle error
                ANError error = response.getError();
                Log.e("Leaderboard", String.valueOf(error.getErrorCode()));
                error.printStackTrace();
            }
        }

        public JSONArray getValue() {
            return value;
        }
    }
}
