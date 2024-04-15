package com.example.jetpackscrollable.util

import android.graphics.Bitmap
import android.util.LruCache

class BitmapCache {
    private val memoryCache: LruCache<String?, Bitmap>
    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        val cacheSize = maxMemory / 8
        memoryCache = object : LruCache<String?, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    fun addBitmapToMemoryCache(key: String?, bitmap: Bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap)
        }
    }

    fun getBitmapFromMemCache(key: String?): Bitmap? {
        return memoryCache[key]
    }
}
