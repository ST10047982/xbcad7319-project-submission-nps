package com.xbcad.xbcad7319_physiotherapyapp.ui.view_app_notes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xbcad.xbcad7319_physiotherapyapp.R
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiClient
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiService
import com.xbcad.xbcad7319_physiotherapyapp.ui.AppointmentDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewAppNotesFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var ibtnHome: ImageButton
    private lateinit var txtSelectedPatient: TextView
    private lateinit var txtNotes: TextView // Add this line
    private lateinit var sharedPref: SharedPreferences
    private lateinit var apiService: ApiService
    private var selectedAppointmentId: String? = null
    private lateinit var appointments: List<AppointmentDetails>  // To store the list of appointments

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_app_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        listView = view.findViewById(R.id.listAppointments)
        ibtnHome = view.findViewById(R.id.ibtnHome)
        txtSelectedPatient = view.findViewById(R.id.txtSelectedPatient)
        txtNotes = view.findViewById(R.id.txtNotes) // Initialize txtNotes

        // Initialize shared preferences
        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        // Initialize API service
        apiService = ApiClient.getRetrofitInstance(requireContext()).create(
            ApiService::class.java)

        // Set up Home button click listener
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_view_app_notes_to_nav_home_staff) // Assuming you have this action
        }

        // Load all appointments
        loadAppointments()
    }

    private fun loadAppointments() {
        val token = sharedPref.getString("bearerToken", null)?.let { "Bearer $it" }
        if (token.isNullOrEmpty()) {
            showToast("User is not logged in. Please log in again.")
            return
        }

        val call = apiService.getAllAppointments(token)
        Log.d("ViewAppNotesFragment", "API call initiated to fetch appointments")

        call.enqueue(object : Callback<List<AppointmentDetails>> {
            override fun onResponse(call: Call<List<AppointmentDetails>>, response: Response<List<AppointmentDetails>>) {
                if (response.isSuccessful) {
                    appointments = response.body() ?: emptyList()

                    if (appointments.isNotEmpty()) {
                        val appointmentInfo = appointments.map { appointment ->
                            val patientFullName = "${appointment.patient.name} ${appointment.patient.surname}"
                            val appointmentDate = appointment.date
                            val appointmentTime = appointment.time
                            val description = appointment.description ?: "No Description"
                            "Patient: $patientFullName\nDate: $appointmentDate\nTime: $appointmentTime\nDescription: $description"
                        }

                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            appointmentInfo
                        )

                        listView.adapter = adapter

                        listView.setOnItemClickListener { _, _, position, _ ->
                            val selectedAppointment = appointments[position]
                            selectedAppointmentId = selectedAppointment.id
                            txtSelectedPatient.text = "Selected Appointment: ${appointmentInfo[position]}"
                            loadAppointmentNotes(selectedAppointmentId!!)
                        }
                    } else {
                        showToast("No appointments found.")
                    }
                } else {
                    showToast("Failed to load appointments.")
                }
            }

            override fun onFailure(call: Call<List<AppointmentDetails>>, t: Throwable) {
                showToast("Error loading appointments: ${t.message}")
            }
        })
    }

    private fun loadAppointmentNotes(appointmentId: String) {
        val token = sharedPref.getString("bearerToken", null)?.let { "Bearer $it" }
        if (token.isNullOrEmpty()) {
            showToast("User is not logged in. Please log in again.")
            return
        }

        val call = apiService.getAppointmentNotes(token, appointmentId)
        Log.d("ViewAppNotesFragment", "API call initiated to fetch appointment notes for ID: $appointmentId")

        call.enqueue(object : Callback<ApiService.AppointmentNotesResponse> {
            override fun onResponse(
                call: Call<ApiService.AppointmentNotesResponse>,
                response: Response<ApiService.AppointmentNotesResponse>
            ) {
                if (response.isSuccessful) {
                    val appointmentNotes = response.body()?.notes

                    if (appointmentNotes != null && appointmentNotes.isNotEmpty()) {
                        val notesText = appointmentNotes.joinToString(separator = "\n\n") { note ->
                            "${note ?: "No content"}" // Handle null notes
                        }
                        txtNotes.text = notesText
                    } else {
                        txtNotes.text = "No notes found for this appointment."
                    }
                } else {
                    txtNotes.text = "Failed to load notes."
                }
            }

            override fun onFailure(call: Call<ApiService.AppointmentNotesResponse>, t: Throwable) {
                txtNotes.text = "Error loading notes: ${t.message}"
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
