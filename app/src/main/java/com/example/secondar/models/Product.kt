package com.example.secondar.models

import com.google.ar.core.Anchor

data class Product(var textNames: String = "", var imagesPath: Int = -1, var modelsName: String = "") {
    lateinit var anchor: Anchor
}