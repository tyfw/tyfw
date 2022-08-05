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


/**
 * APICallers
 * A package consisting of static classes that handle all the back-end calls from the front-end.
 *
 * Please refer to our back-end API docs for detailed information on parameters passed into
 * each constructor.
 */

public class APICallers {
    /**
     * RegisterUser
     */
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

    /**
     *
     */
    // Get Auth
    public static class GetAuth implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private Integer value;
        private final JSONObject jsonObject = new JSONObject();

        public GetAuth(String email, String googleIdToken) {
            try {
                this.jsonObject.put("email", email);
                this.jsonObject.put("googleIdToken", googleIdToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        private final String email;
        private final String time;

        public GetHome(String email, String time) {
            this.email = email;
            this.time = time;
        }

        public void run() {
                String url = "http://34.105.106.85:8081/user/displaycurruser/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", email)
                        .addHeaders("time", time)
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

    // Get user
    public static class GetUser implements Runnable {
        final static String TAG = "GetUserRunnable";
        private JSONObject value;
        private final String email;
        private final String googleIdToken;

        public GetUser(String email, String googleToken) {
            this.email = email;
            this.googleIdToken = googleToken;
        }

        public void run() {

                String url = "http://34.105.106.85:8081/user/getuser/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", this.email)
                        .addHeaders("googleIdToken", this.googleIdToken)
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

    // Get balance
    public static class GetBalance implements Runnable {
        final static String TAG = "GetUserRunnable";
        private JSONObject value;
        private final String email;
        private final String googleIdToken;

        public GetBalance(String email, String googleIdToken) {
            this.email = email;
            this.googleIdToken = googleIdToken;
        }

        public void run() {
                String url = "http://34.105.106.85:8081/user/getbalance/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", this.email)
                        .addHeaders("googleIdToken", this.googleIdToken)
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

    // Profile activity
    public static class GetProfile implements Runnable {
        final static String TAG = "GetProfileRunnable";
        private JSONObject value;
        private JSONObject jsonObject;
        private String email;
        private String token;
        private String time;
        private String friend;
        private String url = "http://34.105.106.85:8081/user/displayotheruserbyusername/";

        public GetProfile(String email, String token, String time, String friend) {
            this.email = email;
            this.token = token;
            this.time = time;
            this.friend = friend;
        }

        public void run() {
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("otherUsername", this.friend)
                        .addHeaders("time", this.time)
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

    // Add friends endpoint
    public static class AddFriend implements Runnable {
        final static String TAG = "GetProfileRunnable";
        private boolean value;
        private JSONObject jsonObject = new JSONObject();

        private String url = "http://34.105.106.85:8081/user/addbyusername/";

        public AddFriend(String email,String token, String username) {
            try {
                jsonObject.put("email", email);
                jsonObject.put("googleIdToken",  token);
                jsonObject.put("friendUsername", username);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        private final String email;

        public GetFriends(String email) {
            this.email = email;
        }

        public void run() {
                String url = "http://34.105.106.85:8081/user/getfriends/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", this.email)
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
        private final JSONObject jsonObject = new JSONObject();

        public GetPrediction(int riskTolerance, int riskAgg, String email) {
            try {
                jsonObject.put("riskTolerance", riskTolerance);
                jsonObject.put("riskAgg", riskAgg);
                jsonObject.put("email", email);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                Log.e("a", jsonObject.toString());
                String url = "http://34.105.106.85:8081/user/getprediction/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", jsonObject.getString("email"))
                        .addHeaders("riskTolerance", jsonObject.getString("riskTolerance"))
                        .addHeaders("riskAgg", jsonObject.getString("riskAgg"))
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
        private final String email;
        private final String token;
        public GetLeaderboard(String email, String token) {
            this.email = email;
            this.token = token;
        }

        public void run() {
                String url = "http://34.105.106.85:8081/user/leaderboard/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", this.email)
                        .addHeaders("googleIdToken", this.token)
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

    public static class GetConversationID implements Runnable {
        final static String TAG = "GetConversationID";
        private String value;
        private final String from;
        private final String to;

        public GetConversationID(String fromUser, String toUser) {
            this.from = fromUser;
            this.to = toUser;
        }

        public void run() {
                String url = "http://34.105.106.85:8081/user/conversation_id/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("fromUser", from)
                        .addHeaders("toUser", to)
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
        private final String query;
        private final String email;
        private final String token;
        public GetSearch(String query, String email, String googleIdToken) {
            this.query = query;
            this.email = email;
            this.token = googleIdToken;
        }

        public void run() {
                String url = "http://34.105.106.85:8081/user/search/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("queryString", query)
                        .addHeaders("email", email)
                        .addHeaders("googleIdToken", token)
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

    public static class GetName implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private final FirstOrLast firstOrLast;
        private String value;
        private final String email;

        public GetName(String email, FirstOrLast firstOrLast) {
            this.email = email;
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
                    Log.d(TAG, error.toString());
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
        private final JSONObject jsonObject = new JSONObject();

        public ChangeName(String name, String email, String newName) {
            try {
                this.jsonObject.put("name", name);
                this.jsonObject.put("email", email);
                this.jsonObject.put("newName", newName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
