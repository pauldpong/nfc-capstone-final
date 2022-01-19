package com.capstone.nfc.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
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
    private val model by viewModels<DashboardViewModel>()
    private lateinit var pdfChooserActivity: ActivityResultLauncher<Intent>
    private lateinit var myFilesAdapter: FileViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pdfChooserActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data!!.data

                uri?.let {
                    context?.contentResolver?.query(uri, null, null, null, null)
                }?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    val fileName: String = cursor.getString(nameIndex)
                    model.uploadPdf(uri, fileName).observe(viewLifecycleOwner) { response ->
                        if (response is Success) {
                            dataBinding.pdfUri.text = fileName
                            model.loadMyFiles()
                        } else if (response is Loading) {
                            dataBinding.pdfUri.text = "loading..."
                        } else {
                            dataBinding.pdfUri.text = "error"
                        }
                    }
                }
            }
        }

        model.loadMyFiles()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUser()
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

        model.myFiles.observe(viewLifecycleOwner) {
            myFilesAdapter.submitList(it)
        }
    }


    private fun setReaderButtonCallback() {
        dataBinding.readerButton.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_readerFragment)
        }
    }

    private fun getUser() {
        model.getUser().observe(viewLifecycleOwner) { response ->
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
            model.signOut().observe(viewLifecycleOwner) {}
        }
    }
}