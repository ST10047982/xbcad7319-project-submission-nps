package com.xbcad.xbcad7319_physiotherapyapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
import com.xbcad.xbcad7319_physiotherapyapp.ui.app_patient.app_patient.AppointmentPatientFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AppointmentPatientTest {

    private FragmentScenario<AppointmentPatientFragment> fragmentScenario;
    private TestNavHostController navController;

    @Before
    public void setup() {
        // Launch MainActivity and initialize the NavController
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);

        // Initialize the NavController and set the start destination
        activityScenario.onActivity(activity -> {
            navController = new TestNavHostController(activity);
            navController.setGraph(R.navigation.mobile_navigation);
            navController.setCurrentDestination(R.id.action_nav_app_to_nav_book_app_patient); // Set the start destination
        });

        // Launch AppointmentPatientFragment
        fragmentScenario = FragmentScenario.launchInContainer(AppointmentPatientFragment.class);

        // Set the NavController for the fragment
        fragmentScenario.onFragment(fragment -> {
            Navigation.setViewNavController(fragment.requireView(), navController);
        });
    }

    @Test
    public void testNavigateToHome() {
        // Simulate clicking the Home button
        onView(withId(R.id.ibtnHome)).perform(click());

        // Verify that the user is navigated to the home screen after clicking Home
        fragmentScenario.onFragment(fragment -> {
            int expectedDestinationId = R.id.action_nav_app_to_nav_home_patient;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.i("AppointmentPatientTest", "Navigated to Home screen.");
        });
    }

    @Test
    public void testNavigateToBookAppointment() {
        // Simulate clicking the Book Appointment button
        onView(withId(R.id.ibtnBook_Appointment)).perform(click());

        // Verify that the user is navigated to the Book Appointment screen
        fragmentScenario.onFragment(fragment -> {
            int expectedDestinationId = R.id.nav_book_app_patient;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.i("AppointmentPatientTest", "Navigated to Book Appointment screen.");
        });
    }

    @Test
    public void testNavigateToRescheduleAppointment() {
        // Simulate clicking the Reschedule Appointment button
        onView(withId(R.id.ibtnReschedule_Appointment)).perform(click());

        // Verify that the user is navigated to the Reschedule Appointment screen
        fragmentScenario.onFragment(fragment -> {
            int expectedDestinationId = R.id.nav_reschedule_app_patient;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.i("AppointmentPatientTest", "Navigated to Reschedule Appointment screen.");
        });
    }

    @Test
    public void testNavigateToCancelAppointment() {
        // Simulate clicking the Cancel Appointment button
        onView(withId(R.id.ibtnCancel_Appointment)).perform(click());

        // Verify that the user is navigated to the Cancel Appointment screen
        fragmentScenario.onFragment(fragment -> {
            int expectedDestinationId = R.id.nav_cancel_app_patient;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.i("AppointmentPatientTest", "Navigated to Cancel Appointment screen.");
        });
    }
}
