package com.capstone.nfc.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.data.Response.*
import com.capstone.nfc.data.StorageFile
import com.capstone.nfc.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment: BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {
    private val model by viewModels<DashboardViewModel>()
    private lateinit var myFilesAdapter: FileViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUser()
        setSignOutCallback()

        // Setup files list
        dataBinding.myFilesList.apply {
            layoutManager = LinearLayoutManager(activity?.applicationContext)

            val onClick = { file: StorageFile ->
                val action = DashboardFragmentDirections.actionDashboardToFileManagementFragment(file)
                findNavController().navigate(action)
            }

            val onLongClick = { file: StorageFile ->
                val action = DashboardFragmentDirections.actionDashboardToWriterActivity(file)
                findNavController().navigate(action)
            }
            myFilesAdapter = FileViewAdapter(onClick, onLongClick)
            adapter = myFilesAdapter
        }

        model.getMyFiles().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                myFilesAdapter.submitList(it)
                dataBinding.emptyListPlaceholder.visibility = View.GONE
            }
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

    private fun setSignOutCallback() {
        dataBinding.signOutButton.setOnClickListener {
            model.signOut().observe(viewLifecycleOwner) {}
        }
    }

    fun refresh() {
        model.loadMyFiles()
    }
}