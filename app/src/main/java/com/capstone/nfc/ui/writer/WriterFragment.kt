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
import android.graphics.BitmapFactory
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WriterFragment: BaseFragment<FragmentWriterBinding>(FragmentWriterBinding::inflate) {
    private val viewModel by viewModels<WriterViewModel>()
    private val args: WriterFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.filePathField.text = args.fileUUID

        val rc = Intent(activity, NFCHCEService::class.java)
        rc.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) //important for Android 10
        rc.putExtra("NFCHCEService.fileUUID", args.fileUUID)
        context?.startService(rc)
    }
}