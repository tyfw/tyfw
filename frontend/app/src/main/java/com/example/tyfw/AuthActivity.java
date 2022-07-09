package com.example.tyfw;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.util.Pair;

import com.androidnetworking.common.Priority;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.example.tyfw.ui.login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tyfw.databinding.ActivityAuthBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Response;

public class AuthActivity extends AppCompatActivity {

    // private ActivityMainBinding binding;

    private GoogleSignInClient googleSignInClient;
    private Integer RC_SIGN_IN = 1;
    private Class nextActivity = MainActivity.class;

    final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(v -> {
            signIn();
        });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            Log.d(TAG, "No user signed in.");
        } else {
            Log.d(TAG, "User " + account.getDisplayName() + " signed in.");

            App config = (App) getApplicationContext();
            config.setGoogleIdToken(account.getIdToken());
            config.setEmail(account.getEmail());

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", account.getEmail());
                jsonObject.put("googleIdToken", account.getIdToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            GetAuth getAuth = new GetAuth(jsonObject);
            Thread getAuthThread = new Thread(getAuth);
            getAuthThread.start();
            try {
                getAuthThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Integer serverResponse = getAuth.getValue();

            if (serverResponse == 200) {
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
            } else if (serverResponse == 201) {
                Intent loginActivity = new Intent(this, LoginActivity.class);
                startActivity(loginActivity);
            } else {
                Log.e("TAG", serverResponse.toString());
                System.out.print(serverResponse);
            }
        }
    }

    class GetAuth implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private Integer value;
        private String url = "http://34.105.106.85:8081/user/authenticate/";
        private JSONObject jsonObject;

        public GetAuth(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
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
}
