package com.capstone.nfc.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.nfc.R
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.data.File
import com.capstone.nfc.data.Response.*
import com.capstone.nfc.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "DashboardFragment"

@AndroidEntryPoint
class DashboardFragment: BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {
    private val viewModel by viewModels<DashboardViewModel>()
    private lateinit var pdfChooserActivity: ActivityResultLauncher<Intent>
    private lateinit var myFilesAdapter: FileViewAdapter

    private var myFiles: MutableList<File> = mutableListOf()

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

        // Setup files list
        dataBinding.myFilesList.apply {
            layoutManager = LinearLayoutManager(activity?.applicationContext)
            myFilesAdapter = FileViewAdapter { file ->
                file.path?.let {
                    val action = DashboardFragmentDirections.actionDashboardToWriterFragment(it)
                    findNavController().navigate(action)
                }

            }
            adapter = myFilesAdapter
        }

        // TODO: Needs to be done in the view model to avoid multiple inserts when coming back
        viewModel.getFiles().observe(viewLifecycleOwner) { response ->
            if (response is Success) {
                for (file in response.data.items) {

                    myFiles.add(File(file.name, file.path))
                }
                myFilesAdapter.submitList(myFiles)
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