package com.kotato.multitimelineclient.TimeLine

import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.graphics.Bitmap
import android.text.Layout
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kotato.multitimelineclient.R
import com.mopub.volley.RequestQueue
import com.mopub.volley.toolbox.ImageLoader

/**
 * Created by kotato on 2017/07/06.
 */
val IMAGE_CHACH_MAX_SIZE : Int = 10 * 1024 * 1024
class TimeLineAdapter(context: Context, queue: RequestQueue, fragmentManager: FragmentManager) : ArrayAdapter<TimeLineItem>(context, 0) {
    val resource = R.layout.time_line_layout
    val imageLoader : ImageLoader = ImageLoader(queue, MyImageCache(IMAGE_CHACH_MAX_SIZE))
    val fragmentManager = fragmentManager

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view : View
        // テンプレート処理。
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(resource, parent, false)
        } else {
            view = convertView
        }

        // データをgetItemで取る
        val item = getItem(position)

        // カスタムビューの場合はViewが確実にあるtry-catch は不要ためか。
        val id = view.findViewById<View>(R.id.id) as TextView
        id.text = item.id.toString()
        val name = view.findViewById<View>(R.id.name) as TextView
        name.text = item.userName

        val text = view.findViewById<View>(R.id.text) as TextView
        text.text = item.text



        val snnIcon = view.findViewById<View>(R.id.user_icon) as ImageView

        //cancel
        val imageContainer = snnIcon.tag

        if(imageContainer != null && imageContainer is ImageLoader.ImageContainer){
            imageContainer.cancelRequest();
        }
        //ユーザのアイコン
        val imageListener = ImageLoader.getImageListener(snnIcon, R.drawable.twitter_logo_white_on_blue, R.drawable.twitter_logo_white_on_blue)
        imageLoader.get(item.userIcon, imageListener)

        Log.d("has Text", item.text)

        val layout = view.findViewById<View>(R.id.media_container) as LinearLayout

        //ListViewは使い回されるので、一度リセット
        layout.removeAllViews()
        var count = 1
        item.mediaUrls?.map {
            it ->
                Log.d("has Media" + count, it)

                val imageView = ImageView(context)
                layout.addView(imageView)

                //cancel
                val imageContainer = imageView.tag
                if(imageContainer != null && imageContainer is ImageLoader.ImageContainer){
                    imageContainer.cancelRequest()
                }
                val imageListener = ImageLoader.getImageListener(imageView, R.color.tw__seekbar_thumb_outer_color, R.color.tw__seekbar_thumb_outer_color)
                imageLoader.get(it, imageListener)
        }

        return view
    }


    fun add(id: Long, userId:Long, userName: String, text: String, userIcon: String) {
        val item = TimeLineItem(id, userId, userName, text, userIcon)
        super.add(item)
    }

    fun remove(index: Int) {
        if (index < 0 || index >= count) {
            return
        }
        remove(getItem(index))
    }

}


class MyImageCache(maxSize: Int) : ImageLoader.ImageCache{
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