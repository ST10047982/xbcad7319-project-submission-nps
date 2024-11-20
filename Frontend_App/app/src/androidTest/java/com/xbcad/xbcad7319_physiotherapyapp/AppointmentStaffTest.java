package com.xbcad.xbcad7319_physiotherapyapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import android.util.Log;
import com.xbcad.xbcad7319_physiotherapyapp.ui.app_staff.AppSatffFragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.xbcad.xbcad7319_physiotherapyapp.R;
import com.xbcad.xbcad7319_physiotherapyapp.ui.MainActivity;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AppointmentStaffTest {

    private FragmentScenario<AppSatffFragment> fragmentScenario;
    private TestNavHostController navController;

    @Before
    public void setup() {
        // Launch MainActivity and initialize the NavController
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);

        // Initialize the NavController and set the start destination
        activityScenario.onActivity(activity -> {
            navController = new TestNavHostController(activity);
            navController.setGraph(R.navigation.mobile_navigation);
            navController.setCurrentDestination(R.id.action_nav_home_staff_to_nav_app_staff); // Set the start destination for the staff
        });

        // Launch AppointmentStaffFragment
        fragmentScenario = FragmentScenario.launchInContainer(AppSatffFragment.class);

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
            int expectedDestinationId = R.id.nav_home_staff;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.i("AppointmentStaffTest", "Navigated to Home screen.");
        });
    }

    @Test
    public void testNavigateToScheduleAppointments() {
        // Simulate clicking the Schedule Appointments button
        onView(withId(R.id.btnBook)).perform(click());

        // Verify that the user is navigated to the Schedule Appointments screen
        fragmentScenario.onFragment(fragment -> {
            int expectedDestinationId = R.id.action_nav_home_staff_to_nav_app_staff;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.i("AppointmentStaffTest", "Navigated to Schedule Appointments screen.");
        });
    }

    @Test
    public void testNavigateToRescheduleAppointments() {
        // Simulate clicking the Reschedule Appointments button
        onView(withId(R.id.ibtnReschedule_Appointment)).perform(click());

        // Verify that the user is navigated to the Reschedule Appointments screen
        fragmentScenario.onFragment(fragment -> {
            int expectedDestinationId = R.id.action_nav_home_staff_to_nav_app_staff;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.i("AppointmentStaffTest", "Navigated to Reschedule Appointments screen.");
        });
    }

    @Test
    public void testNavigateToCancelAppointments() {
        // Simulate clicking the Cancel Appointments button
        onView(withId(R.id.ibtnCancel_Appointment)).perform(click());

        // Verify that the user is navigated to the Cancel Appointments screen
        fragmentScenario.onFragment(fragment -> {
            int expectedDestinationId = R.id.action_nav_home_staff_to_nav_app_staff;
            assertEquals(expectedDestinationId, navController.getCurrentDestination().getId());
            Log.i("AppointmentStaffTest", "Navigated to Cancel Appointments screen.");
        });
    }
}
