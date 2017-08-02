package com.kotato.multitimelineclient

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import com.mopub.volley.RequestQueue
import com.mopub.volley.toolbox.ImageLoader

/**
 *
 * 作ったけどGlideのがいい感じなので乗り換えようと思う
 * Created by kotato on 2017/07/19.
 */

//状態持っちゃってる
object ImageLoadManger {

    val IMAGE_CACHE_MAX_SIZE: Int = 10 * 1024 * 1024
    private val cache = MyImageCache(IMAGE_CACHE_MAX_SIZE)
    private val loaderMap = mutableMapOf<RequestQueue, ImageLoader>()

    fun addImageQue(requestQueue: RequestQueue, imageQue: ImageQue, width: Int = 0, height: Int = 0) {

        val imageLoader = if (loaderMap.containsKey(requestQueue)) {
            loaderMap[requestQueue]
        } else {
            ImageLoader(requestQueue, cache).apply {
                loaderMap.put(requestQueue, this)
            }
        }

        //cancel
        val imageContainer = imageQue.imageView.tag

        if (imageContainer != null && imageContainer is ImageLoader.ImageContainer) {
            imageContainer.cancelRequest();
        }
        val imageListener = ImageLoader.getImageListener(imageQue.imageView, imageQue.resourceId, imageQue.resourceId)
        imageLoader?.get(imageQue.uri, imageListener, width, height)
    }
}


class MyImageCache(maxSize: Int) : ImageLoader.ImageCache {
    val memoryCache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(maxSize) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.rowBytes * value.height
        }
    }

    override fun getBitmap(url: String?): Bitmap? {
        Log.d("getBitmap", url)
        return memoryCache.get(url)
    }

    override fun putBitmap(url: String?, bitmap: Bitmap?) {
        Log.d("putBitmap", url)
        memoryCache.put(url, bitmap)
    }
}

data class ImageQue(val uri: String, val imageView: ImageView, val resourceId: Int = R.color.tw__composer_black)