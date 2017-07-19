package com.kotato.multitimelineclient.TimeLine

import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.kotato.multitimelineclient.*
import com.kotato.multitimelineclient.Media.MediaTabbedActivity
import com.mopub.volley.RequestQueue
import com.mopub.volley.toolbox.ImageLoader
import android.view.Display
import android.widget.GridLayout.LayoutParams


/**
 * Created by kotato on 2017/07/06.
 */
class TimeLineAdapter(context: Context,val queue: RequestQueue) : ArrayAdapter<TimeLineItem>(context, 0) {
    private val MAX_BITMAP_SIZE = 500

    val resource = R.layout.time_line_layout

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



        val userIcon = view.findViewById<View>(R.id.user_icon) as ImageView
        ImageLoadManger.addImageQue(queue, ImageQue(item.userIcon, userIcon, R.drawable.twitter_logo_white_on_blue))

        val layout = view.findViewById<View>(R.id.media_container) as LinearLayout

        //ListViewは使い回されるので、一度リセット
        layout.removeAllViews()
        if(item.mediaUrls != null && item.mediaUrls.isNotEmpty()){
            //画像の枚数に応じてグリッドレイアウトを変更
            val gridLayout = GridLayout(context)
            gridLayout.layoutParams = GridLayout.LayoutParams(GridLayout.spec(1), GridLayout.spec(1))
            gridLayout.rowCount = if (item.mediaUrls.size > 2) 2 else 1
            gridLayout.columnCount = if (item.mediaUrls.size > 1) 2 else 1
            gridLayout.useDefaultMargins = true
            layout.addView(gridLayout)

            //画面幅から計算
            val maxWidth: Int = parent?.width ?: 1
            val viewWidth = maxWidth * 0.9 / gridLayout.columnCount
            val maxHeight = parent?.height ?: 1
            val viewHeight = maxHeight * 0.9 / gridLayout.columnCount
            //画像拡大Viewに渡すURIの配列
            var uris = arrayListOf<String>()
            uris.addAll(item.mediaUrls?.toList())

            item.mediaUrls?.map {
                it ->
                val imageView = ImageView(context)
                imageView.adjustViewBounds = true
                imageView.maxWidth = viewWidth.toInt()
                imageView.maxHeight = viewHeight.toInt()
                imageView.scaleType = ImageView.ScaleType.FIT_XY

//                layout.addView(imageView)
                gridLayout.addView(imageView)
                imageView.setOnClickListener {
                    view ->
                        val intent = Intent(context, MediaTabbedActivity::class.java)
                        intent.putExtra("uris",uris)
                        context.startActivity(intent)
                }

                ImageLoadManger.addImageQue(queue, ImageQue(it, imageView), MAX_BITMAP_SIZE, MAX_BITMAP_SIZE)
            }
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


    fun removeAll() {
        for(i in count-1 downTo 0){
            remove(getItem(i))
        }
    }

    fun getAll(): List<TimeLineItem> {
        return (0..count-1).map {
            getItem(it)
        }
    }

}

