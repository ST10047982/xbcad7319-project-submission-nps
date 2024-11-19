package com.app.xbcad7319_physiotherapyapp.ui.form1

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.xbcad7319_physiotherapyapp.R
import com.app.xbcad7319_physiotherapyapp.ui.ApiService
import com.app.xbcad7319_physiotherapyapp.ui.Form1Request
import com.app.xbcad7319_physiotherapyapp.ui.SignatureView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*

object Form1State {
    var form1Filled: Boolean = false
}


class Form1Fragment : Fragment() {

    // PATIENT DETAILS
    private lateinit var firstNamePEditText: EditText
    private lateinit var surnamePEditText: EditText
    private lateinit var titlePSpinner: Spinner
    private lateinit var idPEditText: EditText
    private lateinit var agePEditText: EditText
    private lateinit var addressPEditText: EditText
    private lateinit var codePEditText: EditText
    private lateinit var cellNumberPEditText: EditText
    private lateinit var workNumberPEditText: EditText
    private lateinit var homeNumberPEditText: EditText
    private lateinit var emailPEditText: EditText
    private lateinit var medicalAidNamePEditText: EditText
    private lateinit var medicalAidNumberPEditText: EditText

    // PERSON RESPONSIBLE
    private lateinit var firstNameREditText: EditText
    private lateinit var surnameREditText: EditText
    private lateinit var titleRSpinner: Spinner
    private lateinit var idEditRText: EditText
    private lateinit var ageEditRText: EditText
    private lateinit var addressREditText: EditText
    private lateinit var codeREditText: EditText
    private lateinit var cellNumberREditText: EditText
    private lateinit var workNumberREditText: EditText
    private lateinit var homeNumberREditText: EditText
    private lateinit var emailREditText: EditText

    // NEXT OF KIN
    private lateinit var firstNameKEditText: EditText
    private lateinit var addressKEditText: EditText
    private lateinit var codeKEditText: EditText

    // SIGNATURE
    private lateinit var nameSEditText: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var etxtSSignature: SignatureView
    private lateinit var placeSEditText: EditText
    private lateinit var calendarView: CalendarView // Correct reference
    private lateinit var selectedDate: Date

    // BUTTONS
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private lateinit var ibtnBack: ImageButton
    private lateinit var btnClearSignature: Button

