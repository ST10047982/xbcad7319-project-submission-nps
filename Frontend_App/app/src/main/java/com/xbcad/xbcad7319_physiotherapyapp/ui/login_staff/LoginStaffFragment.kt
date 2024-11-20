package com.xbcad.xbcad7319_physiotherapyapp.ui.login_staff

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
import com.xbcad.xbcad7319_physiotherapyapp.R
import com.xbcad.xbcad7319_physiotherapyapp.databinding.FragmentLoginStaffBinding
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiClient

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiService
import com.xbcad.xbcad7319_physiotherapyapp.ui.LoginRequest

class LoginStaffFragment : Fragment() {

    private var _binding: FragmentLoginStaffBinding? = null
    private val binding get() = _binding!!

    private var passwordVisible: Boolean = false  // For password visibility toggle
    private lateinit var sharedPref: SharedPreferences
    private val TAG = "LoginStaffFragment"

    // Lazy initialization of ApiService
    private val apiService: ApiService by lazy {
        ApiClient.getRetrofitInstance(requireContext()).create(
            ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconViewPassword.setOnClickListener { togglePasswordVisibility() }
        binding.btnLogin.setOnClickListener { loginUser() }
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


    private fun handleLoginResponse(responseBody: String, username: String) {
        val jsonResponse = JSONObject(responseBody)
        val token = jsonResponse.getString("token")
        val role = jsonResponse.getString("role")

        if (role != "staff") {
            showToast("Login failed: You are not authorized to access this app.")
            return
        }

        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("bearerToken", token)
            putString("loggedInUsername", username)
            apply()
        }

        Log.d(TAG, "Login successful: Token=$token")
        showToast("Login successful!")
        clearFields()

        findNavController().navigate(R.id.action_nav_login_staff_to_nav_home_staff)
    }

    private fun handleErrorResponse(response: Response<ResponseBody>) {
        val errorResponse = response.errorBody()?.string() ?: "Unknown error"
        Log.e(TAG, "Login failed: HTTP ${response.code()} - $errorResponse")
        showToast("Login failed: $errorResponse")
    }

    private fun onForgotPasswordClicked() {
        findNavController().navigate(R.id.action_nav_login_staff_to_nav_forget_password_staff)
    }

    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
        binding.etxtPassword.inputType = if (passwordVisible) {
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        binding.iconViewPassword.setImageResource(
            if (passwordVisible) R.drawable.visible_icon else R.drawable.visible_icon
        )
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
