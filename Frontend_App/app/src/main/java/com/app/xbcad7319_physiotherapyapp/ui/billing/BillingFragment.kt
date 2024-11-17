package com.app.xbcad7319_physiotherapyapp.ui.billing

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.xbcad7319_physiotherapyapp.R
import com.app.xbcad7319_physiotherapyapp.ui.ApiService
import com.app.xbcad7319_physiotherapyapp.ui.BillingRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BillingFragment : Fragment() {

    private lateinit var patientNameEditText: EditText
    private lateinit var costEditText: EditText
    private lateinit var btnSendBill: Button
    private lateinit var btnCancel: Button
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_billing, container, false)

        // Initialize views
        patientNameEditText = view.findViewById(R.id.etxtName)
        costEditText = view.findViewById(R.id.etxtCost)
        btnSendBill = view.findViewById(R.id.btnSendBill)
        btnCancel = view.findViewById(R.id.btnCancel)

        // Initialize apiService
        apiService = com.app.xbcad7319_physiotherapyapp.ui.ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)

        // Send Bill button functionality
        btnSendBill.setOnClickListener {
            Log.d("BillingFragment", "Send Bill button clicked")
            submitBillingData()
        }

        // Cancel button functionality
        btnCancel.setOnClickListener {
            Log.d("BillingFragment", "Cancel button clicked")
            clearAllFields() // Clear all fields when cancel is clicked
        }

        // Back button functionality
        val ibtnBack: ImageButton = view.findViewById(R.id.ibtnHome)
        ibtnBack.setOnClickListener {
            Log.d("BillingFragment", "Home button clicked")
            findNavController().navigate(R.id.action_nav_billing_to_nav_home_staff) // Adjust navigation if needed
        }

        return view
    }

    // Clear all fields
    private fun clearAllFields() {
        Log.d("BillingFragment", "Clearing all fields")
        patientNameEditText.text.clear() // Clear the patient name field
        costEditText.text.clear() // Clear the cost field
        Toast.makeText(context, "All fields cleared", Toast.LENGTH_SHORT).show() // Optional feedback
    }

    // Submit billing data to backend
    private fun submitBillingData() {
        val patientName = patientNameEditText.text.toString().trim()
        val cost = costEditText.text.toString().trim()

        // Validate input
        if (patientName.isEmpty() || cost.isEmpty()) {
            Log.e("BillingFragment", "Validation failed: incomplete form data")
            Toast.makeText(context, "Please complete all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create BillingRequest object
        val billingRequest = BillingRequest(
            patientName = patientName,
            cost = cost.toDouble() // Assuming cost is entered as a number
        )

        Log.d("BillingFragment", "Submitting BillingRequest: $billingRequest")

        // Call API
        apiService.submitBillingData(billingRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("BillingFragment", "Bill submitted successfully")
                    Toast.makeText(context, "Bill submitted successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_nav_billing_to_nav_home_staff) // Adjust navigation as needed
                } else {
                    Log.e("BillingFragment", "Bill submission failed: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to submit bill", Toast.LENGTH_SHORT).show()
                }
            }



            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("BillingFragment", "Bill submission error: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
