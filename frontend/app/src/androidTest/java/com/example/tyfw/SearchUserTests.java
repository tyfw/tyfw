package com.example.tyfw;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import static org.hamcrest.core.StringContains.containsString;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

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
        bundle.putString("email", "dryden.wiebe@gmail.com");
        bundle.putString("googleIdToken", "fakegoogleidtoken");
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

    @Test
    public void listGoesOverTheFold() {
        /*
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        // intent.setType("text/plain");
        intent.putExtra("email", "dryden.wiebe@gmail.com");
        intent.putExtra("googleIdToken", "fakegoogleidtoken");
        activityRule.launchActivity(intent);
        */
        SystemClock.sleep(2000);
        onView(withId(R.id.home_graph_options)).check(matches(withSpinnerText(containsString("Today"))));
    }

}