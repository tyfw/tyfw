package com.example.tyfw.utils;

public class SearchResultsRow {
    // This code is taken from and adapted from (this includes relevant .xml files):
    // https://stackoverflow.com/questions/34518421/adding-a-scoreboard-to-an-android-studio-application
    // https://stackoverflow.com/questions/60478873/make-a-leaderboard-using-a-listview

    private String username;
    private String wallet;

    public SearchResultsRow() {
    }

    public SearchResultsRow(String username, String wallet) {
        this.username = username;
        this.wallet = wallet;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) { this.username = username; }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) { this.wallet = wallet; }

}