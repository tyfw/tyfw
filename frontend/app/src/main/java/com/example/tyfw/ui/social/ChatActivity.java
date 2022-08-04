package com.example.tyfw.ui.social;

import static android.content.ContentValues.TAG;

<<<<<<< HEAD
=======
import androidx.appcompat.app.ActionBar;
>>>>>>> main
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
<<<<<<< HEAD
=======
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
>>>>>>> main
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.example.tyfw.App;
import com.example.tyfw.R;
<<<<<<< HEAD
=======
import com.example.tyfw.api.APICallers;
>>>>>>> main
import com.example.tyfw.utils.MessageAdapter;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity implements TextWatcher {

    private String name;
    private String me;
    private String them;

    private String email;
    private String googleIdToken;

    private WebSocket webSocket;
    private String SERVER_PATH = "ws://34.105.106.85:3000";
    private EditText messageEdit;
    private View sendBtn, pickImgBtn;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        setContentView(R.layout.activity_chat);
=======

        setContentView(R.layout.activity_chat);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
>>>>>>> main

        name = getIntent().getStringExtra("name");
        me = getIntent().getStringExtra("fromUser");
        them = getIntent().getStringExtra("toUser");

        email = getIntent().getStringExtra("email");
        googleIdToken = getIntent().getStringExtra("googleIdToken");

        App config = (App) getApplicationContext();
        config.setEmail(email);
        config.setGoogleIdToken(googleIdToken);

<<<<<<< HEAD
=======
        setTitle(them);

>>>>>>> main
//        sendBtn = findViewById(R.id.sendBtn);
        initializeView();
        initiateSocketConnection();
    }
<<<<<<< HEAD
=======
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.view_profile_chat:
                moveToProfile();
                return true;
            case R.id.share_option:
                shareProfileDetails();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void moveToProfile(){

    }

    private void shareProfileDetails(){
        sendMessage("THIS MY PROFILE FR");
    }
>>>>>>> main

    private String getConvoID(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fromUser", me);
            jsonObject.put("toUser", them);
        } catch (JSONException e) {
            e.printStackTrace();
        }

<<<<<<< HEAD
        GetConversationID getConvoId = new GetConversationID(jsonObject);
=======
        APICallers.GetConversationID getConvoId = new APICallers.GetConversationID(jsonObject);
>>>>>>> main
        Thread getConvoThread = new Thread(getConvoId);
        getConvoThread.start();
        try {
            getConvoThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getConvoId.getValue();
    }

    private void initiateSocketConnection() {
        OkHttpClient client = new OkHttpClient();

        String conversationID = getConvoID(); //TODO: make this get a conversation ID from the API based on fromUser and toUser; make the call for conversation id
        // Request request = new Request.Builder().url(SERVER_PATH).build();
        Log.d("WEBSOCKET CONNECTION URL", SERVER_PATH + "?ConversationID="+ conversationID);
        Request request = new Request.Builder().url(SERVER_PATH + "?ConversationID="+ conversationID).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "Socket Connection Successful", Toast.LENGTH_SHORT).show();
                    initializeView();
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);

<<<<<<< HEAD
                runOnUiThread(() -> {
                    try {

=======
                Log.e("TEXT", text);

                runOnUiThread(() -> {
                    try {
>>>>>>> main
                        JSONObject jsonObject = new JSONObject(text);
                        jsonObject.put("isSent", false);
                        jsonObject.put("name", them);

                        Log.d("MESSAGE", jsonObject.toString());
                        messageAdapter.addItem(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

    }

    private void initializeView() {
        messageEdit = findViewById(R.id.messageEdit);
        sendBtn = findViewById(R.id.sendBtn);
        messageAdapter = new MessageAdapter(getLayoutInflater());
        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageEdit.addTextChangedListener(this);

<<<<<<< HEAD
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    Toast.makeText(ChatActivity.this, "Button pressed", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "send button pressed");

                    jsonObject.put("fromUser", me);
                    jsonObject.put("toUser", them);
                    jsonObject.put("message", messageEdit.getText().toString());

                    webSocket.send(jsonObject.toString());
                    messageAdapter.addItem(jsonObject);

                    jsonObject.put("isSent", true);

                    resetMessageEdit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

=======
        APICallers.GetConvoHistory getConvoHistory = new APICallers.GetConvoHistory(me, them);
        Thread getHistThread = new Thread(getConvoHistory);
        getHistThread.start();
        try {
            getHistThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            JSONArray messages = getConvoHistory.getValue();
            for (int i = 0; i < messages.length(); i++){
                JSONObject msg = messages.getJSONObject(i);
                Log.e(i + "th Message:", msg.toString());
                if (!(msg.isNull("message") && msg.isNull("fromUser") && msg.isNull("toUser"))) {
                    messageAdapter.addItem(msg);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        resetMessageEdit();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(messageEdit.getText().toString());
>>>>>>> main
            }
        });
    }

<<<<<<< HEAD
=======
    private void sendMessage(String text) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fromUser", me);
            jsonObject.put("toUser", them);
            jsonObject.put("message", text);
            webSocket.send(jsonObject.toString());
            messageAdapter.addItem(jsonObject);
            jsonObject.put("isSent", true);
            resetMessageEdit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

>>>>>>> main
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String string = editable.toString().trim();

        if (string.isEmpty()) {
            resetMessageEdit();
        } else {
            sendBtn.setVisibility(View.VISIBLE);
        }
    }

    private void resetMessageEdit() {
        messageEdit.removeTextChangedListener(this);

        messageEdit.setText("");
        sendBtn.setVisibility(View.INVISIBLE);
        messageEdit.addTextChangedListener(this);
<<<<<<< HEAD
    }

    class GetConversationID implements Runnable {
        final static String TAG = "GetConversationID";
        private String value;
        private final JSONObject jsonObject;

        public GetConversationID(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
//                String url = "http://localhost:8081/user/getfriends/";
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
=======
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
>>>>>>> main
    }
}