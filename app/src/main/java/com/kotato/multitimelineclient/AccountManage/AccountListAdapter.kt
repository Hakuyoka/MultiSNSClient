package com.kotato.multitimelineclient.AccountManage

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kotato.multitimelineclient.R
import java.util.ArrayList
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import okhttp3.OkHttpClient
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URL


/**
 * Created by kotato on 2017/07/04.
 */

class AccountListAdapter(context: Context) : ArrayAdapter<Account>(context, 0){

    val resource = R.layout.account_item_layout

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
        id.text = item!!.id
        val name = view.findViewById<View>(R.id.name) as TextView
        name.text = item.name

        val snnIcon = view.findViewById<View>(R.id.snn_icon) as ImageView
        val iconImage : Int = when(item.type){
            0 ->  R.drawable.twitter_logo_white_on_blue
            1 ->  R.drawable.mastodon_logo
            else -> 0
        }
        snnIcon.setImageResource(iconImage)


        val userIcon = view.findViewById<View>(R.id.user_icon) as ImageView
        userIcon.setImageURI(Uri.parse(context.filesDir.path + "/" + item.id.toString() + "_0" + ".png"))
        return view
    }

    val itemList: ArrayList<Account>
        get() {
            val size = count
            val itemList = ArrayList<Account>(size)
            (0..size - 1).mapTo(itemList) { getItem(it) }
            return itemList
        }

    fun addAll(parcelableArrayList: ArrayList<Account>) {
        super.addAll(parcelableArrayList)
    }

    fun add(id: String, name: String, type: Int = 0) {
        val item = Account(id, name, type = type)
        super.add(item)
    }

    fun remove(index: Int) {
        if (index < 0 || index >= count) {
            return
        }
        remove(getItem(index))
    }


}