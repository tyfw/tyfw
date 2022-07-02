package com.example.tyfw.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.tyfw.data.LoginRepository;
import com.example.tyfw.data.Result;
import com.example.tyfw.data.model.LoggedInUser;
import com.example.tyfw.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String firstName, String lastName, String email, String walletAddress) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(firstName, lastName, email, walletAddress);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getFirstName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String firstName, String lastName, String email, String walletAddress) {
        if (!isFirstNameValid(firstName)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_first_name, null, null, null));
        } else if (!isLastNameValid(lastName)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_last_name, null, null));
        } else if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(null, null, R.string.invalid_email, null));
        } else if (!isWalletAddressValid(walletAddress)) {
            loginFormState.setValue(new LoginFormState(null, null, null, R.string.invalid_wallet_address));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isFirstNameValid(String firstName) {
        if (firstName == null) {
            return false;
        }
        if (firstName.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(firstName).matches();
        } else {
            return !firstName.trim().isEmpty();
        }
    }

    private boolean isLastNameValid(String lastName) {
        if (lastName == null) {
            return false;
        }
        if (lastName.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(lastName).matches();
        } else {
            return !lastName.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isEmailValid(String email) {
        return email != null && email.trim().length() > 5 && !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // A placeholder wallet address validation check
    // TODO: implement this logic
    private boolean isWalletAddressValid(String email) {
        return true;
    }
}