    private var FormFilled: Boolean = false


    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_form1, container, false)

        // Initialize all EditTexts, Spinners, SignatureView, and Buttons
        firstNamePEditText = view.findViewById(R.id.etxtPFirst_Name)
        surnamePEditText = view.findViewById(R.id.etxtPSurname)
        titlePSpinner = view.findViewById(R.id.sPTitle)
        idPEditText = view.findViewById(R.id.etxtPID)
        agePEditText = view.findViewById(R.id.etxtPAge)
        addressPEditText = view.findViewById(R.id.etxtPAddress)
        codePEditText = view.findViewById(R.id.etxtPCode)
        cellNumberPEditText = view.findViewById(R.id.etxtPCelNo)
        workNumberPEditText = view.findViewById(R.id.etxtPWorkTel)
        homeNumberPEditText = view.findViewById(R.id.etxtPHomeTel)
        emailPEditText = view.findViewById(R.id.etxtPEmail)
        medicalAidNamePEditText = view.findViewById(R.id.etxtPMedaiclName)
        medicalAidNumberPEditText = view.findViewById(R.id.etxtPMedicalNo)

        // PERSON RESPONSIBLE
        firstNameREditText = view.findViewById(R.id.etxtRFirstName)
        surnameREditText = view.findViewById(R.id.etxtRSurname)
        titleRSpinner = view.findViewById(R.id.sRTitle)
        idEditRText = view.findViewById(R.id.etxtRID)
        ageEditRText = view.findViewById(R.id.etxtRAge)
        addressREditText = view.findViewById(R.id.etxtRAddress)
        codeREditText = view.findViewById(R.id.etxtRCode)
        cellNumberREditText = view.findViewById(R.id.etxtRCelNumber)
        workNumberREditText = view.findViewById(R.id.etxtRWorkNumber)
        homeNumberREditText = view.findViewById(R.id.etxtRHomeNumber)
        emailREditText = view.findViewById(R.id.etxtREmail)

        // NEXT OF KIN
        firstNameKEditText = view.findViewById(R.id.etxtKName)
        addressKEditText = view.findViewById(R.id.etxtKAddress)
        codeKEditText = view.findViewById(R.id.etxtKCode)

        // SIGNATURE
        nameSEditText = view.findViewById(R.id.etxtSName)
        typeSpinner = view.findViewById(R.id.sType)
        etxtSSignature = view.findViewById(R.id.signatureView2)
        placeSEditText = view.findViewById(R.id.etxtSPlace)
        calendarView = view.findViewById(R.id.date) // Ensure this ID matches your layout

        // BUTTONS
        btnCancel = view.findViewById(R.id.btnCancel)
        btnSave = view.findViewById(R.id.btnSave)
        ibtnBack = view.findViewById(R.id.ibtnBack)
        btnClearSignature = view.findViewById(R.id.btnClearSignature)

        // Initialize API service
        apiService = com.app.xbcad7319_physiotherapyapp.ui.ApiClient.getRetrofitInstance(requireContext()).create(ApiService::class.java)

        // Back button functionality
        ibtnBack.setOnClickListener {
            Log.d("Form1Fragment", "Back button clicked")
            findNavController().navigate(R.id.action_nav_form1_to_nav_intake_forms)
        }

        // Cancel button functionality
        btnCancel.setOnClickListener {
            Log.d("Form1Fragment", "Cancel button clicked")
            clearAllFields()
        }

        // Save button functionality
        btnSave.setOnClickListener {
            Log.d("Form1Fragment", "Save button clicked")
            Form1State.form1Filled = false // Use the global state
            submitForm1Data()
        }

        // Clear signature button functionality
        btnClearSignature.setOnClickListener {
            Log.d("Form1Fragment", "Clear signature button clicked")
            etxtSSignature.clearSignature() // Clear the signature view
        }

        populateTitleSpinnerP()
        populateTitleSpinnerR()
        populateTypeSpinner()

        // Set up CalendarView listener to capture selected date
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time // Update selectedDate when date is picked
        }

        // Prevent parent scroll when interacting with signature view
        etxtSSignature.setOnTouchListener { _, event ->
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
            etxtSSignature.onTouchEvent(event) // Pass the event to SignatureView
        }


        return view
    }

    private fun populateTitleSpinnerP() {
        // Define the list of titles
        val titles = listOf("Mr.", "Mrs.", "Ms.")

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, titles)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        titlePSpinner.adapter = adapter
    }

    private fun populateTitleSpinnerR() {
        // Define the list of titles
        val titles = listOf("Mr.", "Mrs.", "Ms.")

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, titles)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        titleRSpinner.adapter = adapter
    }

    private fun populateTypeSpinner() {
        // Define the list of types
        val types = listOf("Patient", "Gaurantor", "Gaurdian")

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        typeSpinner.adapter = adapter
    }

    // Clear all fields
    private fun clearAllFields() {
        Log.d("Form1Fragment", "Clearing all fields")
        firstNamePEditText.text.clear()
        surnamePEditText.text.clear()
        idPEditText.text.clear()
        agePEditText.text.clear()
        addressPEditText.text.clear()
        codePEditText.text.clear()
        cellNumberPEditText.text.clear()
        workNumberPEditText.text.clear()
        homeNumberPEditText.text.clear()
        emailPEditText.text.clear()
        medicalAidNamePEditText.text.clear()
        medicalAidNumberPEditText.text.clear()

        firstNameREditText.text.clear()
        surnameREditText.text.clear()
        idEditRText.text.clear()
        ageEditRText.text.clear()
        addressREditText.text.clear()
        codeREditText.text.clear()
        cellNumberREditText.text.clear()
        workNumberREditText.text.clear()
        homeNumberREditText.text.clear()
        emailREditText.text.clear()

        firstNameKEditText.text.clear()
        addressKEditText.text.clear()
        codeKEditText.text.clear()

        nameSEditText.text.clear()
        placeSEditText.text.clear()
        etxtSSignature.clearSignature()

        Toast.makeText(context, "All fields cleared", Toast.LENGTH_SHORT).show()
    }


    // Capture signature as Base64 string
    private fun captureSignature(): String {
        Log.d("Form2Fragment", "Capturing signature")
        val bitmap = etxtSSignature.getSignatureBitmap()
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val signatureBytes = outputStream.toByteArray()
        val signature = Base64.encodeToString(signatureBytes, Base64.DEFAULT)
        Log.d("Form2Fragment", "Signature captured as Base64: $signature")
        return signature
    }




    private fun submitForm1Data() {
        // Validate required input fields (only check non-empty for required fields)
        if (firstNamePEditText.text.isNullOrEmpty() ||
            surnamePEditText.text.isNullOrEmpty() ||
            idPEditText.text.isNullOrEmpty() ||
            agePEditText.text.isNullOrEmpty() ||
            addressPEditText.text.isNullOrEmpty() ||
            codePEditText.text.isNullOrEmpty() ||
            cellNumberPEditText.text.isNullOrEmpty() ||
            emailPEditText.text.isNullOrEmpty() ||
            firstNameKEditText.text.isNullOrEmpty() ||
            addressKEditText.text.isNullOrEmpty() ||
            codeKEditText.text.isNullOrEmpty() ||
            nameSEditText.text.isNullOrEmpty() ||
            placeSEditText.text.isNullOrEmpty() ||
            !::selectedDate.isInitialized
        ) {
            Log.e("Form1Fragment", "Validation failed: incomplete form data")
            Toast.makeText(context, "Please complete all required fields", Toast.LENGTH_SHORT).show()

            // Ensure FormFilled is false when validation fails
            FormFilled = false
            Log.d("FormStatus", "Form filled status: $FormFilled")
            return
        }

        // Capture the signature
        val signature = captureSignature()
        if (signature.isNullOrEmpty()) {
            Log.e("Form1Fragment", "Signature is empty")
            Toast.makeText(context, "Please provide a signature", Toast.LENGTH_SHORT).show()

            // Ensure FormFilled is false if signature is empty
            FormFilled = false
            Log.d("FormStatus", "Form filled status: $FormFilled")
            return
        }

        // Create the Form1Request object with empty strings for optional fields
        val form1Request = Form1Request(
            firstNameP = firstNamePEditText.text.toString(),
            surnameP = surnamePEditText.text.toString(),
            titleP = titlePSpinner.selectedItem?.toString() ?: "",
            idP = idPEditText.text.toString(),
            ageP = agePEditText.text.toString().toInt(), // Convert to Int
            addressP = addressPEditText.text.toString(),
            codeP = codePEditText.text.toString(),
            cellNumberP = cellNumberPEditText.text.toString(),
            workNumberP = workNumberPEditText.text.toString().ifEmpty { "" },
            homeNumberP = homeNumberPEditText.text.toString().ifEmpty { "" },
            emailP = emailPEditText.text.toString(),
            medicalAidNameP = medicalAidNamePEditText.text.toString().ifEmpty { "" },
            medicalAidNumberP = medicalAidNumberPEditText.text.toString().ifEmpty { "" },

            // Responsible party details (all default to empty string if not provided)
            firstNameR = firstNameREditText.text.toString().ifEmpty { "" },
            surnameR = surnameREditText.text.toString().ifEmpty { "" },
            titleR = titleRSpinner.selectedItem?.toString() ?: "",
            idR = idEditRText.text.toString().ifEmpty { "" },
            ageR = ageEditRText.text.toString().ifEmpty { "" },
            addressR = addressREditText.text.toString().ifEmpty { "" },
            codeR = codeREditText.text.toString().ifEmpty { "" },
            cellNumberR = cellNumberREditText.text.toString().ifEmpty { "" },
            workNumberR = workNumberREditText.text.toString().ifEmpty { "" },
            homeNumberR = homeNumberREditText.text.toString().ifEmpty { "" },
            emailR = emailREditText.text.toString().ifEmpty { "" },

            // Next of kin details
            firstNameK = firstNameKEditText.text.toString(),
            addressK = addressKEditText.text.toString(),
            codeK = codeKEditText.text.toString(),

            // Signature details
            nameS = nameSEditText.text.toString(),
            typeS = typeSpinner.selectedItem?.toString() ?: "", // Changed from type to typeS
            signature = signature,
            placeS = placeSEditText.text.toString(),
            date = selectedDate,
            form1Filled = true
        )

        // Rest of your API submission code remains the same
        apiService.submitForm1Data(form1Request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Data submitted successfully", Toast.LENGTH_SHORT).show()
                    FormFilled = true
                    Log.d("FormStatus", "Form filled status: $FormFilled")
                    Form1State.form1Filled = true
                    // Clear all fields after successful submission
                    clearAllFields()
                } else {
                    Log.e("Form1Fragment", "Submission failed: ${response.message()}")
                    Toast.makeText(context, "Submission failed: ${response.message()}", Toast.LENGTH_SHORT).show()

                    // Ensure FormFilled is false if submission failed
                    Form1State.form1Filled = false
                    Log.d("FormStatus", "Form filled status: $FormFilled")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Form1Fragment", "Submission error: ${t.message}", t)
                Toast.makeText(context, "Submission failed: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }
}
