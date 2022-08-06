package com.example.tyfw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class UserInteractionTimeTest {
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

    @Test
    public void changeGraphOptions() {
        long startTime = System.currentTimeMillis();
        onView(withId(R.id.home_graph_options)).check(matches(withSpinnerText("Today")));
        long endTime = System.currentTimeMillis();
        onView(withId(R.id.home_chart)).check(matches(isDisplayed()));
        assert(startTime - endTime < 5000);

        startTime = System.currentTimeMillis();
        onView(withId(R.id.home_graph_options)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Last Week"))).perform(click());
        endTime = System.currentTimeMillis();
        assert(startTime - endTime < 5000);
        onView(withId(R.id.home_graph_options)).check(matches(withSpinnerText("Last Week")));
        onView(withId(R.id.home_chart)).check(matches(isDisplayed()));

        startTime = System.currentTimeMillis();
        onView(withId(R.id.home_graph_options)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Last Month"))).perform(click());
        endTime = System.currentTimeMillis();
        assert(startTime - endTime < 5000);
        onView(withId(R.id.home_graph_options)).check(matches(withSpinnerText("Last Month")));
        onView(withId(R.id.home_chart)).check(matches(isDisplayed()));

        startTime = System.currentTimeMillis();
        onView(withId(R.id.home_graph_options)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Last Year"))).perform(click());
        endTime = System.currentTimeMillis();
        assert(startTime - endTime < 5000);
        onView(withId(R.id.home_graph_options)).check(matches(withSpinnerText("Last Year")));
        onView(withId(R.id.home_chart)).check(matches(isDisplayed()));
    }

    @Test
    public void navigateLeaderboard() {
        // Navigate to Leaderboard fragment
        long startTime = System.currentTimeMillis();
        onView(allOf(withId(R.id.navigation_leaderboard), withContentDescription("Leaderboard"))).perform(click());
        long endTime = System.currentTimeMillis();
        // To verify non functional requirements
        onView(withId(R.id.leaderboard_container)).check(matches(isDisplayed()));
        assert(startTime - endTime < 5000);

        // Verify that the Leaderboard list is shown on screen
        onView(withId(R.id.list)).check(matches(isDisplayed()));

        // Click on user's own profile
        startTime = System.currentTimeMillis();
        onData(anything()).inAdapterView(withId(R.id.list)).atPosition(1).perform(click());
        endTime = System.currentTimeMillis();
        onView(withId(R.id.home_frag)).check(matches(isDisplayed()));
        assert(startTime - endTime < 5000);
    }

    @Test
    public void navigateSearch() {
        // Navigate to Search fragment
        long startTime = System.currentTimeMillis();
        onView(withId(R.id.navigation_search)).perform(click());
        long endTime = System.currentTimeMillis();
        onView(withId(R.id.search_container)).check(matches(isDisplayed()));
        assert(startTime - endTime < 5000);

        // Search all users
        onView(withId(R.id.search_input)).perform(replaceText("0x"));
        startTime = System.currentTimeMillis();
        onView(withId(R.id.search_button)).perform(click());
        endTime = System.currentTimeMillis();
        onView(withId(R.id.search_results_list)).check(matches(isDisplayed()));
        assert(startTime - endTime < 5000);

        // Click into user profile
        startTime = System.currentTimeMillis();
        onData(anything()).inAdapterView(withId(R.id.search_results_list)).atPosition(1).perform(click());
        endTime = System.currentTimeMillis();
        onView(withId(R.id.profileActivity)).check(matches(isDisplayed()));
        assert(startTime - endTime < 5000);
    }

    @Test
    public void navigateChat() {
        // Navigate to Chat fragment
        long startTime = System.currentTimeMillis();
        onView(withId(R.id.navigation_social)).perform(click());
        long endTime = System.currentTimeMillis();
        onView(withId(R.id.social_container)).check(matches(isDisplayed()));
        assert(startTime - endTime < 5000);
    }

    @Test
    public void navigateSettings() {
        // Navigate to Chat fragment
        long startTime = System.currentTimeMillis();
        onView(withId(R.id.navigation_settings)).perform(click());
        long endTime = System.currentTimeMillis();
        assert(startTime - endTime < 5000);
    }

}
