package com.xbcad.xbcad7319_physiotherapyapp.ui.app_patient.app_patient

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.xbcad.xbcad7319_physiotherapyapp.R

class AppointmentPatientFragment : Fragment() {

    companion object {
        fun newInstance() = AppointmentPatientFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_appointment_client, container, false)

        // Initialize the ImageButton using the inflated view
        val ibtnHome: ImageButton = view.findViewById(R.id.ibtnHome)
        val ibtnBookAppointments: ImageButton = view.findViewById(R.id.ibtnBook_Appointment)
        val ibtnRescheduleAppointments: ImageButton = view.findViewById(R.id.ibtnReschedule_Appointment)
        val ibtnCancelAppointments: ImageButton = view.findViewById(R.id.ibtnCancel_Appointment)

        // Set OnClickListener for the Home button
        ibtnHome.setOnClickListener {
             Log.d("AppointmentPatientFragment", "Home button clicked. Navigating to Home screen.")
            findNavController().navigate(R.id.action_nav_app_to_nav_home_patient)
        }
        // Set OnClickListener for the Book Appointment button
        ibtnBookAppointments.setOnClickListener {
             Log.d("AppointmentPatientFragment", "Book Appointment button clicked. Navigating to Book Appointment screen.")
            findNavController().navigate(R.id.action_nav_app_to_nav_book_app_patient)
        }
        // Set OnClickListener for the Reschedule Appointment button
        ibtnRescheduleAppointments.setOnClickListener {
             Log.d("AppointmentPatientFragment", "Reschedule Appointment button clicked. Navigating to Reschedule Appointment screen.")
            findNavController().navigate(R.id.action_nav_app_to_nav_reschedule_app_patient)
        }
        // Set OnClickListener for the Cancel Appointment button
        ibtnCancelAppointments.setOnClickListener {
             Log.d("AppointmentPatientFragment", "Cancel Appointment button clicked. Navigating to Cancel Appointment screen.")
            findNavController().navigate(R.id.action_nav_app_to_nav_cancel_app_patient)
        }

        return view
    }
}
