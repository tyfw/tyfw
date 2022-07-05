package com.example.tyfw;

import android.app.Application;

public class App extends Application {
    private String googleIdToken;
    private String email;

    public void setGoogleIdToken(String googleIdToken) {
        this.googleIdToken = googleIdToken;
    }

    public String getGoogleIdToken() {
        return this.googleIdToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }
}
