package com.example.tyfw;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.getIntents;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.click;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.hamcrest.Matchers.not;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tyfw.ui.login.LoginActivity;

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
public class LoginUserTests {
    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", "tyfw.cpen321@gmail.com");
        bundle.putString("googleIdToken", "testgoogleidtoken");
        intent.putExtras(bundle);
    }

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(intent);

    @Test
    public void listGoesOverTheFold() {

        SystemClock.sleep(2000);

        onView(withId(R.id.first_name)).perform(typeText("John"));
        onView(withId(R.id.last_name)).perform(typeText("Doe"));
        // This is a valid Ethereum wallet
        onView(withId(R.id.wallet_profile)).perform(typeText("0xDA9dfA130Df4dE4673b89022EE50ff26f6EA73Cf"));
        onView(withId(R.id.username)).perform(typeText("testuser"));


        // onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.login)).check(matches(isClickable()));

        SystemClock.sleep(200000);
        onView(withId(R.id.home_graph_options)).check(matches(withSpinnerText(containsString("Today"))));
    }

}