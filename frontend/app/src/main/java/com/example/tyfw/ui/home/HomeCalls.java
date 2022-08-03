package com.example.tyfw.ui.home;

import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeCalls {
    static class GetHome implements Runnable {
        final static String TAG = "GetHomeRunnable";
        private JSONObject value;
        private String email;
        private String time;
        public GetHome(String email, String time) {
            this.email = email;
            this.time = time;
        }

        public void run() {
            String url = "http://34.105.106.85:8081/user/displaycurruser/";
            ANRequest request = AndroidNetworking.get(url)
                    .addHeaders("email", email)
                    .addHeaders("time", email)
                    .build();

            ANResponse response = request.executeForJSONObject();

            if (response.isSuccess()) {
                value = (JSONObject) response.getResult();
            } else {
                // handle error
                ANError error = response.getError();
                errorResponse(error);
            }
        }

        private void errorResponse(Exception e){
            //TODO: @Dryden this is where I put in the empty array FE handling
            value = new JSONObject();
            try {
                value.putOpt("data", new JSONArray());
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            //Toast.makeText(getContext(), "Unable to load data for this time option. Please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        public JSONObject getValue() {
            return value;
        }
    }

    public static class GetUser implements Runnable {
        final static String TAG = "GetUserRunnable";
        private JSONObject value;
        private String email;
        private String googleIdToken;

        public GetUser(String email) {
            this.email = email;
        }

        public void run() {
            String url = "http://34.105.106.85:8081/user/getuser/";
            ANRequest request = AndroidNetworking.get(url)
                    .addHeaders("email", email)
                    .build();

            ANResponse response = request.executeForJSONObject();

            if (response.isSuccess()) {
                value = (JSONObject) response.getResult();
            } else {
                // handle error
                ANError error = response.getError();
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

    static class GetBalance implements Runnable {
        final static String TAG = "GetUserRunnable";
        private JSONObject value;
        private String email;

        public GetBalance(String email) {
            this.email = email;
        }

        public void run() {
            String url = "http://34.105.106.85:8081/user/getbalance/";
            ANRequest request = AndroidNetworking.get(url)
                    .addHeaders("email", email)
                    .build();

            ANResponse response = request.executeForJSONObject();

            if (response.isSuccess()) {
                value = (JSONObject) response.getResult();
            } else {
                // handle error
                ANError error = response.getError();
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
}
