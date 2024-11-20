package com.xbcad.xbcad7319_physiotherapyapp.ui.patient_profile_staff

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

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiService
import com.xbcad.xbcad7319_physiotherapyapp.ui.ProfileData
import com.xbcad.xbcad7319_physiotherapyapp.ui.UserIdResponse

class PatientProfileStaffFragment : Fragment() {

    private lateinit var listView: ListView
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
        ibtnHome = view.findViewById(R.id.ibtnHome)
        txtSelectedPatient = view.findViewById(R.id.txtSelectedPatient)

        // Initialize shared preferences
        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        // Initialize API service
        apiService = ApiClient.getRetrofitInstance(requireContext()).create(
            ApiService::class.java)

        // Set up Home button click listener
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_view_patient_profile_to_nav_home_staff)
        }

        // Load patient names
        loadPatientNames()


    }

    private fun loadPatientNames() {
        val token = sharedPref.getString("bearerToken", null)?.let { "Bearer $it" }
        if (token.isNullOrEmpty()) {
            showToast("User is not logged in. Please log in again.")
            return
        }
        val call = apiService.getPatientNames(token)
        Log.d("PatientNamesFragment", "API call initiated to fetch patient names")

        call.enqueue(object : Callback<ApiService.PatientNamesResponse> {
            override fun onResponse(call: Call<ApiService.PatientNamesResponse>, response: Response<ApiService.PatientNamesResponse>) {
                if (response.isSuccessful) {
                    val patientNames = response.body()?.patientNames
                    if (patientNames != null) {
                        Log.d("PatientNamesFragment", "Patient names fetched successfully: $patientNames")

                        // Set up the ListView with the fetched patient names
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            patientNames
                        )
                        listView.adapter = adapter

                        listView.setOnItemClickListener { _, _, position, _ ->
                            selectedPatientName = patientNames[position]
                            txtSelectedPatient.text = "Selected Patient: $selectedPatientName"
                            // Assuming the name and surname are separated by a space
                            val (name, surname) = selectedPatientName!!.split(" ", limit = 2)
                            getUserIdByNameAndSurname(name, surname)

                        }
                    } else {
                        Log.w("PatientNamesFragment", "Response body is null. No patients found.")
                        Toast.makeText(requireContext(), "No patients found.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PatientNamesFragment", "Failed to load patients. Code: ${response.code()}, Error: $errorBody")
                    Toast.makeText(requireContext(), "Failed to load patients.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.PatientNamesResponse>, t: Throwable) {
                Log.e("PatientNamesFragment", "API call failed with error: ${t.message}", t)
                Toast.makeText(requireContext(), "Error loading patients: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getPatientProfile(patientID: String) {
        if (patientID.isEmpty()) {
            showToast("Patient ID is required to fetch the profile.")
            return
        }

        apiService.getPatientProfileById(patientID).enqueue(object : Callback<ProfileData> {
            override fun onResponse(call: Call<ProfileData>, response: Response<ProfileData>) {
                if (response.isSuccessful) {
                    response.body()?.let { patientProfile ->
                        // Update the UI with the patient's full profile
                        displayPatientProfile(patientProfile)
                    } ?: run {
                        Log.w("PatientProfile", "Profile data is null.")
                        showToast("Failed to load patient profile.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(
                        "PatientProfile",
                        "Failed to load patient profile. Code: ${response.code()}, Error: $errorBody"
                    )
                    showToast("Failed to load patient profile. Error: ${errorBody ?: "Unknown error"}")
                }
            }

            override fun onFailure(call: Call<ProfileData>, t: Throwable) {
                Log.e("PatientProfile", "API call failed: ${t.message}", t)
                showToast("Error loading patient profile: ${t.message}")
            }
        })
    }



    private fun displayPatientProfile(profile: ProfileData) {
        // Concatenate the patient details into a single string to display in one TextView
        val profileDetails = """
            Email: ${profile.email}
            Phone Number: ${profile.phoneNumber}
            Medical Aid: ${profile.medicalAid}
            Medical Aid Number: ${profile.medicalAidNumber}
        
        """.trimIndent()

        // Find the TextView and set the text to display all profile details
        val txtPatientDetails = view?.findViewById<TextView>(R.id.txtPatientDetails)
        txtPatientDetails?.text = profileDetails
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getUserIdByNameAndSurname(name: String, surname: String) {
        if (name.isEmpty() || surname.isEmpty()) {
            showToast("Name and surname are required.")
            return
        }

        apiService.getUserIdByNameAndSurname(name, surname).enqueue(object : Callback<UserIdResponse> {
            override fun onResponse(call: Call<UserIdResponse>, response: Response<UserIdResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { userIdResponse ->
                        val userId = userIdResponse.userId
                        Log.d("UserSearch", "Found user ID: $userId")

                        getPatientProfile(userId);
                    } ?: run {
                        Log.w("UserSearch", "User ID not found in response.")
                        showToast("Failed to find user ID.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UserSearch", "Failed to fetch user ID. Error: $errorBody")
                    showToast("Failed to fetch user ID.")
                }
            }

            override fun onFailure(call: Call<UserIdResponse>, t: Throwable) {
                Log.e("UserSearch", "API call failed: ${t.message}", t)
                showToast("Error finding user ID: ${t.message}")
            }
        })
    }

}
