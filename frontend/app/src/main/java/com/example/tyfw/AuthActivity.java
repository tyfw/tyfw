package com.example.tyfw;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Response;

public class AuthActivity extends AppCompatActivity {

    // private ActivityMainBinding binding;

    private GoogleSignInClient googleSignInClient;
    private Integer RC_SIGN_IN = 1;

    final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        /*
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,  R.id.navigation_leaderboard,  R.id.navigation_search, R.id.navigation_social, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
         */

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

            // TODO: Authenticate on the backend
            // There should bea way to authenticate if an account exists with account.getIdToken()
            // Assume for now no account exists

            // Thread t = new Thread(() -> callAuthAPI(account.getEmail(), account.getIdToken()));
            // t.start();

            // Taken from: https://stackoverflow.com/questions/25928893/how-to-return-value-from-thread-java
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            Integer api_code = -1;
            try {
                api_code = new CallAPIAsync().execute(Pair.create(account.getEmail(), account.getIdToken())).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            Log.d(TAG, api_code.toString());
            if (api_code == 200) {
                Log.d(TAG, "1");
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
            } else if (api_code == 201) {
                Log.d(TAG, "2");
                Intent loginActivity = new Intent(this, LoginActivity.class);
                startActivity(loginActivity);
            } else {
                Log.d(TAG, "3");
                System.out.print(api_code);
            }

            // If user exists, they are logged in
            // Intent mainActivity = new Intent(this, MainActivity.class);
            // startActivity(mainActivity);
        }
    }

    private int callAuthAPI(String userEmail, String googleIdToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", userEmail);
            jsonObject.put("googleIdToken", googleIdToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final int[] api_code = new int[1];

        AndroidNetworking.post("http://34.105.106.85:8081/user/authenticate/")
                 // .addBodyParameter("email", userEmail)
                 // .addBodyParameter("googleIdToken", googleIdToken)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        // 200: user in db, auth worked
                        // 201: user not in db, auth worked
                        api_code[0] = response.code();
                        Log.d(TAG, String.valueOf(response.code()));
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        api_code[0] =-1;
                    }
                });

        return api_code[0];
    }

    private static class CallAPIAsync extends AsyncTask<Pair<String, String>, Void, Integer> implements com.example.tyfw.CallAPIAsync {
        private final int[] api_code = new int[1];
        private String userEmail;
        private String googleIdToken;

        @Override
        protected Integer doInBackground(Pair<String, String>... pairs) {
            this.userEmail = pairs[0].first;
            this.googleIdToken = pairs[0].second;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", userEmail);
                jsonObject.put("googleIdToken", googleIdToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            AndroidNetworking.post("http://34.105.106.85:8081/user/authenticate/")
                    // .addBodyParameter("email", userEmail)
                    // .addBodyParameter("googleIdToken", googleIdToken)
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsOkHttpResponse(new OkHttpResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            // 200: user in db, auth worked
                            // 201: user not in db, auth worked
                            api_code[0] = response.code();
                            Log.d(TAG, String.valueOf(response.code()));
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            api_code[0] =-1;
                        }
                    });

            return api_code[0];
        }
    }
}
