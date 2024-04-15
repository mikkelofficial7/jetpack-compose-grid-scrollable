package com.example.jetpackscrollable.network

import com.example.jetpackscrollable.model.PhotoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {
    @GET("photos")
    suspend fun getAllPhotos(
        @Query("client_id") clientId: String = RetrofitClient.getClientId(),
        @Query("page") page: Int = 1,
        @Query("per_page") itemPage: Int = 10
    ): PhotoResponse
}