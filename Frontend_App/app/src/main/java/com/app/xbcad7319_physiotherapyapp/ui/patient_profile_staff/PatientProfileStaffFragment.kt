package com.app.xbcad7319_physiotherapyapp.ui.patient_profile_staff

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
import com.app.xbcad7319_physiotherapyapp.ui.ApiClient
import com.app.xbcad7319_physiotherapyapp.ui.ApiService
import com.app.xbcad7319_physiotherapyapp.ui.Patient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientProfileStaffFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var btnView: Button
    private lateinit var ibtnHome: ImageButton
    private lateinit var txtSelectedPatient: TextView
    private lateinit var sharedPref: SharedPreferences
    private lateinit var apiService: ApiService
    private var selectedPatientName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_patient_profile_staff, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        listView = view.findViewById(R.id.listProfiles)
        btnView = view.findViewById(R.id.btnView)
        ibtnHome = view.findViewById(R.id.ibtnHome)
        txtSelectedPatient = view.findViewById(R.id.txtSelectedPatient)

        // Initialize shared preferences
        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        // Initialize API service
        apiService = ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)

        // Set up Home button click listener
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_view_patient_profile_to_nav_home_staff)
        }

        // Load patient names
        loadPatientNames()

        // Button to view selected patient profile
        btnView.setOnClickListener {
            if (selectedPatientName != null) {
                Toast.makeText(requireContext(), "Selected: $selectedPatientName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please select a patient", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPatientNames() {
        val token = sharedPref.getString("bearerToken", "") ?: ""
        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "Not authenticated. Please login again.", Toast.LENGTH_SHORT).show()
            return
        }

        val authToken = "Bearer $token"

        apiService.getPatientNames(authToken).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    response.body()?.let { patientNames ->
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, patientNames)
                        listView.adapter = adapter

                        listView.setOnItemClickListener { _, _, position, _ ->
                            selectedPatientName = patientNames[position]
                            txtSelectedPatient.text = "Selected Patient: $selectedPatientName"
                        }
                    } ?: run {
                        Toast.makeText(requireContext(), "No patients found.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API_ERROR", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Failed to load patients.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e("API_ERROR", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error loading patients: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getPatientProfile(patientId: String) {
        val token = sharedPref.getString("bearerToken", "") ?: ""
        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "Not authenticated. Please login again.", Toast.LENGTH_SHORT).show()
            return
        }

        val authToken = "Bearer $token"

        apiService.getPatientProfileStaff(authToken, patientId).enqueue(object : Callback<Patient> {
            override fun onResponse(call: Call<Patient>, response: Response<Patient>) {
                if (response.isSuccessful) {
                    response.body()?.let { patientProfile ->
                        // Update the UI with the patient's full profile
                        displayPatientProfile(patientProfile)
                    } ?: run {
                        Toast.makeText(requireContext(), "Failed to load patient profile.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API_ERROR", "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Failed to load patient profile.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Patient>, t: Throwable) {
                Log.e("API_ERROR", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Error loading patient profile: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayPatientProfile(profile: Patient) {
        // Concatenate the patient details into a single string to display in one TextView
        val profileDetails = """
            Name: ${profile.name}
            Email: ${profile.email}
            Phone Number: ${profile.phoneNumber}
            Medical Aid: ${profile.medicalAid}
            Medical Aid Number: ${profile.medicalAidNumber}
            // Add more fields as needed based on your profile structure
        """.trimIndent()

        // Find the TextView and set the text to display all profile details
        val txtPatientDetails = view?.findViewById<TextView>(R.id.txtPatientDetails)
        txtPatientDetails?.text = profileDetails
    }
}
