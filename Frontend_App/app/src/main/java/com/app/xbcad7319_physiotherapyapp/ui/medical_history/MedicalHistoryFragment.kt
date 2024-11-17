package com.app.xbcad7319_physiotherapyapp.ui.medical_history

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
import com.app.xbcad7319_physiotherapyapp.ui.MedicalHistory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicalHistoryFragment : Fragment() {

    private lateinit var etxtAllergies: EditText
    private lateinit var etxtInjuries: EditText
    private lateinit var etxtProcedures: EditText
    private lateinit var etxtMedications: EditText
    private lateinit var etxtFamilyHistory: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var sharedPref: SharedPreferences

    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_medical_history, container, false)

        initializeViews(view)
        setupClickListeners()

        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        loadMedicalHistory()

        return view
    }

    private fun initializeViews(view: View) {
        etxtAllergies = view.findViewById(R.id.etxtAllergies)
        etxtInjuries = view.findViewById(R.id.etxtInjuries)
        etxtProcedures = view.findViewById(R.id.etxtProcedures)
        etxtMedications = view.findViewById(R.id.etxtMedications)
        etxtFamilyHistory = view.findViewById(R.id.extxFamily_History)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        ibtnHome.setOnClickListener {
            findNavController().navigate(R.id.action_nav_medical_history_to_home_patient)
        }
    }

    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            if (validateInputs()) {
                saveMedicalHistory()
            }
        }

        btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        fun validateField(editText: EditText, message: String): Boolean {
            return if (editText.text.toString().trim().isEmpty()) {
                editText.error = message
                false
            } else {
                true
            }
        }

        isValid = isValid and validateField(etxtAllergies, "Please enter allergies or write 'None'")
        isValid = isValid and validateField(etxtInjuries, "Please enter injuries or write 'None'")
        isValid = isValid and validateField(etxtProcedures, "Please enter procedures or write 'None'")
        isValid = isValid and validateField(etxtMedications, "Please enter medications or write 'None'")
        isValid = isValid and validateField(etxtFamilyHistory, "Please enter family history or write 'None'")

        return isValid
    }

    private fun loadMedicalHistory() {
        // Retrieve the token from SharedPreferences
        val token = sharedPref.getString("bearerToken", null)?.let { "Bearer $it" }

        if (token.isNullOrEmpty()) {
            Log.e("MedicalHistoryFragment", "Token is null or empty. User might not be logged in.")
            showToast("User is not logged in. Please log in again.")
            return
        }

        Log.d("MedicalHistoryFragment", "Retrieved token: $token")

        val call = apiService.getMedicalHistory(token)
        Log.d("MedicalHistoryFragment", "API call initiated to fetch medical history")

        call.enqueue(object : Callback<MedicalHistory> {
            override fun onResponse(call: Call<MedicalHistory>, response: Response<MedicalHistory>) {
                if (response.isSuccessful) {
                    val medicalHistory = response.body()
                    if (medicalHistory != null) {
                        Log.d("MedicalHistoryFragment", "Medical history fetched successfully: $medicalHistory")
                        updateUI(medicalHistory)
                    } else {
                        Log.w("MedicalHistoryFragment", "Response body is null. No medical history found.")
                        showToast("No medical history found.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(
                        "MedicalHistoryFragment",
                        "Failed to load medical history. Code: ${response.code()}, Error: $errorBody"
                    )
                    showToast("Failed to load medical history. Code: ${response.code()}, Error: ${errorBody ?: "Unknown error"}")
                }
            }

            override fun onFailure(call: Call<MedicalHistory>, t: Throwable) {
                Log.e("MedicalHistoryFragment", "API call failed with error: ${t.message}", t)
                showToast("Error: Unable to connect to server. ${t.message}")
            }
        })
    }



    private fun saveMedicalHistory() {
        val medicalHistory = MedicalHistory(
            allergies = etxtAllergies.text.toString().trim(),
            injuries = etxtInjuries.text.toString().trim(),
            procedures = etxtProcedures.text.toString().trim(),
            medications = etxtMedications.text.toString().trim(),
            familyHistory = etxtFamilyHistory.text.toString().trim()
        )

        val token = sharedPref.getString("bearerToken", null)?.let { "Bearer $it" }

        if (token.isNullOrEmpty()) {
            showToast("User not logged in")
            return
        }

        val call = apiService.saveMedicalHistory(token, medicalHistory)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showToast("Medical history saved successfully")
                    findNavController().navigateUp()
                } else {
                    showToast("Failed to save medical history")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun updateUI(medicalHistory: MedicalHistory) {
        etxtAllergies.setText(medicalHistory.allergies)
        etxtInjuries.setText(medicalHistory.injuries)
        etxtProcedures.setText(medicalHistory.procedures)
        etxtMedications.setText(medicalHistory.medications)
        etxtFamilyHistory.setText(medicalHistory.familyHistory)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
