package com.example.secondar.feature.menu.data

import com.example.secondar.feature.network.ApiWorker
import com.example.secondar.feature.network.Constants
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


object ApiService {
    private val TAG = "--ApiService"

    // get request builder
    fun userApiCall() = Retrofit.Builder()
            .baseUrl(Constants.API_BASE_PATH)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ApiWorker.gsonConverter)
            .client(ApiWorker.client)
            .build()
            .create(MenuRequestInterface::class.java)!!
}