package com.app.xbcad7319_physiotherapyapp.ui.login_patient

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.xbcad7319_physiotherapyapp.R
import com.app.xbcad7319_physiotherapyapp.databinding.FragmentLoginPatientBinding
import com.app.xbcad7319_physiotherapyapp.ui.ApiService
import com.app.xbcad7319_physiotherapyapp.ui.LoginRequest
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPatientFragment : Fragment() {

    private var _binding: FragmentLoginPatientBinding? = null
    private val binding get() = _binding!!

    private var passwordVisible: Boolean = false
    private lateinit var sharedPref: SharedPreferences
    private val TAG = "LoginPatientFragment"

    private val apiService: ApiService by lazy {
        com.app.xbcad7319_physiotherapyapp.ui.ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toggle password visibility when the icon is clicked
        binding.iconViewPassword.setOnClickListener { togglePasswordVisibility() }

        // Handle login button click
        binding.btnLogin.setOnClickListener { loginUser() }

        // Handle forgot password link click
        binding.txtForgotPassword.setOnClickListener { onForgotPasswordClicked() }
    }

    private fun loginUser() {
        val username = binding.etxtUsername.text.toString().trim()
        val password = binding.etxtPassword.text.toString().trim()

        // Validate input fields
        if (username.isBlank()) {
            showToast("Username cannot be empty")
            return
        }
        if (password.isBlank()) {
            showToast("Password cannot be empty")
            return
        }

        // Prepare the login request
        val loginRequest = LoginRequest(username, password)

        // Call the API to log in the user
        loginUserToApi(loginRequest, username)
    }


    private fun loginUserToApi(loginRequest: LoginRequest, username: String) {
        val call = apiService.loginPatient(loginRequest)  // Corrected to use patient login API

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        handleLoginResponse(responseBody, username)
                    } else {
                        showToast("Invalid response from server.")
                    }
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showToast("An error occurred. Please check your connection.")
            }
        })
    }

    private fun logoutUser() {
        val tokenResponse = sharedPref.getString("bearerToken", null)

        if (tokenResponse.isNullOrEmpty()) {
            showToast("You are not logged in.")
            findNavController().navigate(R.id.action_nav_home_patient_to_nav_main_menu)
            return
        }

        apiService.logoutUser("Bearer $tokenResponse").enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showToast("Logged out successfully.")
                   // clearSession()
                    findNavController().navigate(R.id.action_nav_home_patient_to_nav_main_menu)
                } else {
                    showToast("Logout failed. Please try again later.")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("An error occurred during logout.")
            }
        })
    }

    private fun handleLoginResponse(responseBody: String, username: String) {
        try {
            val jsonResponse = JSONObject(responseBody)
            val token = jsonResponse.optString("token", null)
            val role = jsonResponse.optString("role", null)
            val userId = jsonResponse.optString("userId", null)

            if (token.isNullOrEmpty() || role.isNullOrEmpty() || userId.isNullOrEmpty()) {
                showToast("Invalid response from server.")
                return
            }

            if (role != "patient") {
                showToast("You are not authorized to access this app.")
                return
            }

            sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("bearerToken", token)
                putString("loggedInUsername", username)
                putString("userId", userId)
                apply()
            }

            Log.d(TAG, "Login successful. Token saved: $token")

            showToast("Login successful!")
            clearFields()

            findNavController().navigate(R.id.action_nav_login_patient_to_nav_home_patient)
        } catch (e: JSONException) {
            showToast("Failed to process login response. Please try again.")
        }
    }

    private fun handleErrorResponse(response: Response<ResponseBody>) {
        val errorResponse = response.errorBody()?.string() ?: "Unknown error"
        showToast("Login failed: $errorResponse")
    }

    private fun onForgotPasswordClicked() {
        findNavController().navigate(R.id.action_nav_login_patient_to_nav_forget_password_patient)
    }

    // Toggle password visibility
    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
        binding.etxtPassword.inputType = if (passwordVisible) {
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        // Change the password visibility icon based on the current state
        binding.iconViewPassword.setImageResource(
            if (passwordVisible) R.drawable.visible_icon else R.drawable.visible_icon
        )

        // Move the cursor to the end of the password field after toggling visibility
        binding.etxtPassword.setSelection(binding.etxtPassword.text.length)
    }

    private fun clearFields() {
        binding.etxtUsername.text.clear()
        binding.etxtPassword.text.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
