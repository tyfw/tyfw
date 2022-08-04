package com.example.tyfw.api;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.example.tyfw.ui.settings.FirstOrLast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class APICallers {
    // Register User
    public static class RegisterUser implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private Integer value;
        private final JSONObject jsonObject = new JSONObject();

        public RegisterUser(String email, String username, String firstName, String lastName
                , String walletAddress, String googleIdToken, String riskTolerance, String riskAgg) {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(walletAddress);

            try {
                jsonObject.put("email", email);
                jsonObject.put("username", username);
                jsonObject.put("firstName", firstName);
                jsonObject.put("lastName", lastName);
                jsonObject.put("walletAddress", jsonArray);
                jsonObject.put("googleIdToken", googleIdToken);
                jsonObject.put("riskTolerance", String.valueOf(riskTolerance));
                jsonObject.put("riskAgg", String.valueOf(riskAgg));
            }catch (JSONException e ){
                e.printStackTrace();
            }
        }

        public void run() {
            String url = "http://34.105.106.85:8081/user/register/";
            ANRequest request= AndroidNetworking.post(url)
                    .addJSONObjectBody(this.jsonObject)
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

    // Get Auth
    public static class GetAuth implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private Integer value;
        private final JSONObject jsonObject;

        public GetAuth(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            String url = "http://34.105.106.85:8081/user/authenticate/";
            ANRequest request= AndroidNetworking.post(url)
                    .addJSONObjectBody(this.jsonObject)
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

    // Home page
    public static class GetHome implements Runnable {
        final static String TAG = "GetHomeRunnable";
        private JSONObject value;
        private final JSONObject jsonObject;

        public GetHome(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                String url = "http://34.105.106.85:8081/user/displaycurruser/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", jsonObject.getString("email"))
                        .addHeaders("time", jsonObject.getString("time"))
                        .build();

                ANResponse response = request.executeForJSONObject();

                if (response.isSuccess()) {
                    value = (JSONObject) response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
                    errorResponse(error);
                }
            } catch (JSONException e) {
                errorResponse(e);
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

    // Get user
    public static class GetUser implements Runnable {
        final static String TAG = "GetUserRunnable";
        private JSONObject value;
        private final JSONObject jsonObject;

        public GetUser(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                String url = "http://34.105.106.85:8081/user/getuser/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", jsonObject.getString("email"))
                        .build();

                ANResponse response = request.executeForJSONObject();

                if (response.isSuccess()) {
                    value = (JSONObject) response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
                    int errorCode = error.getErrorCode();
                    if (errorCode == 400) {
                        // Toast.makeText(getContext(), "Unable to get all user details from server", Toast.LENGTH_SHORT).show();
                    }
                    errorResponse(error);
                }
            } catch (JSONException e) {
                errorResponse(e);
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

    // Get balance
    public static class GetBalance implements Runnable {
        final static String TAG = "GetUserRunnable";
        private JSONObject value;
        private final JSONObject jsonObject;

        public GetBalance(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                String url = "http://34.105.106.85:8081/user/getbalance/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", jsonObject.getString("email"))
                        .build();

                ANResponse response = request.executeForJSONObject();

                if (response.isSuccess()) {
                    value = (JSONObject) response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
                    errorResponse(error);
                }
            } catch (JSONException e) {
                errorResponse(e);
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

    // Profile activity
    public static class GetProfile implements Runnable {
        final static String TAG = "GetProfileRunnable";
        private JSONObject value;
        private JSONObject jsonObject;
        private String url = "http://34.105.106.85:8081/user/displayotheruserbyusername/";

        public GetProfile(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("otherUsername", jsonObject.getString("friendUsername"))
                        .addHeaders("time", jsonObject.getString("time"))
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
            } catch (JSONException e) {
                errorResponse(e);
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

    // Add friends endpoint
    public static class AddFriend implements Runnable {
        final static String TAG = "GetProfileRunnable";
        private boolean value;
        private JSONObject jsonObject;
        private String url = "http://34.105.106.85:8081/user/addbyusername/";

        public AddFriend(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            ANRequest request = AndroidNetworking.post(url)
                    .addJSONObjectBody(jsonObject)
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
        final static String TAG = "GetFriendsRunnable";
        private JSONObject value;
        private final JSONObject jsonObject;

        public GetFriends(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                String url = "http://34.105.106.85:8081/user/getfriends/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", jsonObject.getString("email"))
                        .build();

                ANResponse response = request.executeForJSONObject();

                if (response.isSuccess()) {
                    value = (JSONObject) response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
                    error.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public JSONObject getValue() {
            return value;
        }
    }

    // Get Auth
    public static class DeleteFriend implements Runnable {
        private Boolean value = null;
        final static String TAG = "DeleteFriend";

        private String email = "";
        private String friend = "";

        public DeleteFriend(String email, String friend) {
            this.email = email;
            this.friend = friend;
        }

        public void run() {
            String url = "http://34.105.106.85:8081/user/delete_friend/";
            ANRequest request= AndroidNetworking.delete(url)
                    .addHeaders("email", this.email)
                    .addHeaders("friend", this.friend)
                    .setPriority(Priority.MEDIUM)
                    .build();

            ANResponse response = request.executeForOkHttpResponse();

            if (response.isSuccess()) {
                value = response.getOkHttpResponse().isSuccessful();
            } else {
                // handle error
                ANError error = response.getError();
                Log.d(TAG, error.toString());
            }
        }

        public Boolean getValue() {
            return value;
        }
    }

    // AI Prediction
    public static class GetPrediction implements Runnable {
        final static String TAG = "GetUserRunnable";
        private JSONObject value;
        private final JSONObject jsonObject;

        public GetPrediction(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                String url = "http://34.105.106.85:8081/user/getprediction/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", jsonObject.getString("email"))
                        .addHeaders("risktolerance", jsonObject.getString("risktolerance"))
                        .build();

                ANResponse response = request.executeForJSONObject();

                if (response.isSuccess()) {
                    value = (JSONObject) response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
                    errorResponse(error);
                }
            } catch (JSONException e) {
                errorResponse(e);
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

    // Leaderboard
    public static class GetLeaderboard implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private JSONArray value;
        private final JSONObject jsonObject;

        public GetLeaderboard(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                Log.e("a", jsonObject.toString());
                String url = "http://34.105.106.85:8081/user/leaderboard/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", jsonObject.getString("email"))
                        .addHeaders("googleIdToken", jsonObject.getString("googleIdToken"))
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public JSONArray getValue() {
            return value;
        }
    }

    public static class GetConversationID implements Runnable {
        final static String TAG = "GetConversationID";
        private String value;
        private final JSONObject jsonObject;

        public GetConversationID(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                String url = "http://34.105.106.85:8081/user/conversation_id/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("fromUser", jsonObject.getString("fromUser"))
                        .addHeaders("toUser", jsonObject.getString("toUser"))
                        .setPriority(Priority.MEDIUM)
                        .build();

                ANResponse<String> response = request.executeForString();

                if (response.isSuccess()) {
                    value = response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
                    Log.e("Social", String.valueOf(error.getErrorCode()));
                    error.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getValue() {
            return value;
        }
    }

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

        public JSONArray getValue() throws JSONException {
            for (int i = 0; i < value.length(); i++){
                JSONObject index = value.getJSONObject(i);
                if (index.get("fromUser").equals(fromUser)){
                    index.put("isSent", true);
                } else {
                    index.put("isSent", false);
                }
            }
            return value;
        }
    }

    // Search
    public static class GetSearch implements Runnable {
        private JSONObject value;
        private final JSONObject jsonObject;

        public GetSearch(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                String url = "http://34.105.106.85:8081/user/search/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("queryString", jsonObject.getString("queryString"))
                        .addHeaders("email", jsonObject.getString("email"))
                        .addHeaders("googleIdToken", jsonObject.getString("googleIdToken"))
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public JSONObject getValue() {
            return value;
        }
    }

    public static class GetName implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private final FirstOrLast firstOrLast;
        private String value;
        private final JSONObject jsonObject;

        public GetName(JSONObject jsonObject, FirstOrLast firstOrLast) {
            this.jsonObject = jsonObject;
            this.firstOrLast = firstOrLast;
        }

        public void run() {
            String req_url;
            String url = "http://34.105.106.85:8081/user/";
            if (this.firstOrLast == FirstOrLast.FIRST) {
                req_url = url + "getfirstname/";
            } else {
                req_url = url + "getlastname/";
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

    public static class ChangeName implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private FirstOrLast firstOrLast;
        private Integer value;
        private final JSONObject jsonObject;

        public ChangeName(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            String url = "http://34.105.106.85:8081/user/changename";
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
