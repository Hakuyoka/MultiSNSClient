package com.kotato.multitimelineclient.TimeLine

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kotato.multitimelineclient.Media.MediaTabbedActivity
import com.kotato.multitimelineclient.R
import com.mopub.volley.RequestQueue


/**
 * Created by kotato on 2017/07/06.
 */


class TimeLineAdapter(context: Context, val queue: RequestQueue) : ArrayAdapter<TimeLineItem>(context, 0) {
    private val MAX_BITMAP_SIZE = 500

    val resource = R.layout.time_line_layout

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view: View = if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            inflater.inflate(resource, parent, false)
        } else {
            convertView
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
//        ImageLoadManger.addImageQue(queue, ImageQue(item.userIcon, userIcon, R.drawable.twitter_logo_white_on_blue))
        Glide.with(context)
                .load(item.userIcon)
                .apply(RequestOptions().fitCenter().placeholder(R.drawable.twitter_logo_white_on_blue))
                .into(userIcon)

        val layout = view.findViewById<View>(R.id.media_container) as LinearLayout
        //ListViewは使い回されるので、一度リセット
        layout.removeAllViews()

        item.media?.let {
            media ->
            val mediaUrls = media.urls
            //画像の枚数に応じてグリッドレイアウトを変更
            val gridLayout = GridLayout(context).apply {
                layoutParams = GridLayout.LayoutParams(parent?.layoutParams).apply {
                    setGravity(Gravity.CENTER)
                    useDefaultMargins = true
                    rowCount = if (mediaUrls.size > 2) 2 else 1
                    columnCount = if (mediaUrls.size > 1) 2 else 1
                }
                layout.addView(this)
            }

            //画面幅から計算
            val maxWidth: Int = parent?.width ?: 1
            val viewWidth = maxWidth * 0.9 / gridLayout.columnCount
            val maxHeight = parent?.height ?: 1
            val viewHeight = maxHeight * 0.9 / gridLayout.columnCount
            //画像拡大Viewに渡すURIの配列
            var uris = arrayListOf<String>()
            uris.addAll(mediaUrls)
            uris.map {
                uri ->
                var imageView = ImageView(context).apply {
                    adjustViewBounds = true
                    this.maxHeight = viewHeight.toInt()
                    this.maxWidth = viewWidth.toInt()
                    scaleType = ImageView.ScaleType.FIT_XY

                    setOnClickListener {
                        val intent = Intent(context, MediaTabbedActivity::class.java)
                        intent.putExtra("uris", uris)
                        intent.putExtra("type", media.type)
                        context.startActivity(intent)
                    }
                }
                gridLayout.addView(imageView)
                Glide.with(context)
                        .load(uri)
                        .apply(RequestOptions().override(MAX_BITMAP_SIZE).fitCenter())
                        .into(imageView)
            }
        }

        return view
    }


    fun add(id: Long, userId: Long, userName: String, text: String, userIcon: String, type: Int) {
        val item = TimeLineItem(id, userId, userName, text, userIcon, type = type)
        super.add(item)
    }

    fun remove(index: Int) {
        if (index < 0 || index >= count) {
            return
        }
        remove(getItem(index))
    }


    fun removeAll() {
        for (i in count - 1 downTo 0) {
            remove(getItem(i))
        }
    }

    fun getAll(): List<TimeLineItem> {
        return (0..count - 1).map {
            getItem(it)
        }
    }

}

