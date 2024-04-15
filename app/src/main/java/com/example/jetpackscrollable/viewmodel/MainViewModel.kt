package com.example.jetpackscrollable.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.runtime.*
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackscrollable.R
import com.example.jetpackscrollable.model.PhotoResponse
import com.example.jetpackscrollable.network.RetrofitClient
import com.example.jetpackscrollable.repository.PhotoRepository
import com.example.jetpackscrollable.state.CommonState
import com.example.jetpackscrollable.state.Status
import com.example.jetpackscrollable.util.BitmapCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


@SuppressLint("StaticFieldLeak")
class MainViewModel(private var context: Context) : ViewModel() {
    private var _listOfPhotoBitmap = arrayListOf<Bitmap>()
    private var _currentPage by mutableStateOf(1)
    private val repository = PhotoRepository(RetrofitClient.create())
    private val _currentState = MutableStateFlow(CommonState<PhotoResponse>(Status.LOADING, null, null))
    private val defaultMaxItem = 10

    fun getCurrentState() = _currentState
    fun getCurrentPage() = _currentPage
    fun getListOfPhotoBitmap() = _listOfPhotoBitmap

    fun getAllPhotos(page: Int = 1) {
        _currentState.value = CommonState.loading()

        viewModelScope.launch {
            repository.getPhoto(page)
                .catch {
                    _currentState.value = CommonState.failed(it.message.toString())
                }
                .collect {
                    if(it.data.isNullOrEmpty()) {
                        _currentState.value = CommonState.failed("Response success but empty result")
                    } else {
                        it.data.mapIndexed { index, photo ->
                            val realIndex = if(_currentPage > 1) (_currentPage * defaultMaxItem) + index else index
                            val bitmapPhoto = getLastBitmapCached(realIndex, photo.urls.regular)

                            _listOfPhotoBitmap.add(bitmapPhoto.second!!)
                        }
                        _currentState.value = CommonState.success(it.data)
                        _currentPage += 1
                    }
                }
        }
    }

    private suspend fun getLastBitmapCached(position: Int, imageUrl: String?): Pair<Int, Bitmap?> {
        val bitmapCache = BitmapCache()
        val bitmapKey = "BITMAP_KEY"

        return if(getListOfPhotoBitmap().isNotEmpty() && getListOfPhotoBitmap().size > position) {
            bitmapCache.addBitmapToMemoryCache(bitmapKey, getListOfPhotoBitmap()[position])

            if(bitmapCache.getBitmapFromMemCache(bitmapKey) == null) {
                convertUrlToBitmap(position, imageUrl)
            } else {
                Pair(position, bitmapCache.getBitmapFromMemCache(bitmapKey))
            }

        } else {
            convertUrlToBitmap(position, imageUrl)
        }
    }
    private suspend fun convertUrlToBitmap(position: Int, imageUrl: String?) : Pair<Int, Bitmap?> {
        var bitmap: Bitmap?
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                bitmap = BitmapFactory.decodeStream(input)

                input.close()
                connection.disconnect()

                return@withContext Pair(position, bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext Pair(position, drawableToBitmap(ResourcesCompat.getDrawable(context.resources, R.drawable.ic_broken_image, null)!!))
            }
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}
