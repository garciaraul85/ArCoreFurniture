package com.example.secondar.models

import com.google.ar.core.Anchor

data class Product(var textNames: String = "", var imagesPath: String = "", var modelsName: String = "") {
    lateinit var anchor: Anchor
}