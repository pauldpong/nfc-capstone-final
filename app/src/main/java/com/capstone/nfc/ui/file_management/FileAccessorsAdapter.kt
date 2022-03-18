package com.capstone.nfc.ui.file_management

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nfc.R
import com.capstone.nfc.data.FileMetadata
import com.capstone.nfc.ui.shared.SharedFileViewAdapter

class FileAccessorsAdapter(private val onClick: (String) -> Unit) : ListAdapter<String, FileAccessorsAdapter.ViewHolder>(AccessorDiffCallback) {
    class ViewHolder(view: View, val onClick: (String) -> Unit) : RecyclerView.ViewHolder(view) {
        private val accessorId: TextView = view.findViewById(R.id.accessor_id)
        private var uid: String? = null

        init {
            itemView.setOnClickListener {
                uid?.let { onClick(it) }
            }
        }

        fun bind(id: String) {
            uid = id
            accessorId.text = uid
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.component_accessor_card, parent, false)

        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object AccessorDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}