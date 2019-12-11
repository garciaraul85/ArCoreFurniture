package com.example.secondar.feature.menu.models

import com.google.gson.annotations.SerializedName

data class Products(@SerializedName("name")val name: String, @SerializedName("icon")val icon: String, @SerializedName("url")val url: String)