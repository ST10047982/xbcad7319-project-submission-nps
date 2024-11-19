package com.app.xbcad7319_physiotherapyapp.ui.cancel_app_patient

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
import com.app.xbcad7319_physiotherapyapp.R
import com.app.xbcad7319_physiotherapyapp.ui.ApiService
import com.app.xbcad7319_physiotherapyapp.ui.AppointmentDetails
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelAppPatientFragment : Fragment() {

    private lateinit var listAppointments: ListView
    private lateinit var btnCancelAppointment: Button
    private lateinit var ibtnHome: ImageButton
    private lateinit var sharedPref: SharedPreferences
    private val apiService: ApiService by lazy {
        com.app.xbcad7319_physiotherapyapp.ui.ApiClient.getRetrofitInstance(requireContext())
            .create(ApiService::class.java)
    }
    private var appointmentsList = mutableListOf<AppointmentDetails>()
    private var selectedAppointment: AppointmentDetails? = null
    private val TAG = "CancelAppPatientFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cancel_app_patient, container, false)

        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        initializeUIComponents(view)
        fetchAndDisplayAppointments()

        return view
    }

    private fun initializeUIComponents(view: View) {
        listAppointments = view.findViewById(R.id.listAppointments)
        btnCancelAppointment = view.findViewById(R.id.btnCancel)
        ibtnHome = view.findViewById(R.id.ibtnHome)

        btnCancelAppointment.setOnClickListener {
            selectedAppointment?.let {
                cancelAppointment(it.id)
            } ?: showToast("Please select an appointment to cancel.")
        }

        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_cancel_app_patient_to_nav_app)
        }
    }

    private fun fetchAndDisplayAppointments() {
        val token = sharedPref.getString("bearerToken", null)

        if (token == null) {
            showToast("User not logged in.")
            return
        }

        apiService.getAllConfirmedAppointments("Bearer $token").enqueue(object : Callback<List<AppointmentDetails>> {
            override fun onResponse(call: Call<List<AppointmentDetails>>, response: Response<List<AppointmentDetails>>) {
                if (response.isSuccessful) {
                    response.body()?.let { appointments ->
                        appointmentsList.clear()
                        appointmentsList.addAll(appointments.filter {
                            it.status == "approved" || it.status == "rescheduled"
                        })
                        populateListView()
                    } ?: showToast("No appointments available.")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "Failed to load appointments: $errorMessage")
                    showToast("$errorMessage")
                }

            }

            override fun onFailure(call: Call<List<AppointmentDetails>>, t: Throwable) {
                Log.e(TAG, "Network error: ${t.message}", t)
                showToast("Network error.")
            }
        })
    }

    private fun populateListView() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            appointmentsList.map {
                "Date: ${it.date.split("T")[0]}\nTime: ${it.time}\nDescription: ${it.description}"
            }
        )
        listAppointments.adapter = adapter

        listAppointments.setOnItemClickListener { _, _, position, _ ->
            selectedAppointment = appointmentsList[position]
        }
    }

    private fun cancelAppointment(appointmentId: String) {
        val token = sharedPref.getString("bearerToken", null)

        if (token == null) {
            showToast("User not logged in.")
            return
        }

        apiService.cancelAppointment("Bearer $token", appointmentId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    showToast("Appointment canceled successfully.")
                    removeAppointmentFromList(appointmentId)
                } else {
                    Log.e(TAG, "Failed to cancel appointment: ${response.errorBody()?.string()}")
                    showToast("Failed to cancel appointment.")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Network error while canceling appointment: ${t.message}", t)
                showToast("An error occurred: ${t.message}")
            }
        })
    }

    private fun removeAppointmentFromList(appointmentId: String) {
        appointmentsList = appointmentsList.filter { it.id != appointmentId }.toMutableList()
        populateListView()
        selectedAppointment = null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
