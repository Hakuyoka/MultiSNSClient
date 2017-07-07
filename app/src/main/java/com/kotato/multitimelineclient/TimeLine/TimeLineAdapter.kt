package com.kotato.multitimelineclient.TimeLine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kotato.multitimelineclient.R

/**
 * Created by kotato on 2017/07/06.
 */
class TimeLineAdapter(context: Context) : ArrayAdapter<TimeLineItem>(context, 0) {
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

        val snnIcon = view.findViewById<View>(R.id.user_icon) as ImageView
        val iconImage : Int = when(0){
            0 ->  R.drawable.twitter_logo_white_on_blue
            1 ->  R.drawable.mastodon_logo
            else -> 0
        }
        snnIcon.setImageResource(iconImage)

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
