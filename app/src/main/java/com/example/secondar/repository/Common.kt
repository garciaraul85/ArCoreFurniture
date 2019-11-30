package com.example.secondar.repository

import com.example.secondar.R
import com.example.secondar.models.Product
import java.util.ArrayList

object Common {
    private val ASSET_3D01 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/bathrooms/sink1/sink01.gltf"
    private val ASSET_3D02 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/bathrooms/shower1/shower01.gltf"
    private val ASSET_3D03 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/beds/bed1/bed01.gltf"
    private val ASSET_3D04 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/beds/bed4/bed04.gltf"
    private val ASSET_3D05 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/cases/bookshelf1/bookshelf01.gltf"
    private val ASSET_3D06 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/cases/bookcase2/bookcase2.gltf"
    private val ASSET_3D07 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/tables/table1/table01.gltf"
    private val ASSET_3D08 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/tables/table2/table02.gltf"

    fun getBathroomsList(): ArrayList<Product> {
        val productsList = ArrayList<Product>()
        val product0 = Product("Table", R.drawable.table, ASSET_3D01)
        productsList.add(product0)
        val product1 = Product("BookShelf", R.drawable.bookshelf, ASSET_3D02)
        productsList.add(product1)
        return productsList
    }

    fun getBedsList(): ArrayList<Product> {
        val productsList = ArrayList<Product>()
        val product2 = Product("Lamp", R.drawable.lamp, ASSET_3D03)
        productsList.add(product2)
        val product3 = Product("Old Tv", R.drawable.odltv, ASSET_3D04)
        productsList.add(product3)
        return productsList
    }

    fun getCasesList(): ArrayList<Product> {
        val productsList = ArrayList<Product>()
        val product4 = Product("Cloth Dryer", R.drawable.clothdryer, ASSET_3D05)
        productsList.add(product4)
        val product5 = Product("Chair", R.drawable.chair, ASSET_3D06)
        productsList.add(product5)
        return productsList
    }

    fun getChairsList(): ArrayList<Product> {
        val productsList = ArrayList<Product>()
        val product4 = Product("Cloth Dryer", R.drawable.clothdryer, ASSET_3D07)
        productsList.add(product4)
        val product5 = Product("Chair", R.drawable.chair, ASSET_3D08)
        productsList.add(product5)
        return productsList
    }
}
