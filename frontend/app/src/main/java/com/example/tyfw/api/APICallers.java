package com.example.tyfw.api;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.spec.ECField;

public class APICallers {
    // Returns an array of time-updated messages fromUser to User
    public static class GetConvoHistory implements Runnable {
        final static String TAG = "GetConversationID";
        private JSONArray value = new JSONArray();
        private String fromUser = "";
        private String toUser = "";


        public GetConvoHistory(String from, String to) {
            if (from != null && to != null){
                this.fromUser = from;
                this.toUser = to;
            }
        }

        public void run() {
                String url = "http://34.105.106.85:8081/user/chathistory/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("fromUser", this.fromUser)
                        .addHeaders("toUser", this.toUser)
                        .setPriority(Priority.MEDIUM)
                        .build();

                ANResponse<JSONArray> response = request.executeForJSONArray();

                if (response.isSuccess()) {
                    value = response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
                    Log.e("Social", String.valueOf(error.getErrorCode()));
                    error.printStackTrace();
                }

        }

        public JSONArray getValue() {
            return value;
        }
    }
}
