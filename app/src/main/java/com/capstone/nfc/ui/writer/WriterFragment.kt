package com.capstone.nfc.ui.writer

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.databinding.FragmentWriterBinding
import com.capstone.nfc.services.NFCHCEService
import android.content.Intent

class WriterFragment: BaseFragment<FragmentWriterBinding>(FragmentWriterBinding::inflate) {
    private val viewModel by viewModels<WriterViewModel>()
    private val args: WriterFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.filePathField.text = args.filePath

//        val rc = Intent(context, NFCHCEService::class.java)
//        rc.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) //important for Android 10
//        rc.putExtra("parama", args.filePath)
//        context?.startService(rc)
    }

    override fun onResume() {
        super.onResume()
        context?.packageManager?.setComponentEnabledSetting(ComponentName(requireContext(), NFCHCEService::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
    }

    override fun onPause() {
        super.onPause()
        context?.packageManager?.setComponentEnabledSetting(ComponentName(requireContext(), NFCHCEService::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
    }
}