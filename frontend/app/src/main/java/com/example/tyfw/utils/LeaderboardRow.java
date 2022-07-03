package com.example.tyfw.utils;

public class LeaderboardRow {
    // This code is taken from and adapted from (this includes relevant .xml files):
    // https://stackoverflow.com/questions/34518421/adding-a-scoreboard-to-an-android-studio-application
    // https://stackoverflow.com/questions/60478873/make-a-leaderboard-using-a-listview

    private String name;
    private String value;

    public LeaderboardRow() {
    }

    public LeaderboardRow(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) { this.value = value; }

}