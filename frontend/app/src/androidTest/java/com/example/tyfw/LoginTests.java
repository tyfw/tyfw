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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginTests {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    @Test
    public void testLoginButtonValidAddress() {
        Intents.init();

        onView(withId(R.id.first_name)).perform(typeText("John"));
        onView(withId(R.id.last_name)).perform(typeText("Doe"));
        // This is a valid Ethereum wallet
        onView(withId(R.id.wallet_profile)).perform(typeText("0xDA9dfA130Df4dE4673b89022EE50ff26f6EA73Cf"));
        onView(withId(R.id.username)).perform(typeText("johndoe")).perform(closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.login)).check(matches(isClickable()));
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testLoginButtonInvalidAddress() {
        Intents.init();

        onView(withId(R.id.first_name)).perform(typeText("John"));
        onView(withId(R.id.last_name)).perform(typeText("Doe"));
        // This is an invalid Ethereum wallet
        onView(withId(R.id.wallet_profile)).perform(typeText("0xasdfasdfasdfasdfasdf"));
        onView(withId(R.id.username)).perform(typeText("johndoe")).perform(closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.login)).check(matches(not(isClickable())));
        assert(getIntents().size() == 0);
    }
}