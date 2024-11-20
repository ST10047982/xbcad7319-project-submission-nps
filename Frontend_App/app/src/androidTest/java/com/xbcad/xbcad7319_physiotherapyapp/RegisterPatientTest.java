package com.xbcad.xbcad7319_physiotherapyapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.util.Log;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.xbcad.xbcad7319_physiotherapyapp.R;
import com.xbcad.xbcad7319_physiotherapyapp.ui.register_patient.RegisterPatientFragment;
import com.xbcad.xbcad7319_physiotherapyapp.ui.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegisterPatientTest {

    private FragmentScenario<RegisterPatientFragment> fragmentScenario;
    private TestNavHostController navController;

    @Before
    public void setup() {
        // Launch MainActivity and initialize the NavController
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);

        // Initialize the NavController and set the start destination
        activityScenario.onActivity(activity -> {
            navController = new TestNavHostController(activity);
            navController.setGraph(R.navigation.mobile_navigation);
            navController.setCurrentDestination(R.id.nav_register_patient); // Set the start destination
        });

        // Launch RegisterPatientFragment
        fragmentScenario = FragmentScenario.launchInContainer(RegisterPatientFragment.class);

        // Set the NavController for the fragment
        fragmentScenario.onFragment(fragment -> {
            Navigation.setViewNavController(fragment.requireView(), navController);
        });
    }

    @Test
    public void testSuccessfulRegistration() {
        // Simulate entering registration details
        onView(withId(R.id.etxtName)).perform(typeText("John"), closeSoftKeyboard());
        onView(withId(R.id.etxtSurame)).perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.etxtMedical_Name)).perform(typeText("Medical Center"), closeSoftKeyboard());
        onView(withId(R.id.etxtMedical_Aid_Number)).perform(typeText("1234567890"), closeSoftKeyboard());
        onView(withId(R.id.etxtPhone_Number)).perform(typeText("1234567890"), closeSoftKeyboard());
        onView(withId(R.id.etxtEmail)).perform(typeText("johndoe@example.com"), closeSoftKeyboard());
        onView(withId(R.id.etxtUsername)).perform(typeText("johndoe"), closeSoftKeyboard());
        onView(withId(R.id.etxtPassword)).perform(replaceText("testPassword"), closeSoftKeyboard());

        // Click the register button
        onView(withId(R.id.btnSave)).perform(click());

        // Wait for the registration process to complete
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()));

        // Verify that the user is navigated to the login screen after successful registration
        fragmentScenario.onFragment(fragment -> {
            int expectedDestinationId = R.id.nav_login_patient;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.i("RegisterPatientTest", "Registration successful for user: johndoe");
        });
    }

    @Test
    public void testRegistrationWithEmptyFields() {
        // Click the register button without entering credentials
        onView(withId(R.id.btnSave)).perform(click());

        // Verify that the user remains on the registration screen
        fragmentScenario.onFragment(fragment -> {
            int expectedDestinationId = R.id.nav_register_patient;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.w("RegisterPatientTest", "Attempted registration with empty fields.");
        });

        // Optionally, check for error messages in case of validation failure
        onView(withId(R.id.etxtName)).check(matches(withText("")));  // Ensure the name field is still empty
        onView(withId(R.id.etxtSurame)).check(matches(withText("")));  // Ensure the surname field is still empty
        onView(withId(R.id.etxtMedical_Name)).check(matches(withText("")));  // Check other required fields

        // Optionally, verify if validation message is shown (if you have such a message for empty fields)
        // Example: onView(withText("Name is required")).check(matches(isDisplayed()));
    }

}
