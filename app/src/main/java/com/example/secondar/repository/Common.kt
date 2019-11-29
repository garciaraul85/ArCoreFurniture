package com.example.secondar.repository

import com.example.secondar.R
import com.example.secondar.models.Product
import java.util.ArrayList

object Common {
    private val ASSET_3D01 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/tables/table3/table03.gltf"
    private val ASSET_3D02 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/kitchens/dishwasher2/dishwasher02.gltf"
    private val ASSET_3D03 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/kitchens/kitchen2/kitchen02.gltf"
    private val ASSET_3D04 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/kitchens/kitchen3/kitchen03.gltf"
    private val ASSET_3D05 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/kitchens/stove1/stove01.gltf"
    private val ASSET_3D06 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/tables/cornertable1/cornertable01.gltf"
    private val ASSET_3D07 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/tables/table1/table01.gltf"
    private val ASSET_3D08 = "https://raw.githubusercontent.com/garciaraul85/ArCoreFurniture/master/app/items/tables/table2/table02.gltf"

    fun getProductsList(): ArrayList<Product> {
        val productsList = ArrayList<Product>()
        val product0 = Product("Table", R.drawable.table, ASSET_3D01)
        productsList.add(product0)
        val product1 = Product("BookShelf", R.drawable.bookshelf, ASSET_3D02)
        productsList.add(product1)
        val product2 = Product("Lamp", R.drawable.lamp, ASSET_3D03)
        productsList.add(product2)
        val product3 = Product("Old Tv", R.drawable.odltv, ASSET_3D04)
        productsList.add(product3)
        val product4 = Product("Cloth Dryer", R.drawable.clothdryer, ASSET_3D05)
        productsList.add(product4)
        val product5 = Product("Chair", R.drawable.chair, ASSET_3D06)
        productsList.add(product5)
        val product6 = Product("Chair", R.drawable.chair, ASSET_3D07)
        productsList.add(product6)
        val product7 = Product("Chair", R.drawable.chair, ASSET_3D08)
        productsList.add(product7)

        return productsList
    }
}
