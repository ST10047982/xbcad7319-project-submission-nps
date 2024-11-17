package com.app.xbcad7319_physiotherapyapp.ui.profile_patient

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.xbcad7319_physiotherapyapp.R
import com.app.xbcad7319_physiotherapyapp.ui.ApiClient
import com.app.xbcad7319_physiotherapyapp.ui.ApiService
import com.app.xbcad7319_physiotherapyapp.ui.ProfileData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePatientFragment : Fragment() {

    private lateinit var etxtEmail: EditText
    private lateinit var etxtPhoneNumber: EditText
    private lateinit var etxtMedicalAid: EditText
    private lateinit var etxtMedicalAidNumber: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var ibtnHome: ImageButton

    private lateinit var sharedPref: SharedPreferences

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_patient, container, false)

        initializeViews(view)
        setupListeners()

        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        loadPatientProfile()

        return view
    }

    private fun initializeViews(view: View) {
        etxtEmail = view.findViewById(R.id.etxtEmail)
        etxtPhoneNumber = view.findViewById(R.id.etxtPhone_Number)
        etxtMedicalAid = view.findViewById(R.id.etxtMedical_Aid)
        etxtMedicalAidNumber = view.findViewById(R.id.etxtMedical_Aid_Number)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
        ibtnHome = view.findViewById(R.id.ibtnHome)
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            saveProfile()
        }

        btnCancel.setOnClickListener {
            requireActivity().onBackPressed() // Go back to the previous screen
        }

        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_patient_profile_to_home_patient)
        }
    }

    private fun loadPatientProfile() {
        val token = sharedPref.getString("bearerToken", "") ?: ""
        if (token.isEmpty()) {
            showToast("Not authenticated. Please login again.")
            findNavController().navigate(R.id.action_nav_patient_profile_to_login_patient)
            return
        }

        // Add Bearer prefix to token
        val authToken = "Bearer $token"

        apiService.getPatientProfile(authToken).enqueue(object : Callback<ProfileData> {
            override fun onResponse(call: Call<ProfileData>, response: Response<ProfileData>) {
                if (response.isSuccessful) {
                    response.body()?.let { profile ->
                        // Populate the EditText fields with the user's profile data
                        etxtEmail.setText(profile.email)
                        etxtPhoneNumber.setText(profile.phoneNumber)
                        etxtMedicalAid.setText(profile.medicalAid)
                        etxtMedicalAidNumber.setText(profile.medicalAidNumber)
                    } ?: run {
                        showToast("Profile data is empty or null")
                    }
                } else {
                    showToast("Failed to load profile: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ProfileData>, t: Throwable) {
                showToast("Error loading profile: ${t.message}")
            }
        })
    }

    private fun saveProfile() {
        val token = sharedPref.getString("bearerToken", "") ?: ""
        if (token.isEmpty()) {
            showToast("Not authenticated. Please login again.")
            findNavController().navigate(R.id.action_nav_patient_profile_to_login_patient)
            return
        }

        val authToken = "Bearer $token"

        // Create ProfileData object from input fields
        val profileUpdate = ProfileData(
            email = etxtEmail.text.toString(),
            phoneNumber = etxtPhoneNumber.text.toString(),
            medicalAid = etxtMedicalAid.text.toString(),
            medicalAidNumber = etxtMedicalAidNumber.text.toString()
        )

        apiService.savePatientProfile(authToken, profileUpdate).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    // You can check the response body if necessary
                    val responseBody = response.body()
                    if (responseBody != null && responseBody["message"] != null) {
                        showToast(responseBody["message"].toString()) // Display success message
                        findNavController().popBackStack() // Go back to the previous screen
                    } else {
                        showToast("Failed to update profile: ${response.message()}")
                    }
                } else {
                    showToast("Failed to update profile: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                showToast("Error updating profile: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
