package com.capstone.nfc.ui.shared
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nfc.R
import com.capstone.nfc.data.FileMetadata

class SharedFileViewAdapter(private val onClick: (FileMetadata) -> Unit) : ListAdapter<FileMetadata, SharedFileViewAdapter.ViewHolder>(FileDiffCallback) {
    class ViewHolder(view: View, val onClick: (FileMetadata) -> Unit) : RecyclerView.ViewHolder(view) {
        private val fileName: TextView = view.findViewById(R.id.file_name_field)
        private val fileIcon: ImageView = view.findViewById(R.id.file_type_icon)
        private var currentFile: FileMetadata? = null

        init {
            itemView.setOnClickListener {
                currentFile?.let {
                    onClick(it)
                }
            }
        }

        fun bind(file: FileMetadata) {
            currentFile = file
            fileName.text = file.name

            Log.e("test", file.type)
            with (file.type) {
                when {
                    equals("image/jpeg") -> fileIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context, R.drawable.ic_jpg))
                    equals("image/png") -> fileIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context, R.drawable.ic_png))
                    equals("application/pdf") -> fileIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context, R.drawable.ic_pdf))
                    equals("text/html") -> fileIcon.setImageDrawable(AppCompatResources.getDrawable(itemView.context, R.drawable.ic_html))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.component_file_card, parent, false)

        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object FileDiffCallback : DiffUtil.ItemCallback<FileMetadata>() {
    override fun areItemsTheSame(oldItem: FileMetadata, newItem: FileMetadata): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: FileMetadata, newItem: FileMetadata): Boolean {
        return oldItem.name == newItem.name && oldItem.accessors == newItem.accessors
    }
}