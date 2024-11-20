package com.xbcad.xbcad7319_physiotherapyapp.ui.appointment_notes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xbcad.xbcad7319_physiotherapyapp.R
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiClient

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiService
import com.xbcad.xbcad7319_physiotherapyapp.ui.AppointmentDetails
import java.io.IOException

class AppointmentNotesFragment : Fragment() {

    private lateinit var listAppointments: ListView
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private var selectedAppointmentId: String = ""
    private lateinit var sharedPref: SharedPreferences
    private lateinit var etxtNotes: EditText

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(
            ApiService::class.java)
    }

    private val TAG = "AppointmentNotesFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_appointment_notes, container, false)

        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        listAppointments = view.findViewById(R.id.listAppointments)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnSave = view.findViewById(R.id.btnSave)
        etxtNotes = view.findViewById(R.id.etxtNotes)

        // Set up ListView item click listener to select an appointment
        listAppointments.setOnItemClickListener { _, _, position, _ ->
            val appointment = listAppointments.adapter.getItem(position) as AppointmentDetails
            selectedAppointmentId = appointment.id
            Log.d(TAG, "Selected appointment ID: $selectedAppointmentId")
            showToast("Selected Appointment: ${selectedAppointmentId}")
        }

        // Set up the cancel button click listener
        btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_nav_app_notes_to_nav_home_staff)
        }

        // Set up the save button click listener
        btnSave.setOnClickListener {
            if (selectedAppointmentId.isNotEmpty()) {
                val notes = etxtNotes.text.toString()
                addAppointmentNotes(selectedAppointmentId, notes)
            } else {
                Toast.makeText(requireContext(), "Please select an appointment to add notes", Toast.LENGTH_SHORT).show()
            }
        }

        fetchAndDisplayAppointments()

        // Home button navigation
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_app_notes_to_nav_home_staff)
        }
        return view
    }

    private fun fetchAndDisplayAppointments() {
        // Get the token from SharedPreferences
        val tokenResponse = sharedPref.getString("bearerToken", null)

        // Check if the token is null
        if (tokenResponse == null) {
            Log.d(TAG, "Token is null, user not logged in.")
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Use the token directly without parsing it as JSON
        Log.d(TAG, "Using Token: $tokenResponse")

        // Make the API call to get confirmed appointments
        val call = apiService.getAllAppointments("Bearer $tokenResponse")
        call.enqueue(object : Callback<List<AppointmentDetails>> {
            override fun onResponse(call: Call<List<AppointmentDetails>>, response: Response<List<AppointmentDetails>>) {
                if (response.isSuccessful) {
                    response.body()?.let { appointments ->
                        populateListView(appointments)
                    } ?: Log.d(TAG, "No confirmed appointments found")
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
    }


    private fun populateListView(appointments: List<AppointmentDetails>) {
        val appointmentDescriptions = appointments.map { appointment ->
            val datePart = appointment.date.split("T")[0]
            "Date: $datePart\nTime: ${appointment.time}\nDescription: ${appointment.description}"
        }
        Log.d(TAG, "Appointments for ListView: $appointmentDescriptions")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, appointmentDescriptions)
        listAppointments.adapter = adapter

        listAppointments.setOnItemClickListener { _, _, position, _ ->
            val selectedAppointment = appointments[position]
            selectedAppointmentId = selectedAppointment.id
            Log.d(TAG, "Selected appointment ID: $selectedAppointmentId")

        }
    }

    private fun addAppointmentNotes(appointmentId: String, notes: String) {
        Log.d(TAG, "Adding appointment notes for ID: $appointmentId, Notes: $notes")

        // Get the JWT token from SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val tokenResponse = sharedPref.getString("bearerToken", null)

        // Check if the token is null
        if (tokenResponse == null) {
            Log.d(TAG, "Token is null, user not logged in.")
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Directly use the token in the Authorization header
        Log.d(TAG, "Using Token: $tokenResponse")

        // Create a map for the request body (it will automatically be converted to JSON)
        val requestBody = mapOf("notes" to notes)

        // Create the API call with the Bearer token in the header
        val call = apiService.addAppointmentNotes("Bearer $tokenResponse", appointmentId, requestBody)

        Log.d(TAG, "Making API call to add notes...")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Notes added successfully, Response Code: ${response.code()}")
                    Toast.makeText(requireContext(), "Notes added successfully", Toast.LENGTH_SHORT).show()
                    etxtNotes.text.clear() // Clear notes input field
                    fetchAndDisplayAppointments() // Refresh appointment list
                    findNavController().navigate(R.id.action_nav_app_notes_to_nav_home_staff)
                } else {
                    val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "Failed to add notes: HTTP ${response.code()} - $errorResponse")
                    Toast.makeText(requireContext(), "Failed to add notes: $errorResponse", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Network error while adding notes: ${t.message}", t)
                if (t is IOException) {
                    Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "An unexpected error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}




