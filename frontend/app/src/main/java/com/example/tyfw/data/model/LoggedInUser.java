package com.example.tyfw.data.model;

import java.util.UUID;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private final String username;
    private final String walletAddress;
    private final String firstName;
    private final String lastName;
    private String googleIdToken;
    private final String email;
    private UUID uuid;

    public LoggedInUser(String username, String walletAddress, String firstName, String lastName, String email) {
        this.username = username;
        this.walletAddress = walletAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uuid = UUID.randomUUID();
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}