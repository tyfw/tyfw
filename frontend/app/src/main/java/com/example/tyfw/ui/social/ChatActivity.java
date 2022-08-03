package com.example.tyfw.ui.social;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
        setContentView(R.layout.activity_chat);

        name = getIntent().getStringExtra("name");
        me = getIntent().getStringExtra("fromUser");
        them = getIntent().getStringExtra("toUser");

        email = getIntent().getStringExtra("email");
        googleIdToken = getIntent().getStringExtra("googleIdToken");

        App config = (App) getApplicationContext();
        config.setEmail(email);
        config.setGoogleIdToken(googleIdToken);

//        sendBtn = findViewById(R.id.sendBtn);
        initializeView();
        initiateSocketConnection();
    }

    private String getConvoID(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fromUser", me);
            jsonObject.put("toUser", them);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GetConversationID getConvoId = new GetConversationID(jsonObject);
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

                runOnUiThread(() -> {
                    try {

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

            }
        });
    }

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
    }
}