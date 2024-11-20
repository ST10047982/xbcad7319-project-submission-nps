package com.xbcad.xbcad7319_physiotherapyapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.xbcad.xbcad7319_physiotherapyapp.R;
import com.xbcad.xbcad7319_physiotherapyapp.ui.MainActivity;
import com.xbcad.xbcad7319_physiotherapyapp.ui.login_staff.LoginStaffFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginStaffTest {

    private FragmentScenario<LoginStaffFragment> fragmentScenario;
    private TestNavHostController navController;

    @Before
    public void setup() {
        // Launch MainActivity and initialize the NavController
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);

        // Initialize the NavController and set the start destination
        activityScenario.onActivity(activity -> {
            navController = new TestNavHostController(activity);
            navController.setGraph(R.navigation.mobile_navigation);
            navController.setCurrentDestination(R.id.nav_login_staff); // Set the start destination
        });

        // Launch LoginStaffFragment
        fragmentScenario = FragmentScenario.launchInContainer(LoginStaffFragment.class);

        // Set the NavController for the fragment
        fragmentScenario.onFragment(fragment -> {
            Navigation.setViewNavController(fragment.requireView(), navController);
        });
    }

    @Test
    public void testSuccessfulLogin() {
        // Simulate entering login credentials
        onView(withId(R.id.etxtUsername)).perform(typeText("staffmember"), closeSoftKeyboard());
        onView(withId(R.id.etxtPassword)).perform(typeText("password123"), closeSoftKeyboard());

        // Click the login button
        onView(withId(R.id.btnLogin)).perform(click());

        // Add a delay to wait for the login process to complete
        try {
            Thread.sleep(2000); // Wait for the login process to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the user is navigated to the home screen after successful login
        fragmentScenario.onFragment(fragment -> {
            int expectedDestinationId = R.id.nav_home_staff;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.i("LoginStaffTest", "Login successful for user: staffmember");
        });
    }

    @Test
    public void testLoginWithEmptyFields() {
        // Click the login button without entering any credentials
        onView(withId(R.id.btnLogin)).perform(click());

        // Verify that the user remains on the login screen
        fragmentScenario.onFragment(fragment -> {
            int expectedDestinationId = R.id.nav_login_staff;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.w("LoginStaffTest", "Attempted login with empty fields.");
        });
    }

    @Test
    public void testForgotPasswordLink() {
        // Click the forgot password link
        onView(withId(R.id.txtForgotPassword)).perform(click());

        // Verify that the user is navigated to the forgot password screen
        fragmentScenario.onFragment(fragment -> {
            int expectedDestinationId = R.id.nav_forget_password_staff;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.i("LoginStaffTest", "Navigated to Forgot Password screen.");
        });
    }
}
