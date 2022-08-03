package com.example.tyfw;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.androidnetworking.common.Priority;

import com.example.tyfw.ui.login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    private GoogleSignInClient googleSignInClient;
    private final Integer RC_SIGN_IN = 1;

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

            AuthCalls.GetAuth getAuth = new AuthCalls.GetAuth(account.getEmail(), account.getIdToken());
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
                mainActivity.putExtra("email", account.getEmail());
                mainActivity.putExtra("googleIdToken", account.getIdToken());
                startActivity(mainActivity);
            } else if (serverResponse == 201) {
                Intent loginActivity = new Intent(this, LoginActivity.class);
                loginActivity.putExtra("email", account.getEmail());
                loginActivity.putExtra("googleIdToken", account.getIdToken());
                startActivity(loginActivity);
            } else {
                Log.e("TAG", serverResponse.toString());
                System.out.print(serverResponse);
            }
        }
    }
}
