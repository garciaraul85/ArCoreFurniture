package com.example.secondar

import com.example.secondar.models.Product
import java.util.ArrayList

object Common {
    private val ASSET_3D = "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf"

    fun getProductsList(): ArrayList<Product> {
        val productsList = ArrayList<Product>()
        val product0: Product = Product("Table", R.drawable.table, "table.sfb")
        productsList.add(product0)
        val product1: Product = Product("BookShelf", R.drawable.bookshelf, "model.sfb")
        productsList.add(product1)
        val product2: Product = Product("Lamp", R.drawable.lamp, "lamp.sfb")
        productsList.add(product2)
        val product3: Product = Product("Old Tv", R.drawable.odltv, "tv.sfb")
        productsList.add(product3)
        val product4: Product = Product("Cloth Dryer", R.drawable.clothdryer, "cloth.sfb")
        productsList.add(product4)
        val product5: Product = Product("Chair", R.drawable.chair, ASSET_3D)
        productsList.add(product5)

        return productsList
    }
}
