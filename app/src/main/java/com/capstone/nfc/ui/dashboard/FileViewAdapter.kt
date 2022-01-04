package com.capstone.nfc.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nfc.R
import com.capstone.nfc.data.File

class FileViewAdapter(private val onClick: (File) -> Unit) : ListAdapter<File, FileViewAdapter.ViewHolder>(FileDiffCallback) {
    class ViewHolder(view: View, val onClick: (File) -> Unit) : RecyclerView.ViewHolder(view) {
        private val fileName: TextView = view.findViewById(R.id.file_name_field)
        private var currentFile: File? = null

        init {
            itemView.setOnClickListener {
                currentFile?.let {
                    onClick(it)
                }
            }
        }

        fun bind(file: File) {
            currentFile = file
            fileName.text = file.name
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

object FileDiffCallback : DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.name == newItem.name
    }
}