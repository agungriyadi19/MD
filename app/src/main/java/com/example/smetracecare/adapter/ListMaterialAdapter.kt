package com.example.smetracecare.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.example.smetracecare.R
import com.example.smetracecare.data.MaterialDetail

class ListMaterialAdapter(private val materialList: List<MaterialDetail>) :
    RecyclerView.Adapter<ListMaterialAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val photo : ImageView = itemView.findViewById(R.id.iv_item_photo)
        val name: TextView = itemView.findViewById(R.id.tv_item_name)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: MaterialDetail)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val listMaterial = materialList[position]
        val bitmap = decodeBase64ToBitmap(listMaterial.image)
        if (bitmap != null) {
            Glide.with(holder.itemView.context)
                .load(bitmap)
                .into(holder.photo)
        } else {
            holder.photo.setImageResource(R.drawable.ic_launcher_background) // Gambar placeholder jika decoding gagal
        }
        holder.name.text = listMaterial.name
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(materialList[holder.adapterPosition])
        }
    }
    fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedString = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_story, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = materialList.size
}