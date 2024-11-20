package com.xbcad.xbcad7319_physiotherapyapp.ui.main_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.xbcad.xbcad7319_physiotherapyapp.R
import com.xbcad.xbcad7319_physiotherapyapp.databinding.FragmentMainMenuBinding

class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(MainMenuViewModel::class.java)

        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the Button using the binding
        val btnPatientLogin: Button = binding.btnLPatient
        val btnStaffLogin: Button = binding.btnLStaff
        val btnPatientRegister: Button = binding.btnRPatient

        // Set OnClickListener for Patient Login button
        btnPatientLogin.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_login_patient)
        }
        // Set OnClickListener for Staff Login button
        btnStaffLogin.setOnClickListener {
            findNavController().navigate(R.id.action_nav_main_menu_to_nav_login_staff)
        }
        // Set OnClickListener for Patient Login button
        btnPatientRegister.setOnClickListener {
            findNavController().navigate(R.id.action_nav_main_menu_to_nav_register_patient)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
