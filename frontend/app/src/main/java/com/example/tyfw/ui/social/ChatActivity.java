package com.example.tyfw.ui.social;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tyfw.App;
import com.example.tyfw.R;
import com.example.tyfw.api.APICallers;
import com.example.tyfw.utils.MessageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity implements TextWatcher {
    private String me;
    private String them;

    private WebSocket webSocket;
    private String SERVER_PATH = "ws://34.105.106.85:3000";
    private EditText messageEdit;
    private View sendBtn;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        me = getIntent().getStringExtra("fromUser");
        them = getIntent().getStringExtra("toUser");

        String email = getIntent().getStringExtra("email");
        String googleIdToken = getIntent().getStringExtra("googleIdToken");

        App config = (App) getApplicationContext();
        config.setEmail(email);
        config.setGoogleIdToken(googleIdToken);

        setTitle(them);

//        sendBtn = findViewById(R.id.sendBtn);
        initializeView();
        initiateSocketConnection();
    }
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
        if (item.getItemId() == R.id.share_option) {
            shareProfileDetails();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void shareProfileDetails(){
        String email = getIntent().getStringExtra("email");
        String googleIdToken = getIntent().getStringExtra("googleIdToken");
        APICallers.GetBalance getBalance = new APICallers.GetBalance(email, googleIdToken);
        Thread getBalanceThread = new Thread(getBalance);
        getBalanceThread.start();
        try {
            getBalanceThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject serverResponse = getBalance.getValue();
        DecimalFormat df = new DecimalFormat("0.00");
        try {
            String currBal = df.format(serverResponse.getDouble("balance"))+ " USD";
            sendMessage("Check out my balance: " + currBal);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"Unable to fetch profile details. Please try again later", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private String getConvoID(){
        APICallers.GetConversationID getConvoId = new APICallers.GetConversationID(me, them);
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

        String conversationID = getConvoID();
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

                Log.e("TEXT", text);

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
            }
        });
    }

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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // ignore
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // ignore
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
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
    }
}