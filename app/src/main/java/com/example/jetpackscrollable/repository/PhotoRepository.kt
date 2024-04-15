package com.example.jetpackscrollable.repository

import com.example.jetpackscrollable.model.PhotoResponse
import com.example.jetpackscrollable.network.ApiClient
import com.example.jetpackscrollable.state.CommonState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PhotoRepository(private val apiService: ApiClient) {
    suspend fun getPhoto(page: Int): Flow<CommonState<PhotoResponse>> {
        return flow {
            val photos = apiService.getAllPhotos(page = page)
            emit(CommonState.success(photos))
        }.flowOn(Dispatchers.IO)
    }
}
