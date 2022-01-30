package com.capstone.nfc.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nfc.R
import com.capstone.nfc.data.StorageFile

class FileViewAdapter(private val onClick: (StorageFile) -> Unit, private val onLongClick: (StorageFile) -> Unit) : ListAdapter<StorageFile, FileViewAdapter.ViewHolder>(FileDiffCallback) {
    class ViewHolder(view: View, val onClick: (StorageFile) -> Unit, val onLongClick: (StorageFile) -> Unit) : RecyclerView.ViewHolder(view) {
        private val fileName: TextView = view.findViewById(R.id.file_name_field)
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