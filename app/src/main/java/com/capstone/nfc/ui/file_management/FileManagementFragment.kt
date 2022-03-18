package com.capstone.nfc.ui.file_management

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.data.Response
import com.capstone.nfc.databinding.FragmentFileManagementBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FileManagementFragment: BaseFragment<FragmentFileManagementBinding>(FragmentFileManagementBinding::inflate) {
    private val viewModel by viewModels<FileManagementViewModel>()
    private lateinit var accessorsAdapter: FileAccessorsAdapter
    private val args: FileManagementFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFileMetadata(args.filePath).observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    accessorsAdapter.submitList(it.data.accessors)
                }
            }
        }

        dataBinding.accessors.apply {
            layoutManager = LinearLayoutManager(activity?.applicationContext)

            val onClick: (String) -> Unit = { uid: String ->
                viewModel.revokeAccess(args.filePath, uid).observe(viewLifecycleOwner) {
                    when (it) {
                        is Response.Success -> {
                            //TODO
                        }
                    }
                }
            }

            accessorsAdapter = FileAccessorsAdapter(onClick)
            adapter = accessorsAdapter
        }
    }
}