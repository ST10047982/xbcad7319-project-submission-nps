package com.app.xbcad7319_physiotherapyapp.ui.forget_password_staff

import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.xbcad7319_physiotherapyapp.R
import com.app.xbcad7319_physiotherapyapp.databinding.FragmentForgetPasswordStaffBinding
import com.app.xbcad7319_physiotherapyapp.ui.ApiService
import com.app.xbcad7319_physiotherapyapp.ui.PasswordUpdateRequest // Ensure this import matches your project structure
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordStaffFragment : Fragment() {

    private var _binding: FragmentForgetPasswordStaffBinding? = null
    private val binding get() = _binding!!

    private var passwordVisible: Boolean = false
    private val TAG = "ForgetPasswordStaffFragment"

    // Create an instance of ApiService
    private val apiService: ApiService by lazy {
        com.app.xbcad7319_physiotherapyapp.ui.ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetPasswordStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            updatePassword()
        }

        binding.btnCancel.setOnClickListener {
            clearFields()
        }

        // Handle password visibility toggle click
        binding.iconViewPassword.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun updatePassword() {
        val username = binding.etxtUsername.text.toString().trim()
        val newPassword = binding.etxtNewPassword.text.toString().trim()

        // Validate inputs
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a PasswordUpdateRequest object
        val passwordUpdateRequest = PasswordUpdateRequest(
            username = username,
            newPassword = newPassword
        )

        // Call API to update password
        updatePasswordInApi(passwordUpdateRequest)
    }
    // the code above was taken and adapted from Firebase.
    // https://firebase.google.com/docs/auth/android/manage-users

    private fun updatePasswordInApi(passwordUpdateRequest: PasswordUpdateRequest) {
        val call = apiService.updatePassword(passwordUpdateRequest)

        Log.d(TAG, "Sending password update request for username: ${passwordUpdateRequest.username}")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                    clearFields() // Clear fields after successful update

                    findNavController().navigate(R.id.action_nav_forget_password_staff_to_nav_login_staff)

                } else {
                    val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "Failed to update password: HTTP ${response.code()} - $errorResponse")
                    Toast.makeText(requireContext(), "Failed to update password: $errorResponse", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Error: ${t.message}", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    // the code above was taken and adapted from Firebase.
    // https://firebase.google.com/docs/auth/android/manage-users

    private fun clearFields() {
        binding.etxtUsername.text.clear()
        binding.etxtNewPassword.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible

        if (passwordVisible) {
            binding.etxtNewPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.iconViewPassword.setImageResource(R.drawable.visible_icon) // Update to your visible icon
        } else {
            binding.etxtNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.iconViewPassword.setImageResource(R.drawable.visible_icon) // Update to your hidden icon
        }
        binding.etxtNewPassword.setSelection(binding.etxtNewPassword.text.length)
    }
    // the code above was taken and adapted from Android Developers
    // https://developer.android.com/quick-guides/content/show-hide-password
}
