package com.app.xbcad7319_physiotherapyapp.ui

import android.content.Context

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {


    private const val BASE_URL = "https://us-central1-opscice-task4.cloudfunctions.net/api/"

    private var retrofit: Retrofit? = null

    fun getRetrofitInstance(context: Context): Retrofit {
        if (com.app.xbcad7319_physiotherapyapp.ui.ApiClient.retrofit == null) {

            com.app.xbcad7319_physiotherapyapp.ui.ApiClient.retrofit = Retrofit.Builder()
                .baseUrl(com.app.xbcad7319_physiotherapyapp.ui.ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return com.app.xbcad7319_physiotherapyapp.ui.ApiClient.retrofit!!
    }

    fun getService(context: Context): com.app.xbcad7319_physiotherapyapp.ui.ApiService {
        return com.app.xbcad7319_physiotherapyapp.ui.ApiClient.getRetrofitInstance(context)
            .create(com.app.xbcad7319_physiotherapyapp.ui.ApiService::class.java)
    }
}




