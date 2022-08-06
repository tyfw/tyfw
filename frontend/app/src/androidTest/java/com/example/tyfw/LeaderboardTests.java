package com.example.tyfw;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.IsNot.not;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.tyfw.api.APICallers;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LeaderboardTests {

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", "tyfw.cpen321@gmail.com");
        bundle.putString("googleIdToken", "testgoogleidtoken");
        intent.putExtras(bundle);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(intent);
    public View decorView;

    @Before
    public void loadDecorView(){
        activityScenarioRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @Test
    public void openLeaderboardView() {
        SystemClock.sleep(2000);
        // Navigate to Leaderboard fragment
        onView(allOf(withId(R.id.navigation_leaderboard), withContentDescription("Leaderboard"))).perform(click());

        // Verify that the Leaderboard fragment is shown on screen
        onView(withId(R.id.leaderboard_container)).check(matches(isDisplayed()));

        // Verify that the Leaderboard list is shown on screen
        onView(withId(R.id.list)).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(0).onChildView(withText("Username or wallet address")).check(matches(withText("Username or wallet address"))).check(matches(not(isClickable())));
    }

    @Test
    public void viewOwnProfile() {
        SystemClock.sleep(2000);
        // Navigate to Leaderboard fragment
        onView(allOf(withId(R.id.navigation_leaderboard), withContentDescription("Leaderboard"))).perform(click());

        // Verify that the Leaderboard fragment is shown on screen
        onView(withId(R.id.leaderboard_container)).check(matches(isDisplayed()));

        // Verify that the Leaderboard list is shown on screen
        onView(withId(R.id.list)).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(1).perform(click());

        onView(withId(R.id.home_frag)).check(matches(isDisplayed()));
    }

    @Test
    public void addAndViewFriendProfile() {
        // Navigate to Search fragment
        onView(allOf(withId(R.id.navigation_search), withContentDescription("Search"))).perform(click());

        // Verify that the Search fragment is shown on screen
        onView(withId(R.id.search_input)).perform(replaceText("Cooper"), closeSoftKeyboard());

        SystemClock.sleep(1000);

        onView(withId(R.id.search_button)).perform(click());

        SystemClock.sleep(1000);

        onView(withId(R.id.search_results_list)).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.search_results_list)).atPosition(1).perform(click());
        onView(withId(R.id.profileActivity)).check(matches(isDisplayed()));

        // Verify that the friend button exists and works
        onView(withId(R.id.friend_button)).check(matches(isDisplayed())).check(matches(isClickable()));
        SystemClock.sleep(1000);

        onView(withId(R.id.friend_button)).perform(click()).check(matches(withText("Friends")));

        pressBack(); // exit to search
        pressBack(); // exit to main activity

        SystemClock.sleep(2000);
        // Navigate to Leaderboard fragment
        onView(allOf(withId(R.id.navigation_leaderboard), withContentDescription("Leaderboard"))).perform(click());

        // Verify that the Leaderboard fragment is shown on screen
        onView(withId(R.id.leaderboard_container)).check(matches(isDisplayed()));

        // Verify that the Leaderboard list is shown on screen
        onView(withId(R.id.list)).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(2).perform(click());

        onView(withId(R.id.profileActivity)).check(matches(isDisplayed()));
    }

    @After
    public void deleteFriends() {
        APICallers.DeleteFriend delFriend = new APICallers.DeleteFriend("tyfw.cpen321@gmail.com", "Cooper");
        Thread delFriendThread = new Thread(delFriend);
        delFriendThread.start();
        try {
            delFriendThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
