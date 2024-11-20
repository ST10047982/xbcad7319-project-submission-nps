package com.xbcad.xbcad7319_physiotherapyapp.ui

import android.content.Context

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {


    private const val BASE_URL = "https://us-central1-opscice-task4.cloudfunctions.net/api/"

    private var retrofit: Retrofit? = null

    fun getRetrofitInstance(context: Context): Retrofit {
        if (retrofit == null) {

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getService(context: Context): ApiService {
        return getRetrofitInstance(context)
            .create(ApiService::class.java)
    }
}




