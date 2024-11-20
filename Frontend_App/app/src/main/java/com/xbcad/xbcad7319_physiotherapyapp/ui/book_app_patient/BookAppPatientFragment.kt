package com.xbcad.xbcad7319_physiotherapyapp.ui.book_app_patient

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.xbcad.xbcad7319_physiotherapyapp.R

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiClient

import org.json.JSONException
import org.json.JSONObject
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiService
import com.xbcad.xbcad7319_physiotherapyapp.ui.BookAppointmentRequest
import com.xbcad.xbcad7319_physiotherapyapp.ui.form1.Form1State
import com.xbcad.xbcad7319_physiotherapyapp.ui.form2.Form2State

class BookAppPatientFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var timeSpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var sharedPref: SharedPreferences

    private var selectedDate: String = ""

    // Create an instance of ApiService
    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(
            ApiService::class.java)
    }

    private val TAG = "BookAppPatientFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_app_patient, container, false)


        // Initialize SharedPreferences
        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        // Initialize views
        calendarView = view.findViewById(R.id.calendarView)
        timeSpinner = view.findViewById(R.id.spinner2)
        descriptionEditText = view.findViewById(R.id.etxtDescription)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        // Populate the time spinner with times from 8:00 AM to 4:30 PM every 30 minutes
        populateTimeSpinner()

        // Get the selected date from the CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${month + 1}-$dayOfMonth"
        }

        // Save button click listener
        btnSave.setOnClickListener {
            val selectedTime = timeSpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString()

            // Validate inputs before proceeding
            if (selectedDate.isEmpty() || selectedTime.isEmpty() || description.isEmpty()) {
                // Display appropriate messages based on empty fields
                val message = when {
                    selectedDate.isEmpty() -> "Please select a date"
                    selectedTime.isEmpty() -> "Please select a time"
                    description.isEmpty() -> "Please enter a description"
                    else -> "Please fill in all fields"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Retrieve the token and username from SharedPreferences
            val tokenResponse = sharedPref.getString("bearerToken", null)
            val username = sharedPref.getString("loggedInUsername", null)

            // Check if the token and username are valid
            if (tokenResponse == null || username == null) {
                Toast.makeText(requireContext(), "User not  logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Log the token for debugging purposes
            Log.d(TAG, "Token response = $tokenResponse")

            try {
                // Ensure token is not null or empty before parsing
                if (tokenResponse.isNullOrEmpty()) {
                    throw JSONException("Token response is empty or null")
                }

                // Check if tokenResponse is a JSON object or a string
                val token = if (tokenResponse.contains("{")) {
                    val jsonObject = JSONObject(tokenResponse)
                    jsonObject.optString("token", "")
                } else {
                    tokenResponse // If it's just the token, use it directly
                }

                // Check if the token is valid
                if (token.isEmpty()) {
                    throw JSONException("Token field is missing")
                }

                // Check if the Form2 state is filled before allowing booking
                if (!Form2State.form2Filled || !Form1State.form1Filled) {

                    Log.d(TAG, "Form1 filled: ${Form1State.form1Filled}")
                    Log.d(TAG, "Form2 filled: ${Form2State.form2Filled}")
                    Toast.makeText(requireContext(), "Please fill out the required form first", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_nav_book_app_patient_to_nav_intake_forms)
                    return@setOnClickListener
                }



                // Create an AppointmentRequest with the necessary details
                val appointmentRequest = BookAppointmentRequest(
                    patient = username,  // Use the stored username
                    date = selectedDate,
                    time = selectedTime,
                    description = description
                )

                // Pass the extracted token to the API call
                bookAppointment(appointmentRequest, token)

            } catch (e: JSONException) {
                // Log the error for debugging purposes
                Log.e(TAG, "Error parsing token: ${e.message}", e)

                // Handle errors if the token parsing fails
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error parsing token", Toast.LENGTH_SHORT).show()
            }
        }

// Clear button click listener
        btnCancel.setOnClickListener {
            // Reset the CalendarView to today's date
            val currentDate = System.currentTimeMillis()
            calendarView.date = currentDate

            // Reset the Spinner to the first item "Select a time"
            timeSpinner.setSelection(0)

            // Clear the description EditText
            descriptionEditText.text.clear()


        }


        // Home button navigation
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_book_app_patient_to_nav_app)
        }

        return view
    }

    private fun populateTimeSpinner() {
        val times = listOf(
            "Select a time",
            "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM",
            "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
            "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM", "3:00 PM",
            "3:30 PM", "4:00 PM", "4:30 PM"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, times)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner.adapter = adapter
    }

    private fun bookAppointment(appointmentRequest: BookAppointmentRequest, token: String) {
        // Ensure the Bearer prefix is included
        val call = apiService.bookAppointment("Bearer $token", appointmentRequest)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Appointment booked successfully", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Appointment booked successfully")
                    findNavController().navigate(R.id.action_nav_book_app_patient_to_nav_app)
                } else {
                    // Logging and error handling
                    val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "Failed to book appointment: HTTP ${response.code()} - $errorResponse")
                    Toast.makeText(requireContext(), "Failed to book appointment: $errorResponse", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Network error while booking appointment: ${t.message}", t)
                Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
