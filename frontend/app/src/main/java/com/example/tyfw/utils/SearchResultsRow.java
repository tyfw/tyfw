package com.example.tyfw.utils;

public class SearchResultsRow {
    // This code is taken from and adapted from (this includes relevant .xml files):
    // https://stackoverflow.com/questions/34518421/adding-a-scoreboard-to-an-android-studio-application
    // https://stackoverflow.com/questions/60478873/make-a-leaderboard-using-a-listview

    private String value;

    public SearchResultsRow() {
    }

    public SearchResultsRow(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) { this.value = value; }

}