package com.example.myvault

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyPDFAdapter(private val userList:ArrayList<userData>):RecyclerView.Adapter<MyPDFAdapter.MyviewHolder>()    {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    class MyviewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView){
        val pdfName: TextView = itemView.findViewById(R.id.pdfNameR)

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyviewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pdflist,parent,false)
        return MyviewHolder(itemView,mListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {
        holder.pdfName.text = userList[position].pdfName
    }
}