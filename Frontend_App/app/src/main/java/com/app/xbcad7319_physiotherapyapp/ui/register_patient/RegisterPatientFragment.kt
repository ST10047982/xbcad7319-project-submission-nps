package com.app.xbcad7319_physiotherapyapp.ui.register_patient

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.xbcad7319_physiotherapyapp.R
import com.app.xbcad7319_physiotherapyapp.ui.ApiService
import com.app.xbcad7319_physiotherapyapp.ui.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import androidx.navigation.fragment.findNavController
import java.io.IOException

class RegisterPatientFragment : Fragment() {

    private lateinit var etxtName: EditText
    private lateinit var etxtSurname: EditText
    private lateinit var etxtMedicalName: EditText
    private lateinit var etxtMedicalNumber: EditText
    private lateinit var etxtPhoneNumber: EditText
    private lateinit var etxtEmail: EditText
    private lateinit var etxtUsername: EditText
    private lateinit var etxtPassword: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private val TAG = "RegisterPatientFragment"
    private var passwordVisible: Boolean = false

    // Create an instance of ApiService
    private val apiService: ApiService by lazy {
        com.app.xbcad7319_physiotherapyapp.ui.ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register_patient, container, false)

        // Initialize views
        etxtName = view.findViewById(R.id.etxtName)
        etxtSurname = view.findViewById(R.id.etxtSurame)
        etxtMedicalName = view.findViewById(R.id.etxtMedical_Name)
        etxtMedicalNumber = view.findViewById(R.id.etxtMedical_Aid_Number)
        etxtPhoneNumber = view.findViewById(R.id.etxtPhone_Number)
        etxtEmail = view.findViewById(R.id.etxtEmail)
        etxtUsername = view.findViewById(R.id.etxtUsername)
        etxtPassword = view.findViewById(R.id.etxtPassword)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        // Handle Save button click
        btnSave.setOnClickListener {
            registerPatient()
        }

        // Handle Cancel button click
        btnCancel.setOnClickListener {
            clearFields()
        }

        // Handle password visibility toggle
        val iconViewPassword: View = view.findViewById(R.id.iconViewPassword)
        iconViewPassword.setOnClickListener {
            togglePasswordVisibility()
        }

        return view
    }

    private fun registerPatient() {
        val name = etxtName.text.toString().trim()
        val surname = etxtSurname.text.toString().trim()
        val medicalName = etxtMedicalName.text.toString().trim()
        val medicalNumber = etxtMedicalNumber.text.toString().trim()
        val phoneNumber = etxtPhoneNumber.text.toString().trim()
        val email = etxtEmail.text.toString().trim()
        val username = etxtUsername.text.toString().trim()
        val password = etxtPassword.text.toString().trim()

        // Validate inputs
        if (name.isEmpty() || surname.isEmpty() || phoneNumber.isEmpty() ||
            email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill out all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new User object
        val newUser = User(
            username = username,
            password = password,
            role = "patient", // Default role
            name = name,
            surname = surname,
            email = email,
            phoneNumber = phoneNumber,
            medicalAid = medicalName.takeIf { it.isNotEmpty() },
            medicalAidNumber = medicalNumber.takeIf { it.isNotEmpty() }
        )

        // Call API to save user object
        savePatientToDatabase(newUser)
    }

    private fun savePatientToDatabase(user: User) {
        // Call API to register the user
        val call = apiService.registerPatient(user)


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Successfully registered
                    val token = response.body()?.string() // Assuming the token is in the response body as a string

                    // Store the token in SharedPreferences
                    val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString("bearerToken", token)
                    editor.apply()

                    Toast.makeText(context, "Patient registered successfully!", Toast.LENGTH_SHORT).show()
                    clearFields()

                    findNavController().navigate(R.id.action_nav_register_patient_to_nav_login_patient)
                } else {
                    val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "Registration failed: HTTP ${response.code()} - $errorResponse")
                    Toast.makeText(context, "Registration failed: $errorResponse", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Error: ${t.message}", t)
                if (t is IOException) {
                    Toast.makeText(context, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "An unexpected error occurred: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible

        if (passwordVisible) {
            etxtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            etxtPassword.setSelection(etxtPassword.text.length)
        } else {
            etxtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            etxtPassword.setSelection(etxtPassword.text.length)
        }
    }

    private fun clearFields() {
        etxtName.text.clear()
        etxtSurname.text.clear()
        etxtMedicalName.text.clear()
        etxtMedicalNumber.text.clear()
        etxtPhoneNumber.text.clear()
        etxtEmail.text.clear()
        etxtUsername.text.clear()
        etxtPassword.text.clear()
    }
}
