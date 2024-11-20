package com.xbcad.xbcad7319_physiotherapyapp.ui.notifications_staff

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xbcad.xbcad7319_physiotherapyapp.R
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiService
import com.xbcad.xbcad7319_physiotherapyapp.ui.NotificationsResponse

class NotificationsStaffFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var notificationAdapter: ArrayAdapter<String>
    private val notificationList = mutableListOf<String>() // To hold notification data

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(
            ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications_staff, container, false)

        listView = view.findViewById(R.id.listNotifications)

        // Initialize the adapter and set it to the ListView
        notificationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, notificationList)
        listView.adapter = notificationAdapter

        fetchNotifications()

        // Home button navigation
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_notifications_staff_to_nav_home_staff)
        }

        return view
    }

    // Function to fetch notifications using the API
    private fun fetchNotifications() {
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val tokenResponse = sharedPref.getString("bearerToken", null)

        if (tokenResponse == null) {
            Log.d(TAG, "Token is null, user not logged in.")
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Call the API to get staff notifications
        val call = apiService.getStaffNotifications("Bearer $tokenResponse")

        call.enqueue(object : Callback<NotificationsResponse> {
            override fun onResponse(call: Call<NotificationsResponse>, response: Response<NotificationsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { notificationsResponse ->
                        if (notificationsResponse.notifications.isEmpty()) {
                            Log.d(TAG, "No notifications found for this user")
                            Toast.makeText(requireContext(), "No notifications available", Toast.LENGTH_SHORT).show()
                        } else {
                            notificationList.clear()
                            notificationsResponse.notifications.forEach { notification ->
                                val datePart = notification.date.split("T")[0] // Format date
                                val timePart = notification.time // Adjust if necessary
                                val description = notification.message

                                val formattedNotification = "Date: $datePart\nTime: $timePart\nDescription: $description"
                                notificationList.add(formattedNotification)
                            }
                            notificationAdapter.notifyDataSetChanged()
                        }
                    } ?: run {
                        Log.d(TAG, "Response body is null")
                        Toast.makeText(requireContext(), "Failed to fetch notifications", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Failed to fetch notifications: $errorBody")
                    if (errorBody?.contains("No notifications found") == true) {
                        Toast.makeText(requireContext(), "No notifications available", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to fetch notifications", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                Log.e(TAG, "Error fetching notifications: ${t.message}", t)
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    companion object {
        const val TAG = "NotificationsStaffFragment"
    }
}
