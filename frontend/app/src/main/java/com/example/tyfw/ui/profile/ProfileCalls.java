package com.example.tyfw.ui.profile;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileCalls {
    static class GetProfile implements Runnable {
        private JSONObject value;
        private String time;
        private String friendUsername;
        private String url = "http://34.105.106.85:8081/user/displayotheruserbyusername/";

        public GetProfile(String time, String friendUsername) {
            this.time = time;
            this.friendUsername = friendUsername;
        }

        public void run() {
            ANRequest request = AndroidNetworking.get(url)
                    .addHeaders("otherUsername", friendUsername)
                    .addHeaders("time", time)
                    .setPriority(Priority.MEDIUM)
                    .build();

            ANResponse response = request.executeForJSONObject();

            if (response.isSuccess()) {
                value = (JSONObject) response.getResult();
            } else {
                // handle error
                ANError error = response.getError();
                error.printStackTrace();
                errorResponse(error);
            }
        }

        private void errorResponse(Exception e){
            value = new JSONObject();
            try {
                value.putOpt("data", new JSONArray());
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

        public JSONObject getValue() {
            return value;
        }
    }

    static class AddFriend implements Runnable {
        final static String TAG = "GetProfileRunnable";
        private boolean value;
        private String email;
        private String googleIdToken;
        private String friendUsername;
        private String url = "http://34.105.106.85:8081/user/addbyusername/";

        public AddFriend(String email, String googleIdToken, String friendUsername) {
            this.email = email;
            this.googleIdToken = googleIdToken;
            this.friendUsername = friendUsername;
        }

        public void run() {
            ANRequest request = AndroidNetworking.post(url)
                    .addBodyParameter("email", email)
                    .addBodyParameter("googleIdToken", googleIdToken)
                    .addBodyParameter("friendUsername", friendUsername)
                    .setPriority(Priority.MEDIUM)
                    .build();

            ANResponse response = request.executeForOkHttpResponse();

            if (response.isSuccess()) {
                value = response.getOkHttpResponse().isSuccessful();
            } else {
                // handle error
                ANError error = response.getError();
                error.printStackTrace();
            }
        }

        public boolean success() {
            return value;
        }
    }

    public static class GetFriends implements Runnable {
        private JSONObject value;
        private String email;
        private String url = "http://34.105.106.85:8081/user/getfriends/";

        public GetFriends(String email) {
            this.email = email;
        }

        public void run() {
            ANRequest request = AndroidNetworking.get(url)
                    .addHeaders("email", email)
                    .build();

            ANResponse response = request.executeForJSONObject();

            if (response.isSuccess()) {
                value = (JSONObject) response.getResult();
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
