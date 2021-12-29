package com.capstone.nfc.ui.writer

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.fragment.app.viewModels
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.databinding.FragmentWriterBinding
import com.capstone.nfc.services.NFCHCEService

class WriterFragment: BaseFragment<FragmentWriterBinding>(FragmentWriterBinding::inflate) {
    private val viewModel by viewModels<WriterViewModel>()
    override fun onResume() {
        super.onResume()
        context?.packageManager?.setComponentEnabledSetting(ComponentName(requireContext(), NFCHCEService::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
    }

    override fun onPause() {
        super.onPause()
        context?.packageManager?.setComponentEnabledSetting(ComponentName(requireContext(), NFCHCEService::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
    }
}