package com.example.tyfw;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tyfw.ui.login.LoginActivity;

import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;

import androidx.test.core.app.ApplicationProvider;
import static org.hamcrest.core.StringContains.containsString;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class LoginUserTests {
    static String invalid_wallet_address = "Address must be =42 characters and begin with 0x";
    static String invalid_first_name = "Not a valid first name";
    static String invalid_last_name = "Not a valid last name";
    static String invalid_email = "Not a valid email";

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
    public void defaultLoginButtonDisabled() {
        onView(withId(R.id.login)).check(matches(not(isEnabled())));
    }

    @Test
    public void invalidFirstName() {
        onView(withId(R.id.first_name)).perform(typeText(" "));
        onView(withId(R.id.first_name)).check(matches(hasErrorText(invalid_first_name)));
    }

    @Test
    public void invalidLastName() {
        onView(withId(R.id.first_name)).perform(typeText("John"));
        onView(withId(R.id.last_name)).perform(typeText(" "));
        SystemClock.sleep(15000);
        onView(withId(R.id.last_name)).check(matches(hasErrorText(invalid_last_name)));
    }

    @Test
    public void invalidEthereumAddress() {
        onView(withId(R.id.first_name)).perform(typeText("John"));
        onView(withId(R.id.last_name)).perform(typeText("Doe"));
        // This is an invalid Ethereum wallet
        onView(withId(R.id.wallet_profile)).perform(typeText("0xDA9dfA130Df4dE4673b89022EE50ff26f6EA73"));

        onView(withId(R.id.wallet_profile)).check(matches(hasErrorText(invalid_wallet_address)));
        onView(withId(R.id.wallet_profile)).perform(closeSoftKeyboard());
        onView(withId(R.id.login)).check(matches(not(isEnabled())));
    }

    @Test
    public void validEthereumAddress() {
        Intents.init();

        onView(withId(R.id.first_name)).perform(typeText("John"));
        onView(withId(R.id.last_name)).perform(typeText("Doe"));
        // This is a valid Ethereum wallet
        onView(withId(R.id.wallet_profile)).perform(typeText("0xDA9dfA130Df4dE4673b89022EE50ff26f6EA73Cf")).perform(closeSoftKeyboard());

        onView(withId(R.id.wallet_profile)).check(matches(not(hasErrorText(invalid_wallet_address))));
        onView(withId(R.id.username)).perform(typeText("testuser")).perform(closeSoftKeyboard());

        onView(withId(R.id.login)).check(matches(isEnabled()));
        onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.home_graph_options)).check(matches(withSpinnerText(containsString("Today"))));
        intended(hasComponent(MainActivity.class.getName()));
        Intents.release();
    }
}