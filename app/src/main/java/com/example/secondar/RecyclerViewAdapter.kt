package com.example.secondar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.secondar.models.Product

import java.util.ArrayList
import android.R.attr.data
import android.text.method.TextKeyListener.clear



class RecyclerViewAdapter(private val productsList: ArrayList<Product>, private val furniture: IFurniture)
    : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(productsList[position].imagesPath)
        holder.textView.text = productsList[position].textNames
        holder.imageView.setOnClickListener { view -> furniture.onModelItemClick(productsList[position].modelsName) }
    }

    fun updateProductList(datas: ArrayList<Product>) {
        productsList.clear()
        productsList.addAll(datas)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var imageView: ImageView = itemView.findViewById(R.id.imageview)
        internal var textView: TextView = itemView.findViewById(R.id.text)
    }
}