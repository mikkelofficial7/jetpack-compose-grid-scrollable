package com.example.jetpackscrollable.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private lateinit var retrofit: Retrofit
    private const val BASE_URL = "https://api.unsplash.com/"
    private const val CLIENT_ID = "L42mK8rdn5hxA1AzldSIdc0gvBU4xCw-JT8GhDJMHfI"

    private fun getInstance(): Retrofit {
        if (!::retrofit.isInitialized) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }

    internal fun create() : ApiClient {
        return getInstance().create(ApiClient::class.java)
    }

    internal fun getClientId() = CLIENT_ID
}