package com.example.secondar.feature.menu.models

import com.google.gson.annotations.SerializedName

data class MenuResponse(
        @SerializedName("category")
        var files: List<Category>
)