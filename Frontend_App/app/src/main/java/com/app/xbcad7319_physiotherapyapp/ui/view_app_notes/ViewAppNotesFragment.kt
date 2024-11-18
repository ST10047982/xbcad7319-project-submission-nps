package com.app.xbcad7319_physiotherapyapp.ui.view_app_notes

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.xbcad7319_physiotherapyapp.R

class ViewAppNotesFragment : Fragment() {

    companion object {
        fun newInstance() = ViewAppNotesFragment()
    }

    private val viewModel: ViewAppNotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_view_app_notes, container, false)
    }
}