package com.xbcad.xbcad7319_physiotherapyapp.ui.home_staff

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

class HomeStaffFragment : Fragment() {

    private lateinit var txtNotificationCount: TextView
    private val notificationList = mutableListOf<Notification>()
    private val TAG = "HomeStaffFragment"

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(
            ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home_staff, container, false)

        // Initialize TextView for notification count
        txtNotificationCount = view.findViewById(R.id.txtNotificationCount)

        // Load notifications
        loadStaffNotifications()

        // Initialize buttons and set OnClickListeners
        val ibtnNotifications: ImageButton = view.findViewById(R.id.ibtnNotifications)
        val ibtnPatientNotes: ImageButton = view.findViewById(R.id.ibtnPatient_Notes)
        val ibtnViewPatientNotes: ImageButton = view.findViewById(R.id.ibtnView_Patient_Notes)
        val ibtnViewPatientProfile: ImageButton = view.findViewById(R.id.ibtnPatient_Profile)
        val ibtnBilling: ImageButton = view.findViewById(R.id.ibtnBilling)
        val ibtnApp: ImageButton = view.findViewById(R.id.ibtnAppointments)
        val btnLogout: Button = view.findViewById(R.id.btnLogout)

        ibtnNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_notifications_staff)
        }
        ibtnPatientNotes.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_app_notes)
        }
        ibtnViewPatientNotes.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_view_app_notes)
        }
        ibtnViewPatientProfile.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_view_patient_profile)
        }
        ibtnBilling.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_billing)
        }
        ibtnApp.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_staff_to_nav_app_staff)
        }
        btnLogout.setOnClickListener{
            logoutUser()
        }

        return view
    }

    private fun loadStaffNotifications() {
        // Retrieve the token from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val token = sharedPref.getString("bearerToken", null)

        if (token.isNullOrEmpty()) {
            Log.d(TAG, "Token is null. Cannot load notifications.")
            return
        }

        // Make the request to get staff notifications, not patient notifications
        apiService.getStaffNotifications("Bearer $token").enqueue(object : Callback<NotificationsResponse> {
            override fun onResponse(call: Call<NotificationsResponse>, response: Response<NotificationsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.notifications?.let { notifications ->
                        // Update the UI with the fetched notifications
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
        // Update the list and the notification count
        notificationList.clear()
        notificationList.addAll(newNotifications)
        updateNotificationCount(notificationList.size)
    }

    private fun updateNotificationCount(count: Int) {
        // Update the notification count on the UI
        txtNotificationCount.text = count.toString()
        txtNotificationCount.visibility = if (count > 0) View.VISIBLE else View.GONE

        // Store the notification count in SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("notificationCount", count)
            apply()
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
        findNavController().navigate(R.id.action_nav_home_staff_to_nav_main_menu)
    }
}
