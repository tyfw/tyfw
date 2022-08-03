package com.example.tyfw;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SearchUserTests {
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
        SystemClock.sleep(2500);

        onView(withText("No users found with the requested search")).inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void searchAllAddresses() {
        SystemClock.sleep(2000);
        // Navigate to Search fragment
        onView(allOf(withId(R.id.navigation_search), withContentDescription("Search"))).perform(click());

        // Verify that the Search fragment is shown on screen
        onView(withId(R.id.search_input)).perform(replaceText("0x"), closeSoftKeyboard());

        SystemClock.sleep(1000);

        onView(withId(R.id.search_button)).perform(click());

        SystemClock.sleep(1000);

        onView(withId(R.id.search_results_list)).check(matches(isDisplayed()));

        onData(anything()).inAdapterView(withId(R.id.search_results_list)).atPosition(0).check(matches(not(isClickable())));
    }

    @Test
    public void searchProfileVsAddress() {
        SystemClock.sleep(2000);
        // Navigate to Search fragment
        onView(allOf(withId(R.id.navigation_search), withContentDescription("Search"))).perform(click());

        // Search for Cooper user
        onView(withId(R.id.search_input)).perform(replaceText("Cooper"), closeSoftKeyboard());

        SystemClock.sleep(1000);

        onView(withId(R.id.search_button)).perform(click());

        SystemClock.sleep(1000);

        onView(withId(R.id.search_results_list)).check(matches(isDisplayed()));

        onData(anything()).inAdapterView(withId(R.id.search_results_list)).atPosition(1).onChildView(withText("Cooper")).check(matches(withText("Cooper")));

        pressBack();

        // Search for Tesla user
        onView(withId(R.id.search_input)).perform(replaceText("Tesla"), closeSoftKeyboard());

        SystemClock.sleep(1000);

        onView(withId(R.id.search_button)).perform(click());

        SystemClock.sleep(1000);

        onView(withId(R.id.search_results_list)).check(matches(isDisplayed()));

        onData(anything()).inAdapterView(withId(R.id.search_results_list)).atPosition(1).onChildView(withText("Tesla")).check(matches(withText("Tesla")));

        pressBack();

        // Search for wallet address 0xAf1931c20
        onView(withId(R.id.search_input)).perform(replaceText("0xAf1931c20"), closeSoftKeyboard());

        SystemClock.sleep(1000);

        onView(withId(R.id.search_button)).perform(click());

        SystemClock.sleep(1000);

        onView(withId(R.id.search_results_list)).check(matches(isDisplayed()));

        onData(anything()).inAdapterView(withId(R.id.search_results_list)).atPosition(1).onChildView(withText("Luna")).check(matches(withText("Luna")));
    }

    @Test
    public void searchAndViewMockUser() {
        SystemClock.sleep(2000);
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

        // Check that everything on the profile view is shown

        // Verify that profile information exists
        onView(withId(R.id.profile_username)).check(matches(isDisplayed())).check(matches(withText("Cooper")));
        onView(withId(R.id.wallet_address)).check(matches(isDisplayed())).check(matches(withText("0x3D8FC1CFfAa110F7A7F9f8BC237B73d54C4aBf61")));
        onView(withId(R.id.profile_default_pic)).check(matches(isDisplayed()));
    }

    @Test
    public void searchAndAddFriend() {
        SystemClock.sleep(2000);
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
        onView(withId(R.id.friend_button)).check(matches(isDisplayed())).check(matches(isClickable())).check(matches(withText(R.string.add_friend_button)));
        SystemClock.sleep(1000);
        onView(withId(R.id.friend_button)).perform(click()).check(matches(withText("Friends")));
        SystemClock.sleep(1000);
    }

    @Test
    public void searchAndViewOtherWallet() {
        SystemClock.sleep(2000);
        // Navigate to Search fragment
        onView(allOf(withId(R.id.navigation_search), withContentDescription("Search"))).perform(click());

        // Verify that the Search fragment is shown on screen
        onView(withId(R.id.search_input)).perform(replaceText("0xAf1931c20"), closeSoftKeyboard());

        SystemClock.sleep(1000);

        onView(withId(R.id.search_button)).perform(click());

        SystemClock.sleep(1000);

        onView(withId(R.id.search_results_list)).check(matches(isDisplayed()));

        onData(anything()).inAdapterView(withId(R.id.search_results_list)).atPosition(1).perform(click());

        onView(withId(R.id.profileActivity)).check(matches(isDisplayed()));

        // Verify spinner options
        onView(withId(R.id.profile_graph_options)).check(matches(withSpinnerText("Today")));
        onView(withId(R.id.wallet_chart)).check(matches(isDisplayed()));

        onView(withId(R.id.profile_graph_options)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Last Week"))).perform(click());
        onView(withId(R.id.profile_graph_options)).check(matches(withSpinnerText("Last Week")));
        onView(withId(R.id.wallet_chart)).check(matches(isDisplayed()));

        onView(withId(R.id.profile_graph_options)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Last Month"))).perform(click());
        onView(withId(R.id.profile_graph_options)).check(matches(withSpinnerText("Last Month")));
        onView(withId(R.id.wallet_chart)).check(matches(isDisplayed()));

        onView(withId(R.id.profile_graph_options)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Last Year"))).perform(click());
        onView(withId(R.id.profile_graph_options)).check(matches(withSpinnerText("Last Year")));
        onView(withId(R.id.wallet_chart)).check(matches(isDisplayed()));
    }



}