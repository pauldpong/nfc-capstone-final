package com.capstone.nfc.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

private const val TAG = "DashboardFragment"

@AndroidEntryPoint
class DashboardFragment: BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {
    private val viewModel by viewModels<DashboardViewModel>()
    private lateinit var pdfChooserActivity: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pdfChooserActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data!!.data
                uri?.let {
                    viewModel.uploadPdf(uri).observe(viewLifecycleOwner) { response ->
                        if (response is Success) {
                            dataBinding.pdfUri.text = response.data?.name
                        } else {
                            dataBinding.pdfUri.text = "error"
                        }
                    }
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUser()
        setWriterButtonCallback()
        setReaderButtonCallback()
        setUploadPdfButtonCallback()
        setSignOutCallback()

        viewModel.getFiles().observe(viewLifecycleOwner) { response ->
            if (response is Success) {
                Log.e(TAG,response.data.items.toString())
                Log.e(TAG,response.data.prefixes.toString())
            }
        }
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

    private fun setUploadPdfButtonCallback() {
        dataBinding.uploadPdfButton.setOnClickListener {
            val intent = Intent().apply {
                type = "*/*"
                action = Intent.ACTION_GET_CONTENT
            }

            pdfChooserActivity.launch(intent)
        }
    }

    private fun setSignOutCallback() {
        dataBinding.signOutButton.setOnClickListener {
            viewModel.signOut().observe(viewLifecycleOwner) {}
        }
    }
}