package com.example.tyfw.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer firstNameError;
    @Nullable
    private Integer lastNameError;
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer walletAddressError;
    private boolean isDataValid;

    LoginFormState(@Nullable Integer firstNameError, @Nullable Integer lastNameError,
                   @Nullable Integer emailError,  @Nullable Integer walletAddressError) {
        this.firstNameError = firstNameError;
        this.lastNameError = lastNameError;
        this.emailError = emailError;
        this.walletAddressError = walletAddressError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.firstNameError = null;
        this.lastNameError = null;
        this.emailError = null;
        this.walletAddressError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getFirstNameError() {
        return firstNameError;
    }

    @Nullable
    Integer getLastNameError() {
        return lastNameError;
    }

    @Nullable
    Integer getEmailError() { return emailError; }

    @Nullable
    Integer getWalletAddressError() {
        return walletAddressError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}