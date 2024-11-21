package com.xbcad.xbcad7319_physiotherapyapp.ui.app_staff

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.xbcad.xbcad7319_physiotherapyapp.R
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiClient

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiService
import com.xbcad.xbcad7319_physiotherapyapp.ui.AppointmentDetails

class AppSatffFragment : Fragment() {

    private lateinit var listAppointments: ListView
    private lateinit var btnBook: Button
    private lateinit var btnCancel: Button
    private var selectedAppointmentId: String = ""
    private lateinit var sharedPref: SharedPreferences

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(
            ApiService::class.java)
    }
    private val TAG = "AppStaffFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_app_satff, container, false)

        // Initialize views
        listAppointments = view.findViewById(R.id.listAppointments)
        btnBook = view.findViewById(R.id.btnBook)
        btnCancel = view.findViewById(R.id.btnCancel)
        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        // Handle appointment selection
        listAppointments.setOnItemClickListener { _, _, position, _ ->
            val appointment = listAppointments.adapter.getItem(position) as AppointmentDetails
            selectedAppointmentId = appointment.id
            Log.d(TAG, "Selected appointment ID: $selectedAppointmentId")
        }

        // Confirm booking button click listener
        btnBook.setOnClickListener {
            if (selectedAppointmentId.isNotEmpty()) {
                 Log.d(TAG, "Confirm button clicked with appointment ID: $selectedAppointmentId")
                confirmAppointment(selectedAppointmentId)
            } else {
                Toast.makeText(requireContext(), "Please select an appointment to confirm", Toast.LENGTH_SHORT).show()
                 Log.d(TAG, "No appointment selected for confirmation")
            }
        }

        // Cancel appointment button click listener
        btnCancel.setOnClickListener {
            if (selectedAppointmentId.isNotEmpty()) {
                 Log.d(TAG, "Cancel button clicked with appointment ID: $selectedAppointmentId")
                cancelAppointment(selectedAppointmentId)
            } else {
                Toast.makeText(requireContext(), "Please select an appointment to cancel", Toast.LENGTH_SHORT).show()
                 Log.d(TAG, "No appointment selected for cancellation")
            }
        }

        // Fetch and display appointments in the list view
        fetchAndDisplayAppointments()

        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)

        // Navigate to home when home button is clicked
        ibtnHome.setOnClickListener {
             Log.d(TAG, "Home button clicked. Navigating to home.")
            findNavController().navigate(R.id.action_nav_app_staff_to_nav_home_staff)
        }

        return view
    }

    private fun fetchAndDisplayAppointments() {
        val tokenResponse = sharedPref.getString("bearerToken", null)

        tokenResponse?.let { token ->
             Log.d(TAG, "Fetching appointments with token: $token")
            val call = apiService.getAllAppointments("Bearer $token")
            call.enqueue(object : Callback<List<AppointmentDetails>> {
                override fun onResponse(call: Call<List<AppointmentDetails>>, response: Response<List<AppointmentDetails>>) {
                    if (response.isSuccessful) {
                        response.body()?.let { appointments ->
                            val pendingAppointments = appointments.filter { it.status == "pending" }
                            if (pendingAppointments.isNotEmpty()) {
                                populateListView(pendingAppointments)
                                 Log.d(TAG, "Pending appointments displayed: $pendingAppointments")
                            } else {
                                showToast("No pending appointments found.")
                                 Log.d(TAG, "No pending appointments found")
                            }
                        }
                         Log.d(TAG, "Appointments loaded successfully.")
                    } else {
                         Log.e(TAG, "Failed to load appointments: ${response.errorBody()?.string()}")
                        showToast("Failed to load appointments")
                    }
                }

                override fun onFailure(call: Call<List<AppointmentDetails>>, t: Throwable) {
                     Log.e(TAG, "Network error while fetching appointments: ${t.message}")
                    showToast("Network error")
                }
            })
        }
         Log.d(TAG, "Token is null, user not logged in.")
    }

    private fun populateListView(appointments: List<AppointmentDetails>) {
        val appointmentDescriptions = appointments.map { appointment ->
            val datePart = appointment.date.split("T")[0]
            "Date: $datePart\nTime: ${appointment.time}\nDescription: ${appointment.description}\nStatus: ${appointment.status}"
        }

         Log.d(TAG, "Populating ListView with appointments: $appointmentDescriptions")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, appointmentDescriptions)
        listAppointments.adapter = adapter

        listAppointments.setOnItemClickListener { _, _, position, _ ->
            val selectedAppointment = appointments[position]
            selectedAppointmentId = selectedAppointment.id
             Log.d(TAG, "Selected appointment: $selectedAppointment")
        }
    }

    private fun confirmAppointment(appointmentId: String) {
        val tokenResponse = sharedPref.getString("bearerToken", null)

        tokenResponse?.let { token ->
             Log.d(TAG, "Confirming appointment with ID: $appointmentId using token.")
            val call = apiService.approveAppointment("Bearer $token", appointmentId)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Appointment approved successfully", Toast.LENGTH_SHORT).show()
                        fetchAndDisplayAppointments()
                        findNavController().navigate(R.id.action_nav_app_staff_to_nav_home_staff)
                         Log.d(TAG, "Appointment confirmed successfully.")
                    } else {
                         Log.e(TAG, "Failed to confirm appointment: HTTP ${response.code()} - ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                     Log.e(TAG, "Network error while confirming appointment: ${t.message}")
                }
            })
        }
         Log.d(TAG, "Token is null, user not logged in.")
    }

    private fun cancelAppointment(appointmentId: String) {
        val tokenResponse = sharedPref.getString("bearerToken", null)

        tokenResponse?.let { token ->
             Log.d(TAG, "Canceling appointment with ID: $appointmentId using token.")
            val call = apiService.cancelAppointment("Bearer $token", appointmentId)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Appointment canceled successfully", Toast.LENGTH_SHORT).show()
                        fetchAndDisplayAppointments()
                        findNavController().navigate(R.id.action_nav_app_staff_to_nav_home_staff)
                         Log.d(TAG, "Appointment canceled successfully.")
                    } else {
                         Log.e(TAG, "Failed to cancel appointment: HTTP ${response.code()} - ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                     Log.e(TAG, "Network error while canceling appointment: ${t.message}")
                }
            })
        }
         Log.d(TAG, "Token is null, user not logged in.")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
