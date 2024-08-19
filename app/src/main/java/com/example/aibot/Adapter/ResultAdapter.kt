package com.example.aibot.Adapter

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.aibot.Message
import com.example.aibot.R

class ResultAdapter(private val messages: MutableList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_REQUEST = 0
        private const val TYPE_RESPONSE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isRequest) TYPE_REQUEST else TYPE_RESPONSE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_REQUEST -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.request_adapter_design, parent, false)
                RequestViewHolder(view)
            }

            TYPE_RESPONSE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.response_adapter_design, parent, false)
                ResponseViewHolder(view)
            }

            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position].message
        if (holder is RequestViewHolder) {
            holder.bind(message)
        } else if (holder is ResponseViewHolder) {
            holder.bind(message)
        }

    }

    inner class RequestViewHolder(view: View) : ViewHolder(view) {
        val txtRequest: TextView = view.findViewById(R.id.txtRequest)
        fun bind(message: String) {
            txtRequest.text = message
        }
    }

    inner class ResponseViewHolder(view: View) : ViewHolder(view) {
        val txtResponse: TextView = view.findViewById(R.id.txtResponse)
        fun bind(message: String) {
            txtResponse.text = message
        }
    }


}