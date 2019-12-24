package com.example.secondar.feature.menu.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.secondar.R
import com.example.secondar.feature.menu.models.Product
import com.squareup.picasso.Picasso

class ProductsAdapter(private val context: Context, private val productsList: MutableList<Product>, private val furniture: IFurniture)
    : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.with(context).load(productsList[position].imagesPath).into(holder.imageView)
        holder.textView.text = productsList[position].textNames
        holder.imageView.setOnClickListener { view -> furniture.onModelItemClick(productsList[position].modelsName) }
    }

    fun updateProductList(datas: MutableList<Product>) {
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