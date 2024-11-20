package com.xbcad.xbcad7319_physiotherapyapp.ui.form2

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xbcad.xbcad7319_physiotherapyapp.R
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiClient

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.xbcad.xbcad7319_physiotherapyapp.ui.ApiService
import com.xbcad.xbcad7319_physiotherapyapp.ui.Form2Request
import com.xbcad.xbcad7319_physiotherapyapp.ui.SignatureView
import java.io.ByteArrayOutputStream
import java.util.*

object Form2State {
    var form2Filled: Boolean = false
}

class Form2Fragment : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var areaConsentedEditText: EditText
    private lateinit var signatureView: SignatureView
    private lateinit var calendarView: CalendarView
    private lateinit var btnClearSignature: Button
    private lateinit var btnCancel: Button
    private lateinit var selectedDate: Date

    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_form2, container, false)

        // Initialize views
        nameEditText = view.findViewById(R.id.etxtName)
        areaConsentedEditText = view.findViewById(R.id.etxtDryNeedeled)
        signatureView = view.findViewById(R.id.signature_view)
        calendarView = view.findViewById(R.id.calendarView2)
        btnClearSignature = view.findViewById(R.id.btnClearSignature)
        btnCancel = view.findViewById(R.id.btnCancel)

        // Initialize apiService
        apiService = ApiClient.getRetrofitInstance(requireContext()).create(
            ApiService::class.java)

        // Handle calendar date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
            Log.d("Form2Fragment", "Selected date: $selectedDate")
        }

        // Back button functionality
        val ibtnBack: ImageButton = view.findViewById(R.id.ibtnBack)
        ibtnBack.setOnClickListener {
            Log.d("Form2Fragment", "Back button clicked")
            findNavController().navigate(R.id.action_nav_form2_to_nav_intake_forms)
        }

        // Clear signature button functionality
        btnClearSignature.setOnClickListener {
            Log.d("Form2Fragment", "Clear signature button clicked")
            signatureView.clearSignature() // Clear the signature view
        }

        // Cancel button functionality
        btnCancel.setOnClickListener {
            Log.d("Form2Fragment", "Cancel button clicked")
            clearAllFields() // Clear all fields when cancel is clicked
        }

        // Save button functionality
        val btnSave: Button = view.findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            Log.d("Form2Fragment", "Save button clicked")
            submitForm2Data()
        }

        // Prevent parent scroll when interacting with signature view
        signatureView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    (view.parent as? ScrollView)?.requestDisallowInterceptTouchEvent(true)
                    Log.d("Form2Fragment", "Signature view touch started")
                }
                MotionEvent.ACTION_MOVE -> {
                    (view.parent as? ScrollView)?.requestDisallowInterceptTouchEvent(true)
                    Log.d("Form2Fragment", "Signature view touch in progress")
                }
                MotionEvent.ACTION_UP -> {
                    (view.parent as? ScrollView)?.requestDisallowInterceptTouchEvent(false)
                    Log.d("Form2Fragment", "Signature view touch ended")
                }
            }
            signatureView.onTouchEvent(event) // Pass the event to SignatureView
        }

        return view
    }

    // Clear all fields and signature
    private fun clearAllFields() {
        Log.d("Form2Fragment", "Clearing all fields")
        nameEditText.text.clear() // Clear the name field
        areaConsentedEditText.text.clear() // Clear the area consented field
        signatureView.clearSignature() // Clear the signature view
        calendarView.setDate(System.currentTimeMillis(), false, true) // Reset the calendar to today's date
        Toast.makeText(context, "All fields cleared", Toast.LENGTH_SHORT).show() // Optional feedback
    }

    // Capture signature as Base64 string
    private fun captureSignature(): String {
        Log.d("Form2Fragment", "Capturing signature")
        val bitmap = signatureView.getSignatureBitmap()
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val signatureBytes = outputStream.toByteArray()
        val signature = Base64.encodeToString(signatureBytes, Base64.DEFAULT)
        Log.d("Form2Fragment", "Signature captured as Base64: $signature")
        return signature
    }

    private fun submitForm2Data() {
        val name = nameEditText.text.toString().trim()
        val areaConsented = areaConsentedEditText.text.toString().trim()

        // Ensure that selectedDate is initialized
        if (!::selectedDate.isInitialized) {
            Log.e("Form2Fragment", "Date not selected")
            Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate input
        if (name.isEmpty() || areaConsented.isEmpty()) {
            Log.e("Form2Fragment", "Validation failed: incomplete form data")
            Toast.makeText(context, "Please complete all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Capture the signature
        val signature = captureSignature()
        if (signature.isEmpty()) {
            Log.e("Form2Fragment", "Signature is empty")
            Toast.makeText(context, "Please provide a signature", Toast.LENGTH_SHORT).show()
            return
        }

        // Create Form2Request object with the Date type
        val form2Request = Form2Request(
            name = name,
            areasConcernedForNeedling = areaConsented,
            date = selectedDate, // Keep the date as Date type
            signature = signature,
            form2Filled = true // This should be true when the form is successfully saved
        )

        Log.d("Form2Fragment", "Submitting Form2Request: $form2Request")

        // Call API
        apiService.submitForm2Data(form2Request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("Form2Fragment", "Form submitted successfully")
                    Toast.makeText(context, "Form submitted successfully", Toast.LENGTH_SHORT).show()

                    // Update the state to true after successful form submission
                    Form2State.form2Filled = true

                    // Navigate back to the intake forms
                    findNavController().navigate(R.id.action_nav_form2_to_nav_intake_forms)
                } else {
                    Log.e("Form2Fragment", "Form submission failed: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to submit form", Toast.LENGTH_SHORT).show()
                    Form2State.form2Filled = false // Ensure form state remains false if submission fails
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Form2Fragment", "Form submission error: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Form2State.form2Filled = false // Ensure form state remains false on failure
            }
        })
    }
}
