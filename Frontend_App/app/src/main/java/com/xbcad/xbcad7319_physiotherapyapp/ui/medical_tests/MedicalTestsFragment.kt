package com.xbcad.xbcad7319_physiotherapyapp.ui.medical_tests

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.xbcad.xbcad7319_physiotherapyapp.ui.MedicalTest
import java.text.SimpleDateFormat
import java.util.*

class MedicalTestsFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var spinner: Spinner
    private lateinit var btnAddImage: Button
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var etxtNotes: EditText
    private lateinit var etxtResults: EditText
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var sharedPref: SharedPreferences

    private val testTypes = arrayOf(
        "Range of Motion Assessment",
        "Muscle Strength Test",
        "Balance Assessment",
        "Gait Analysis",
        "Posture Assessment",
        "Functional Movement Screen",
        "Joint Mobility Test",
        "Neurological Assessment",
        "Pain Assessment",
        "Cardiovascular Fitness Test",
        "Other"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_medical_tests, container, false)

        initializeViews(view)
        setupSpinner()

        // Initialize SharedPreferences
        sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        // Load medical tests after sharedPref is initialized
        loadMedicalTests()
        setupListeners()

        return view
    }

    private fun initializeViews(view: View) {
        listView = view.findViewById(R.id.medicalTestList)
        spinner = view.findViewById(R.id.spinner)
        btnAddImage = view.findViewById(R.id.btnAdd_Image)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
        etxtNotes = view.findViewById(R.id.etxtNotes)
        etxtResults = view.findViewById(R.id.etxtResults)

        view.findViewById<ImageButton>(R.id.ibtnHome).setOnClickListener {
            findNavController().navigate(R.id.action_nav_medical_tests_to_nav_home_patient)
        }

        // Setup list view item click listener
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val test = listView.adapter.getItem(position) as MedicalTest
            showDeleteDialog(test)
            true
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            testTypes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupListeners() {
        btnAddImage.setOnClickListener {
            openImagePicker()
        }

        btnSave.setOnClickListener {
            saveMedicalTest()
        }

        btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Image"),
            PICK_IMAGE_REQUEST
        )
    }

    private fun loadMedicalTests() {
        val token = "Bearer ${sharedPref.getString("bearerToken", "")}"
        val apiService = ApiClient.getService(requireContext())

        apiService.getMedicalTests(token).enqueue(object : Callback<List<MedicalTest>> {
            override fun onResponse(
                call: Call<List<MedicalTest>>,
                response: Response<List<MedicalTest>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { tests ->
                        displayMedicalTests(tests)
                    }
                }
            }

            override fun onFailure(call: Call<List<MedicalTest>>, t: Throwable) {
                Toast.makeText(context, "Failed to load medical tests", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayMedicalTests(tests: List<MedicalTest>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            tests.map { "${it.testName} - ${formatDate(it.testDate)}\n${it.testResults}" }
        )
        listView.adapter = adapter
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun saveMedicalTest() {
        // Validate inputs first
        if (!validateInputs()) {
            showToast("Please fill in all required fields")
            return
        }

        // Get and validate token
        val bearerToken = sharedPref.getString("bearerToken", "")
        if (bearerToken.isNullOrEmpty()) {
            showToast("Authentication error - please log in again")
            return
        }

        // Get and validate userId
        val userId = sharedPref.getString("userId", "")
        if (userId.isNullOrEmpty()) {
            showToast("User ID not found - please log in again")
            return
        }

        // Show loading indicator
        val progressDialog = ProgressDialog(context).apply {
            setMessage("Saving medical test...")
            setCancelable(false)
            show()
        }

        val testType = spinner.selectedItem.toString()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val medicalTest = MedicalTest(
            id = "", // Empty string for new tests
            patientId = userId,
            testName = testType,
            testDate = currentDate,
            testResults = etxtResults.text.toString().trim(),
            imageUrl = selectedImageUri?.toString(),
            notes = etxtNotes.text.toString().trim()
        )

        val token = "Bearer $bearerToken"
        val apiService = ApiClient.getService(requireContext())

        apiService.addMedicalTest(token, medicalTest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()

                when {
                    response.isSuccessful -> {
                        showToast("Medical test saved successfully")
                        clearInputs()
                        loadMedicalTests()
                    }
                    response.code() == 401 -> {
                        showToast("Authentication error - please log in again")
                        // Optional: Navigate to login screen
                        // findNavController().navigate(R.id.action_to_login)
                    }
                    else -> {
                        val errorBody = response.errorBody()?.string()
                        showToast("Failed to save medical test: ${errorBody ?: "Unknown error"}")
                        Log.e("MedicalTests", "Error response: $errorBody")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                showToast("Network error: ${t.message}")
                Log.e("MedicalTests", "Network error", t)
            }
        })
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate test type selection
        if (spinner.selectedItem == null) {
            showToast("Please select a test type")
            isValid = false
        }

        // Validate results field
        if (etxtResults.text.toString().trim().isEmpty()) {
            etxtResults.error = "Please enter test results"
            isValid = false
        }

        // Validate if results exceed maximum length (if there's a limit)
        val maxResultsLength = 1000 // Adjust this value based on your backend limits
        if (etxtResults.text.toString().length > maxResultsLength) {
            etxtResults.error = "Results text is too long"
            isValid = false
        }

        return isValid
    }

    private fun showDeleteDialog(test: MedicalTest) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Test")
            .setMessage("Are you sure you want to delete this test?")
            .setPositiveButton("Delete") { _, _ ->
                deleteMedicalTest(test.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteMedicalTest(testId: String) {
        val token = "Bearer ${sharedPref.getString("bearerToken", "")}"
        val apiService = ApiClient.getService(requireContext())

        apiService.deleteMedicalTest(token, testId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    showToast("Test deleted successfully")
                    loadMedicalTests()
                } else {
                    showToast("Failed to delete test")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showToast("Error deleting test: ${t.message}")
            }
        })
    }

    private fun clearInputs() {
        etxtResults.text.clear()
        etxtNotes.text.clear()
        selectedImageUri = null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            btnAddImage.text = "Image Selected"
        }
    }
}