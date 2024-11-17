package com.app.xbcad7319_physiotherapyapp.ui.intake_forms

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.app.xbcad7319_physiotherapyapp.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class IntakeFormsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_intake_forms, container, false)

        // Initialize the UI elements
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        val btnForm1PDF: Button = view.findViewById(R.id.btnForm1PDF)
        val btnForm1: Button = view.findViewById(R.id.btnForm1)
        val btnForm2PDF: Button = view.findViewById(R.id.btnForm2PDF)
        val btnForm2: Button = view.findViewById(R.id.btnForm2)

        Log.d("IntakeFormsFragment", "UI elements initialized")

        // Set OnClickListener for the Home button
        ibtnHome.setOnClickListener {
            Log.d("IntakeFormsFragment", "Home button clicked")
            findNavController().navigate(R.id.action_nav_intake_forms_to_nav_home_patient)
        }

        // Set OnClickListener for the Form 1 button
        btnForm1.setOnClickListener {
            Log.d("IntakeFormsFragment", "Form 1 button clicked")
            findNavController().navigate(R.id.action_nav_intake_forms_to_nav_form1)
        }

        // Set OnClickListener for the Form 2 button
        btnForm2.setOnClickListener {
            Log.d("IntakeFormsFragment", "Form 2 button clicked")
            findNavController().navigate(R.id.action_nav_intake_forms_to_nav_form2)
        }

        // Set OnClickListener for the Form 1 PDF button
        btnForm1PDF.setOnClickListener {
            Log.d("IntakeFormsFragment", "Form 1 PDF button clicked")
            openPdfFromAssets("Form1.pdf")
            Toast.makeText(context, "Opening Form1 PDF", Toast.LENGTH_SHORT).show()
        }

        // Set OnClickListener for the Form 2 PDF button
        btnForm2PDF.setOnClickListener {
            Log.d("IntakeFormsFragment", "Form 2 PDF button clicked")
            openPdfFromAssets("Form2.pdf")
            Toast.makeText(context, "Opening Form2 PDF", Toast.LENGTH_SHORT).show()
        }

        return view // Return the inflated view at the end
    }

    private fun openPdfFromAssets(fileName: String) {
        try {
            Log.d("IntakeFormsFragment", "Attempting to open PDF: $fileName")

            // Get the input stream for the PDF file from assets
            val assetManager = requireContext().assets
            val inputStream: InputStream = assetManager.open(fileName)

            // Create a temporary file to store the PDF
            val tempFile = File(requireContext().cacheDir, fileName)
            val outputStream = FileOutputStream(tempFile)

            // Copy the input stream to the temporary file
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()

            Log.d("IntakeFormsFragment", "PDF copied to temp file successfully")

            // Open the PDF using an external viewer
            openPdfInExternalViewer(tempFile)

        } catch (e: IOException) {
            Log.e("IntakeFormsFragment", "Error opening PDF: ${e.message}", e)
        }
    }

    private fun openPdfInExternalViewer(file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )

        // Create an Intent to view the PDF
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            Log.d("IntakeFormsFragment", "Opening PDF in external viewer")
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("IntakeFormsFragment", "No application available to view PDF: ${e.message}", e)
        }
    }
}
