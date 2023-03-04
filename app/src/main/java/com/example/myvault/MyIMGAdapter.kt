package com.example.myvault

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyIMGAdapter(private val userList:ArrayList<userData>):RecyclerView.Adapter<MyIMGAdapter.MyviewHolder>()    {
    class MyviewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imgName: TextView = itemView.findViewById(R.id.imgNameR)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyviewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.imglist,parent,false)
        return MyviewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {
        holder.imgName.text = userList[position].imgName
    }
}