package com.example.tyfw;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.tyfw.ui.login.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WalletAddressTests {
    static String invalid_wallet_address = "Address must be =42 characters and begin with 0x";

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
    public void validEthereumAddress1() {
        onView(withId(R.id.first_name)).perform(typeText("John"));
        onView(withId(R.id.last_name)).perform(typeText("Doe"));
        // This is a valid Ethereum wallet
        onView(withId(R.id.wallet_profile)).perform(typeText("0xDA9dfA130Df4dE4673b89022EE50ff26f6EA73Cf")).perform(closeSoftKeyboard());

        onView(withId(R.id.wallet_profile)).check(matches(not(hasErrorText(invalid_wallet_address))));
        onView(withId(R.id.username)).perform(typeText("testuser")).perform(closeSoftKeyboard());

        onView(withId(R.id.login)).check(matches(isEnabled()));
        final long startTime = System.currentTimeMillis();
        onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.home_graph_options)).check(matches(withSpinnerText(containsString("Today"))));
        final long endTime = System.currentTimeMillis();
        // To verify non functional requirements
        assert(startTime - endTime < 5000);
    }

    @Test
    public void validEthereumAddress2() {
        // This is a different address than above
        onView(withId(R.id.first_name)).perform(typeText("John"));
        onView(withId(R.id.last_name)).perform(typeText("Doe"));
        // This is a valid Ethereum wallet
        onView(withId(R.id.wallet_profile)).perform(typeText("0x44C1767ED909E808cee9a92d016CE3956d60871F")).perform(closeSoftKeyboard());

        onView(withId(R.id.wallet_profile)).check(matches(not(hasErrorText(invalid_wallet_address))));
        onView(withId(R.id.username)).perform(typeText("testuser")).perform(closeSoftKeyboard());

        onView(withId(R.id.login)).check(matches(isEnabled()));
        final long startTime = System.currentTimeMillis();
        onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.home_graph_options)).check(matches(withSpinnerText(containsString("Today"))));
        final long endTime = System.currentTimeMillis();
        // To verify non functional requirements
        assert(startTime - endTime < 5000);
    }
}