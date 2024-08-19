package com.example.aibot.Adapter.ViewModel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.aibot.Adapter.Modelclass.AIResponse
import com.example.aibot.Adapter.utill.OnItemClickListner
import com.example.aibot.Message
import com.example.aibot.R

class DrawerAdapter(
    private val messages: List<AIResponse>,
    private val listener: OnItemClickListner
) :
    RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtResponse: TextView = view.findViewById(R.id.txtResponse)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.drawer_adapter_design, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val message = messages[p1].request
        val resp = messages[p1]
        p0.txtResponse.text = message
        p0.itemView.setOnClickListener {
            listener.onItemClick(resp)

        }


    }
}

