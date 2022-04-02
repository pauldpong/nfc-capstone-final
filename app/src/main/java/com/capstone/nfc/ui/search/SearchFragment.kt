package com.capstone.nfc.ui.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.data.FileMetadata
import com.capstone.nfc.data.Response
import com.capstone.nfc.data.StorageFile
import com.capstone.nfc.databinding.FragmentSearchBinding
import com.capstone.nfc.ui.dashboard.FileViewAdapter
import com.capstone.nfc.ui.shared.SharedFileViewAdapter
import com.google.android.gms.common.api.internal.ActivityLifecycleObserver
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "SearchFragment"

@AndroidEntryPoint
class SearchFragment: BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate), LifecycleObserver  {
    private val model by viewModels<SearchViewModel>()
    private lateinit var myFilesAdapter: FileViewAdapter
    private lateinit var mySharedFilesAdapter: SharedFileViewAdapter
    @Inject
    lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.emptyListPlaceholder.visibility = View.INVISIBLE

        // Setup my files list
        dataBinding.myFilesList.apply {
            layoutManager = LinearLayoutManager(activity?.applicationContext)

            val onClick = { file: StorageFile ->
                val action = SearchFragmentDirections.actionSearchToFileManagementFragment(file)
                findNavController().navigate(action)
            }

            val onLongClick = { file: StorageFile ->
                val action = SearchFragmentDirections.actionSearchToWriterActivity(file)
                findNavController().navigate(action)
            }
            myFilesAdapter = FileViewAdapter(onClick, onLongClick)
            adapter = myFilesAdapter
        }

        dataBinding.sharedFiles.apply {
            layoutManager = LinearLayoutManager(activity?.applicationContext)

            val onClick: (FileMetadata) -> Unit = { file: FileMetadata ->
                auth.currentUser?.uid?.let { uid ->
                    // Check if user has permissions
                    if (file.accessors.contains(uid)) {
                        // Navigate to FilePreview
                        val action = SearchFragmentDirections.actionSearchToFilePreviewFragment2(file.path)
                        findNavController().navigate(action)
                    } else {
                        Log.e(TAG, "user has no permissions")
                    }
                }
            }

            mySharedFilesAdapter = SharedFileViewAdapter(onClick)
            adapter = mySharedFilesAdapter
        }
//        var query : String = (activity as SearchableActivity).searchQuery
//        searchFiles(query)
        activity?.lifecycleScope?.launchWhenCreated {
            var query : String = (activity as SearchableActivity).searchQuery
            searchFiles(query)
        }
    }

    fun searchFiles (query: String) {
        //need to search files and add it to view
        //getting my files
        var found : Boolean = false
        model.getMyFiles().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                //search the file for the query
                val result = mutableListOf<StorageFile>()
                for (file in it){
                    if (file.name.lowercase().contains(query.lowercase())) {
                        result.add(file)
                    }
                }
                if (result.isNotEmpty()) {
                    found = true
                    myFilesAdapter.submitList(result)
                }
            }

            //now search shared files
            model.sharedFiles.observe(viewLifecycleOwner) {
                when (it) {
                    is Response.Loading -> {

                    }
                    is Response.Success -> {
                        val sharedFiles : List<FileMetadata> = it.data
                        if (sharedFiles.isNotEmpty()) {
                            val result = mutableListOf<FileMetadata>()
                            for (file in sharedFiles) {
                                if (file.name.lowercase().contains(query.lowercase())) {
                                    result.add(file)
                                }
                            }
                            if (result.isNotEmpty()) {
                                found = true
                                mySharedFilesAdapter.submitList(result)
                            } else {
                                mySharedFilesAdapter.submitList(result)
                            }
                        } else {
                            mySharedFilesAdapter.submitList(sharedFiles)
                        }

                    }

                }
                if(found) {
                    dataBinding.emptyListPlaceholder.visibility = View.GONE
                } else {
                    dataBinding.emptyListPlaceholder.visibility = View.VISIBLE
                }
                dataBinding.progressBarPlaceHolder.visibility = View.GONE
            }

        }
    }
}