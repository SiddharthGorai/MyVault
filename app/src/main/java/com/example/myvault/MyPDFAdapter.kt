package com.example.myvault

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyPDFAdapter(private val userList:ArrayList<userData>):RecyclerView.Adapter<MyPDFAdapter.MyviewHolder>()    {
    class MyviewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val pdfName: TextView = itemView.findViewById(R.id.pdfNameR)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyviewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pdflist,parent,false)
        return MyviewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {
        holder.pdfName.text = userList[position].pdfName
    }
}