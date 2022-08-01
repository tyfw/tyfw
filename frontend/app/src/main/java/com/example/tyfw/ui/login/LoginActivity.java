package com.example.tyfw.ui.login;

import android.app.Activity;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.example.tyfw.App;
import com.example.tyfw.MainActivity;
import com.example.tyfw.databinding.ActivityLoginBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText walletAddressEditText;

    private String email;
    private String googleIdToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        EditText usernameEditText;

        email = getIntent().getStringExtra("email");
        googleIdToken = getIntent().getStringExtra("googleIdToken");

        App config = (App) getApplicationContext();
        config.setEmail(email);
        config.setGoogleIdToken(googleIdToken);

        super.onCreate(savedInstanceState);

        email = getIntent().getStringExtra("email");
        googleIdToken = getIntent().getStringExtra("googleIdToken");

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        firstNameEditText = binding.firstName;
        lastNameEditText = binding.lastName;
        emailEditText = binding.email;
        walletAddressEditText = binding.walletProfile;
        usernameEditText = binding.username;


        emailEditText.setText(email);
        emailEditText.setEnabled(false);

        emailEditText.setText(email);
        emailEditText.setEnabled(false);

        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        getLoginFormState(loginViewModel, firstNameEditText, lastNameEditText,
                emailEditText , walletAddressEditText, loginButton); // reduce complexity

        getLoginResult( loginViewModel,  firstNameEditText,  lastNameEditText, walletAddressEditText, usernameEditText,  loadingProgressBar );

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(
                        firstNameEditText.getText().toString(),
                        lastNameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        walletAddressEditText.getText().toString());
            }
        };
        firstNameEditText.addTextChangedListener(afterTextChangedListener);
        lastNameEditText.addTextChangedListener(afterTextChangedListener);
        lastNameEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                        firstNameEditText.getText().toString(),
                        lastNameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        walletAddressEditText.getText().toString(
                ));
            }
            return false;
        });

        emailEditText.addTextChangedListener(afterTextChangedListener);
        walletAddressEditText.addTextChangedListener(afterTextChangedListener);

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(
                    firstNameEditText.getText().toString(),
                    lastNameEditText.getText().toString(),
                    emailEditText.getText().toString(),
                    walletAddressEditText.getText().toString()
            );
        });
    }
    
    private void getLoginFormState(LoginViewModel loginViewModel, EditText firstNameEditText, EditText lastNameEditText,
                                   EditText emailEditText , EditText walletAddressEditText, Button loginButton){
        
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            // loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getFirstNameError() != null) {
                firstNameEditText.setError(getString(loginFormState.getFirstNameError()));
            }
            if (loginFormState.getLastNameError() != null) {
                lastNameEditText.setError(getString(loginFormState.getLastNameError()));
            }
            if (loginFormState.getEmailError() != null) {
                emailEditText.setError(getString(loginFormState.getEmailError()));
            }
            if (loginFormState.getWalletAddressError() != null) {
                walletAddressEditText.setError(getString(loginFormState.getWalletAddressError()));
            }
            if (loginFormState.getFirstNameError() == null && loginFormState.getLastNameError() == null &&
                    loginFormState.getEmailError() == null && loginFormState.getWalletAddressError() == null){
                loginButton.setEnabled(true);
            }
        });
    }
    
    private void getLoginResult(LoginViewModel loginViewModel, EditText firstNameEditText, EditText lastNameEditText,
                                EditText walletAddressEditText,EditText usernameEditText, ProgressBar loadingProgressBar ){
        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }

            if (loginResult.getSuccess() != null) {
                updateUiWithUser(
                        loginResult.getSuccess(),
                        firstNameEditText.getText().toString(),
                        lastNameEditText.getText().toString(),
                        walletAddressEditText.getText().toString(),
                        usernameEditText.getText().toString());
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
            finish();
        });
    }
    
    private void updateUiWithUser(LoggedInUserView model, String firstName, String lastName, String walletAddress, String username) {
        // model.notify();

        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(walletAddress);

            jsonObject.put("email", this.email);
            jsonObject.put("username", username);
            jsonObject.put("firstName", firstName);
            jsonObject.put("lastName", lastName);
            jsonObject.put("walletAddress", jsonArray);
            jsonObject.put("googleIdToken", this.googleIdToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RegisterUser getAuth = new RegisterUser(jsonObject);
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
            mainActivity.putExtra("email", this.email);
            mainActivity.putExtra("googleIdToken", this.googleIdToken);
            startActivity(mainActivity);
        } else {
            Log.e("as", serverResponse.toString());
            System.out.print(serverResponse);
        }
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    class RegisterUser implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private Integer value;
        private final JSONObject jsonObject;

        public RegisterUser(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
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
}