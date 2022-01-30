package com.capstone.nfc.ui.shared

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.data.FileMetadata
import com.capstone.nfc.data.StorageFile
import com.capstone.nfc.databinding.FragmentSharedBinding
import com.capstone.nfc.ui.dashboard.DashboardFragmentDirections
import com.capstone.nfc.ui.dashboard.DashboardViewModel
import com.capstone.nfc.ui.dashboard.FileViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SharedFragment: BaseFragment<FragmentSharedBinding>(FragmentSharedBinding::inflate) {
    private val model by viewModels<SharedViewModel>()
    private lateinit var myFilesAdapter: SharedFileViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.sharedFiles.apply {
            layoutManager = LinearLayoutManager(activity?.applicationContext)

            val onClick = { file: FileMetadata ->
                file.path.let {
                    val action = SharedFragmentDirections.actionSharedFragmentToFilePreviewFragment2(it)
                    findNavController().navigate(action)
                }
            }

            myFilesAdapter = SharedFileViewAdapter(onClick)
            adapter = myFilesAdapter
        }

        model.getSharedFiles().observe(viewLifecycleOwner) {
            myFilesAdapter.submitList(it)
        }
    }
}