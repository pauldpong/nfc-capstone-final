package com.capstone.nfc.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.nfc.R
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.data.FileMetadata
import com.capstone.nfc.data.Response.*
import com.capstone.nfc.data.StorageFile
import com.capstone.nfc.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DashboardFragment: BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {
    private val model by viewModels<DashboardViewModel>()
    private lateinit var fileChooserActivity: ActivityResultLauncher<Intent>
    private lateinit var myFilesAdapter: FileViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fileChooserActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data!!.data

                uri?.let {
                    context?.contentResolver?.query(uri, null, null, null, null)
                }?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    val fullFileName: String = cursor.getString(nameIndex)

                    model.uploadPdf(uri, fullFileName).observe(viewLifecycleOwner) { response ->
                        if (response is Success) {
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

            val onClick = { file: StorageFile ->
                file.path.let {
                    val action = DashboardFragmentDirections.actionDashboardToFilePreviewFragment(it)
                    findNavController().navigate(action)
                }
            }

            val onLongClick = { file: StorageFile ->
                file.uuid.let {
                    val action = DashboardFragmentDirections.actionDashboardToWriterFragment(it)
                    findNavController().navigate(action)
                }
            }
            myFilesAdapter = FileViewAdapter(onClick, onLongClick)
            adapter = myFilesAdapter
        }

        model.getMyFiles().observe(viewLifecycleOwner) {
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

            fileChooserActivity.launch(intent)
        }
    }

    private fun setSignOutCallback() {
        dataBinding.signOutButton.setOnClickListener {
            model.signOut().observe(viewLifecycleOwner) {}
        }
    }
}