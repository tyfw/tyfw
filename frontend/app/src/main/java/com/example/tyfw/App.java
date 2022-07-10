package com.example.tyfw;

import android.app.Application;

public class App extends Application {
    private String googleIdToken;
    private String email;
    private String username;

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

    public void setUsername(String username) { this.username = username; }

    public String getUsername() { return this.username; }
}
