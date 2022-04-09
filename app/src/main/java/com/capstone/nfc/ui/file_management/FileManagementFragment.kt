package com.capstone.nfc.ui.file_management

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.nfc.base.BaseFragment
import com.capstone.nfc.data.Response
import com.capstone.nfc.databinding.FragmentFileManagementBinding
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class FileManagementFragment: BaseFragment<FragmentFileManagementBinding>(FragmentFileManagementBinding::inflate) {
    private val viewModel by viewModels<FileManagementViewModel>()
    private lateinit var accessorsAdapter: FileAccessorsAdapter
    private val args: FileManagementFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.fileNameField.text = args.file.name

        if (args.file.type == "application/pdf") {
            context?.let {
                downloadPdfFromInternet(args.file.downloadUrl, it.cacheDir.absolutePath, args.file.name)
            }
        } else {
            // .placeholder(R.drawable.user_placeholder)
            // .error(R.drawable.user_placeholder_error)
            Picasso.get().load(args.file.downloadUrl).fit().centerCrop().into(dataBinding.filePreview)
        }

        viewModel.loadFileMetadata(args.file.uuid)
        viewModel.getFileMetadata().observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    accessorsAdapter.submitList(it.data.accessors)
                }
            }
        }

        dataBinding.accessors.apply {
            layoutManager = LinearLayoutManager(activity?.applicationContext)

            val onClick: (String) -> Unit = { uid: String ->
                viewModel.revokeAccess(args.file.uuid, uid).observe(viewLifecycleOwner) {
                    when (it) {
                        is Response.Success -> {
                            viewModel.loadFileMetadata(args.file.uuid)
                        }
                    }
                }
            }

            accessorsAdapter = FileAccessorsAdapter(onClick)
            adapter = accessorsAdapter
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
                    val downloadedFile = File(dirPath, fileName)
                    dataBinding.filePreview.setImageBitmap(pdfToBitmap(downloadedFile))
                }

                override fun onError(error: com.downloader.Error?) {
                    Toast.makeText(
                        context,
                        "Error in downloading file : $error",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
    }

    private fun pdfToBitmap(pdfFile: File): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val renderer =
                PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
            val pageCount = renderer.pageCount
            if (pageCount > 0) {
                val page = renderer.openPage(0)
                bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                renderer.close()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return bitmap
    }
}