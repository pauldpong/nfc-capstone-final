package com.capstone.nfc.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.capstone.nfc.R
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.data.Response.*
import com.capstone.nfc.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment: BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {
    private val viewModel by viewModels<DashboardViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUser()
        setWriterButtonCallback()
        setReaderButtonCallback()
        setSignOutCallback()
    }

    private fun setWriterButtonCallback() {
        dataBinding.contactCard.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_writerFragment)
        }
    }

    private fun setReaderButtonCallback() {
        dataBinding.readerButton.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_readerFragment)
        }
    }

    private fun getUser() {
        viewModel.getUser().observe(viewLifecycleOwner) { response ->
            if (response is Success) {
                dataBinding.nameField.append(response.data.name)
                dataBinding.emailField.append(response.data.email)
                dataBinding.phoneField.append(response.data.phone)
            }
        }
    }

    private fun setSignOutCallback() {
        dataBinding.signOutButton.setOnClickListener {
            viewModel.signOut().observe(viewLifecycleOwner) {}
        }
    }
}