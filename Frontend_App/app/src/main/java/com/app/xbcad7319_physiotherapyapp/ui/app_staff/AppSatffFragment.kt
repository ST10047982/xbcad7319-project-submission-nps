package com.app.xbcad7319_physiotherapyapp.ui.app_staff

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
import com.app.xbcad7319_physiotherapyapp.R
import com.app.xbcad7319_physiotherapyapp.ui.ApiService
import com.app.xbcad7319_physiotherapyapp.ui.AppointmentDetails
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class AppSatffFragment : Fragment() {

    private lateinit var listAppointments: ListView
    private lateinit var btnBook: Button
    private lateinit var btnCancel: Button
    private var selectedAppointmentId: String = ""
    private lateinit var sharedPref: SharedPreferences

    private val apiService: ApiService by lazy {
        com.app.xbcad7319_physiotherapyapp.ui.ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
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
        }

        // Confirm booking button click listener
        btnBook.setOnClickListener {
            if (selectedAppointmentId.isNotEmpty()) {
                confirmAppointment(selectedAppointmentId)
            } else {
                Toast.makeText(requireContext(), "Please select an appointment to confirm", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancel appointment button click listener
        btnCancel.setOnClickListener {
            if (selectedAppointmentId.isNotEmpty()) {
                cancelAppointment(selectedAppointmentId)
            } else {
                Toast.makeText(requireContext(), "Please select an appointment to cancel", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch and display appointments in the list view
        fetchAndDisplayAppointments()

        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)

        // Navigate to home when home button is clicked
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_app_staff_to_nav_home_staff)
        }

        return view
    }

    private fun fetchAndDisplayAppointments() {
        val tokenResponse = sharedPref.getString("bearerToken", null)

        tokenResponse?.let { token ->
            // Directly use the token as a Bearer token for the API request
            val call = apiService.getAllAppointments("Bearer $token")
            call.enqueue(object : Callback<List<AppointmentDetails>> {
                override fun onResponse(call: Call<List<AppointmentDetails>>, response: Response<List<AppointmentDetails>>) {
                    if (response.isSuccessful) {
                        response.body()?.let { appointments ->
                            // Filter appointments to only show those with a "pending" status
                            val pendingAppointments = appointments.filter { it.status == "pending" }
                            if (pendingAppointments.isNotEmpty()) {
                                populateListView(pendingAppointments)
                            } else {
                                showToast("No pending appointments found.")
                            }
                        } ?: Log.d(TAG, "No appointments found")
                    } else {
                        Log.e(TAG, "Failed to load appointments: ${response.errorBody()?.string()}")
                        showToast("Failed to load appointments")
                    }
                }

                override fun onFailure(call: Call<List<AppointmentDetails>>, t: Throwable) {
                    Log.e(TAG, "Network error: ${t.message}")
                    showToast("Network error")
                }
            })
        } ?: Log.d(TAG, "Token is null, user not logged in.")
    }




    private fun populateListView(appointments: List<AppointmentDetails>) {
        val appointmentDescriptions = appointments.map { appointment ->
            val datePart = appointment.date.split("T")[0]  // Extract date
            // Include the status in the description
            "Date: $datePart\nTime: ${appointment.time}\nDescription: ${appointment.description}\nStatus: ${appointment.status}"
        }

        Log.d(TAG, "Appointments for ListView: $appointmentDescriptions")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, appointmentDescriptions)
        listAppointments.adapter = adapter

        listAppointments.setOnItemClickListener { _, _, position, _ ->
            val selectedAppointment = appointments[position]
            selectedAppointmentId = selectedAppointment.id
            Log.d(TAG, "Selected appointment ID: $selectedAppointmentId")
            showToast("Selected Appointment: ${selectedAppointment.id}")
        }
    }


    private fun confirmAppointment(appointmentId: String) {
        val tokenResponse = sharedPref.getString("bearerToken", null)

        tokenResponse?.let { token ->
            // Directly use the token as a Bearer token for the API request
            val call = apiService.approveAppointment("Bearer $token", appointmentId)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Appointment approved successfully", Toast.LENGTH_SHORT).show()
                        fetchAndDisplayAppointments() // Refresh the list
                        findNavController().navigate(R.id.action_nav_app_staff_to_nav_home_staff)
                    } else {
                        val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                        Log.e(TAG, "Failed to approve appointment: HTTP ${response.code()} - $errorResponse")
                        Toast.makeText(requireContext(), "Failed to approve: $errorResponse", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "Network error while approving appointment: ${t.message}", t)
                    if (t is IOException) {
                        Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "An unexpected error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } ?: Log.d(TAG, "Token is null, user not logged in.")
    }


    private fun cancelAppointment(appointmentId: String) {
        val tokenResponse = sharedPref.getString("bearerToken", null)

        tokenResponse?.let { token ->
            // Directly use the token as a Bearer token for the API request
            val call = apiService.cancelAppointment("Bearer $token", appointmentId)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Appointment canceled successfully", Toast.LENGTH_SHORT).show()
                        fetchAndDisplayAppointments() // Refresh the list to show the updated status
                        findNavController().navigate(R.id.action_nav_app_staff_to_nav_home_staff)
                    } else {
                        val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                        Log.e(TAG, "Failed to cancel appointment: HTTP ${response.code()} - $errorResponse")
                        Toast.makeText(requireContext(), "Failed to cancel: $errorResponse", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "Network error while canceling appointment: ${t.message}", t)
                    if (t is IOException) {
                        Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "An unexpected error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } ?: Log.d(TAG, "Token is null, user not logged in.")
    }



    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
