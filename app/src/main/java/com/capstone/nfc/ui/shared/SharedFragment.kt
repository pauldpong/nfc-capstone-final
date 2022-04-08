package com.capstone.nfc.ui.shared

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.data.FileMetadata
import com.capstone.nfc.data.Response
import com.capstone.nfc.data.StorageFile
import com.capstone.nfc.databinding.FragmentSharedBinding
import com.capstone.nfc.ui.dashboard.DashboardFragmentDirections
import com.capstone.nfc.ui.dashboard.DashboardViewModel
import com.capstone.nfc.ui.dashboard.FileViewAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "SharedFragment"

@AndroidEntryPoint
class SharedFragment: BaseFragment<FragmentSharedBinding>(FragmentSharedBinding::inflate) {
    private val model by viewModels<SharedViewModel>()
    private lateinit var myFilesAdapter: SharedFileViewAdapter
    @Inject lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.sharedFiles.apply {
            layoutManager = LinearLayoutManager(activity?.applicationContext)

            val onClick: (FileMetadata) -> Unit = { file: FileMetadata ->
                auth.currentUser?.uid?.let { uid ->
                    // Check if user has permissions
                    if (file.accessors.contains(uid)) {
                        // Navigate to FilePreview
                        val action = SharedFragmentDirections.actionSharedFragmentToFilePreviewFragment2(file.path)
                        findNavController().navigate(action)
                    } else {
                        Log.e(TAG, "user has no permissions")
                    }
                }
            }

            myFilesAdapter = SharedFileViewAdapter(onClick)
            adapter = myFilesAdapter
        }


        model.sharedFiles.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Loading -> {

                }
                is Response.Success -> {
                    val sharedFiles : List<FileMetadata> = it.data
                    if (sharedFiles.isNotEmpty()) {
                        myFilesAdapter.submitList(sharedFiles)
                        dataBinding.emptyListPlaceholder.visibility = View.GONE
                    } else {
                        myFilesAdapter.submitList(sharedFiles)
                        dataBinding.emptyListPlaceholder.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}