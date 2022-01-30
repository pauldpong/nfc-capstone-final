package com.capstone.nfc.ui.preview

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.data.Response.*
import com.capstone.nfc.databinding.FragmentPreviewBinding
import dagger.hilt.android.AndroidEntryPoint
import android.content.ActivityNotFoundException

import android.content.Intent
import android.net.Uri
import android.util.Log


@AndroidEntryPoint
class FilePreviewFragment: BaseFragment<FragmentPreviewBinding>(FragmentPreviewBinding::inflate) {
    private val viewModel by viewModels<FilePreviewViewModel>()
    private val args: FilePreviewFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.preview.settings.loadWithOverviewMode = true
        dataBinding.preview.settings.useWideViewPort = true

        viewModel.getFile(args.filePath).observe(viewLifecycleOwner) { response ->
            if (response is Success) {
//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.setDataAndType(Uri.parse(response.data.toString()), "application/pdf")
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                val newIntent = Intent.createChooser(intent, "Open File")
//                try {
//                    startActivity(newIntent)
//                } catch (e: ActivityNotFoundException) {
//                    // Instruct the user to install a PDF reader here, or something
//                }
                dataBinding.preview.loadUrl(response.data.toString())
            }
        }
    }
}