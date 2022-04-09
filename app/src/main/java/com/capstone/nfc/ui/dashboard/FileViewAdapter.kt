package com.capstone.nfc.ui.dashboard

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nfc.R
import com.capstone.nfc.data.StorageFile
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File
import java.net.URL


class FileViewAdapter(private val onClick: (StorageFile) -> Unit, private val onLongClick: (StorageFile) -> Unit) : ListAdapter<StorageFile, FileViewAdapter.ViewHolder>(FileDiffCallback) {
    class ViewHolder(view: View, val onClick: (StorageFile) -> Unit, val onLongClick: (StorageFile) -> Unit) : RecyclerView.ViewHolder(view) {
        private val fileName: TextView = view.findViewById(R.id.file_name_field)
        private val fileIcon: ImageView = view.findViewById(R.id.file_type_icon)
        private val filePreview: ImageView = view.findViewById(R.id.file_preview)
        private var currentFile: StorageFile? = null

        init {
            itemView.setOnClickListener {
                currentFile?.let {
                    onClick(it)
                }
            }
            itemView.setOnLongClickListener {
                currentFile?.let {
                    onLongClick(it)
                }

                return@setOnLongClickListener true
            }
        }

        fun bind(file: StorageFile) {
            currentFile = file
            fileName.text = file.name

            // File Icon
            with (file.type) {
                when {
                    equals("image/jpeg") -> fileIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context, R.drawable.ic_jpg))
                    equals("image/png") -> fileIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context, R.drawable.ic_png))
                    equals("application/pdf") -> fileIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context, R.drawable.ic_pdf))
                    equals("text/html") -> fileIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context, R.drawable.ic_html))
                }
            }

            // File Preview
            if (file.type == "application/pdf") {
                downloadPdfFromInternet(file.downloadUrl, itemView.context.cacheDir.absolutePath, file.name)
            } else {
                // .placeholder(R.drawable.user_placeholder)
                // .error(R.drawable.user_placeholder_error)
                Picasso.get().load(file.downloadUrl).fit().centerCrop().into(filePreview)
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
                        filePreview.setImageBitmap(pdfToBitmap(downloadedFile))
                        filePreview.scaleType = ImageView.ScaleType.CENTER_CROP
                    }

                    override fun onError(error: com.downloader.Error?) {
                        Toast.makeText(
                            itemView.context,
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.component_file_card, parent, false)

        return ViewHolder(view, onClick, onLongClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object FileDiffCallback : DiffUtil.ItemCallback<StorageFile>() {
    override fun areItemsTheSame(oldItem: StorageFile, newItem: StorageFile): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: StorageFile, newItem: StorageFile): Boolean {
        return oldItem.name == newItem.name
    }
}