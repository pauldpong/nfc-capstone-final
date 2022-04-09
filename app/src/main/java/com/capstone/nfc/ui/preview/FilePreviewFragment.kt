package com.capstone.nfc.ui.preview

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.data.Response.*
import com.capstone.nfc.databinding.FragmentPreviewBinding
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

private const val TAG = "FilePreviewFragment"

    @AndroidEntryPoint
class FilePreviewFragment: BaseFragment<FragmentPreviewBinding>(FragmentPreviewBinding::inflate) {
    private val viewModel by viewModels<FilePreviewViewModel>()
    private val args: FilePreviewFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        PRDownloader.initialize(context)

        dataBinding.preview.settings.loadWithOverviewMode = true
        dataBinding.preview.settings.useWideViewPort = true

        viewModel.getFile(args.filePath).observe(viewLifecycleOwner) { response ->
            if (response is Success) {
                var file_url : String = response.data.toString()
                Log.i(TAG, file_url)
                var pdf : Boolean = file_url.contains(".pdf")
                if (pdf) {
                    dataBinding.preview.visibility = View.GONE
                    dataBinding.pdfView.visibility = View.VISIBLE
                    context?.let { getRootDirPath(it) }
                        ?.let { downloadPdfFromInternet(file_url, it, "placeholder") }
                    dataBinding.preview.loadUrl(file_url)
                } else {
                    dataBinding.pdfView.visibility = View.GONE
                    dataBinding.preview.visibility = View.VISIBLE
                    dataBinding.preview.loadUrl(file_url)
                }

            }
        }
    }



    private fun downloadPdfFromInternet(url: String, dirPath: String, fileName: String) {
        PRDownloader.download(
            url,
            dirPath,
            fileName
        ).build()
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Toast.makeText(getContext(), "downloadComplete", Toast.LENGTH_LONG)
                        .show()
                    val downloadedFile = File(dirPath, fileName)
                    showPdfFromFile(downloadedFile)
                }

                override fun onError(error: com.downloader.Error?) {
                    Toast.makeText(
                        context,
                        "Error in downloading file : $error",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

            })
    }

    private fun showPdfFromFile(file: File) {
        dataBinding.pdfView.fromFile(file)
            .password(null)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .onPageError { page, _ ->
                Toast.makeText(
                    getContext(),
                    "Error at page: $page", Toast.LENGTH_LONG
                ).show()
            }
            .load()
    }

    fun getRootDirPath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file: File = ContextCompat.getExternalFilesDirs(
                context.applicationContext,
                null
            )[0]
            file.absolutePath
        } else {
            context.applicationContext.filesDir.absolutePath
        }
    }
}