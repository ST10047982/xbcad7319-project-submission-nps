package com.xbcad.xbcad7319_physiotherapyapp.ui.home_patient

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xbcad.xbcad7319_physiotherapyapp.R
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiService
import com.xbcad.xbcad7319_physiotherapyapp.ui.Notification
import com.xbcad.xbcad7319_physiotherapyapp.ui.NotificationsResponse

class HomePatientFragment : Fragment() {

    private lateinit var txtNotificationCount: TextView
    private val notificationList = mutableListOf<Notification>()
    private val TAG = "HomePatientFragment"

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(
            ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home_patient, container, false)

        // Initialize TextView for notification count
        txtNotificationCount = view.findViewById(R.id.txtNotificationCount)

        // Load notifications
        loadPatientNotifications()

        // Initialize buttons and set click listeners
        setupNavigationButtons(view)

        return view
    }

    private fun setupNavigationButtons(view: View) {
        view.findViewById<ImageButton>(R.id.ibtnBookAppointment).setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_app)
        }
        view.findViewById<ImageButton>(R.id.ibtnMedical_History).setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_medical_history)
        }
        view.findViewById<ImageButton>(R.id.ibtnMedical_Tests).setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_medical_tests)
        }
        view.findViewById<ImageButton>(R.id.ibtnProfile).setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_patient_profile)
        }
        view.findViewById<ImageButton>(R.id.ibtnNotifications).setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_notifications_patient)
        }
        view.findViewById<ImageButton>(R.id.ibtnIntake_Forms).setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_intake_forms)
        }
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val token = sharedPref.getString("bearerToken", null)

        if (token.isNullOrEmpty()) {
            Log.d(TAG, "Token is null. Navigating to main menu.")
            navigateToMainMenu()
            return
        }

        apiService.logoutUser("Bearer $token").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Logout successful.")
                    sharedPref.edit().clear().apply()
                    Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()

                    navigateToMainMenu()
                } else {
                    Log.e(TAG, "Logout failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "Error during logout", t)
            }
        })
    }

    private fun navigateToMainMenu() {
        findNavController().navigate(R.id.action_nav_home_patient_to_nav_main_menu)
    }

    private fun loadPatientNotifications() {
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val token = sharedPref.getString("bearerToken", null)

        if (token.isNullOrEmpty()) {
            Log.d(TAG, "Token is null. Cannot load notifications.")
            return
        }

        apiService.getPatientNotifications("Bearer $token").enqueue(object : Callback<NotificationsResponse> {
            override fun onResponse(call: Call<NotificationsResponse>, response: Response<NotificationsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.notifications?.let { notifications ->
                        updateNotifications(notifications)
                    } ?: Log.e(TAG, "Empty notifications response.")
                } else {
                    Log.e(TAG, "Failed to fetch notifications: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                Log.e(TAG, "Error fetching notifications", t)
            }
        })
    }

    private fun updateNotifications(newNotifications: List<Notification>) {
        notificationList.clear()
        notificationList.addAll(newNotifications)
        updateNotificationCount(notificationList.size)
    }

    private fun updateNotificationCount(count: Int) {
        txtNotificationCount.text = count.toString()
        txtNotificationCount.visibility = if (count > 0) View.VISIBLE else View.GONE

        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("notificationCount", count)
            apply()
        }
    }
}
