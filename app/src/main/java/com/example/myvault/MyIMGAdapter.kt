package com.example.myvault

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyIMGAdapter(private val userList:ArrayList<userData>,
                   private val context: imgFrag
):RecyclerView.Adapter<MyIMGAdapter.MyviewHolder>()    {

    private lateinit var pListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: onItemClickListener){
        pListener = listener
    }

    class MyviewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView){
        val imgView: ImageView = itemView.findViewById(R.id.imageS)
        val imgName: TextView = itemView.findViewById(R.id.imgNameR)
        val imgUrl: TextView = itemView.findViewById(R.id.imgUrlR)
        init{
            itemView.setOnClickListener {
            listener.onItemClick(adapterPosition)
    }
}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyviewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.imglist,parent,false)
        return MyviewHolder(itemView,pListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {
//        val imgg = userList[position]
//        holder.imgView.setImageResource(imgg.image!!)
        Glide.with(context).load(userList[position].image).into(holder.imgView)
        holder.imgName.text = userList[position].imgName
        holder.imgUrl.text = userList[position].image
    }
}