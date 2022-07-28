package com.example.tyfw;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.tyfw.ui.search.SearchFragment;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SearchUserTests {
    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", "charlee0315@gmail.com");
        bundle.putString("googleIdToken", "testgoogleidtoken");
        intent.putExtras(bundle);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(intent);
    /*
    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(
            MainActivity.class,
            true,    // initialTouchMode
            false);  // launchActivity. False to set intent.
     */

    // Matching toasts following this answer: https://stackoverflow.com/a/64843252
    private View decorView;

    @Before
    public void loadDecorView(){
        activityScenarioRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @Test
    public void openSearchView() {
        SystemClock.sleep(2000);
        // Navigate to Search fragment
        onView(allOf(withId(R.id.navigation_search), withContentDescription("Search"))).perform(click());

        // Verify that the Search fragment is shown on screen
        onView(withId(R.id.search_container)).check(matches(isDisplayed()));

        // Verify that the Search text iput and Search button exists and can be used
        onView(withId(R.id.search_input)).check(matches(isDisplayed())).check(matches(isClickable()));

        onView(withId(R.id.search_button)).check(matches(isDisplayed())).check(matches(isClickable()));
    }

    @Test
    public void invalidSearchInput() {
        SystemClock.sleep(2000);
        // Navigate to Search fragment
        onView(allOf(withId(R.id.navigation_search), withContentDescription("Search"))).perform(click());

        // Verify that the Search fragment is shown on screen
        onView(withId(R.id.search_input)).perform(replaceText("Loren ipsum"), closeSoftKeyboard());

        onView(withId(R.id.search_button)).perform(click());

        // Introduced lag to allow Toast to pop up before validation
        SystemClock.sleep(5000);

        onView(withText("No users found with the requested search")).inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void searchAddresses() {
        SystemClock.sleep(2000);
        // Navigate to Search fragment
        onView(allOf(withId(R.id.navigation_search), withContentDescription("Search"))).perform(click());

        // Verify that the Search fragment is shown on screen
        onView(withId(R.id.search_input)).perform(replaceText("0x"), closeSoftKeyboard());

        // Introduced lag to allow Toast to pop up before validation
        SystemClock.sleep(5000);

        onView(withId(R.id.search_button)).perform(click());

        SystemClock.sleep(5000);

        onView(withId(R.id.search_results_list)).check(matches(isDisplayed()));
    }

}