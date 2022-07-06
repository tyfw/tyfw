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

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", account.getEmail());
                jsonObject.put("googleIdToken", account.getIdToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new AuthAPITask(new AuthAPITask.TaskCompleteListener() {
                @Override
                public void onTaskComplete(Integer api_code) {
                    if (api_code == 200) {
                        Log.d(TAG, "1");
                        Intent mainActivity = new Intent(AuthActivity.this, MainActivity.class);
                        startActivity(mainActivity);
                    } else if (api_code == 201) {
                        Log.d(TAG, "2");
                        Intent loginActivity = new Intent(AuthActivity.this, LoginActivity.class);
                        startActivity(loginActivity);
                    } else {
                        Log.d(TAG, "3");
                        System.out.print(api_code);
                    }
                }
            }).execute(jsonObject);
        }
    }

    // Implemented using Async API
    // TODO if we want to change: https://stackoverflow.com/questions/58767733/the-asynctask-api-is-deprecated-in-android-11-what-are-the-alternatives
    private static class AuthAPITask extends AsyncTask<JSONObject, Integer, Integer> {
        private String url = "http://34.105.106.85:8081/user/authenticate/";


        // Followed instructions in this medium article:
        // https://ioannisanif.medium.com/making-an-intent-call-from-inside-activity-when-asynctask-is-in-a-separate-file-via-a-java-9b6850b6a73d

        public interface TaskCompleteListener {
            void onTaskComplete(Integer result);
        }// define a member variable associated with that interface
        private final TaskCompleteListener mTaskCompleteListener;

        public AuthAPITask(TaskCompleteListener listener){
            mTaskCompleteListener = listener;
        }

        @Override
        protected Integer doInBackground(JSONObject... body) {
            final int[] api_code = new int[1];

            for (int i = 0; i < body.length; i++ ){
                AndroidNetworking.post(url)
                        .addJSONObjectBody(body[i])
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
            }

            return api_code[0];
        }

        @Override
        protected void onPostExecute(Integer api_code){
            Log.d(TAG, api_code.toString());
            mTaskCompleteListener.onTaskComplete(api_code);
        }
    }

}
