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
import com.kotato.multitimelineclient.model.Account
import java.util.*


/**
 * Created by kotato on 2017/07/04.
 */

class AccountListAdapter(context: Context) : ArrayAdapter<Account>(context, 0) {

    val resource = R.layout.account_item_layout

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view: View = if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            inflater.inflate(resource, parent, false)
        } else {
            convertView
        }

        // データをgetItemで取る
        getItem(position)?.let {
            item ->
            val id = view.findViewById<View>(R.id.id) as TextView
            id.text = item.id
            val name = view.findViewById<View>(R.id.name) as TextView
            name.text = item.name

            val snnIcon = view.findViewById<View>(R.id.snn_icon) as ImageView
            val iconImage: Int = when (item.type) {
                0 -> R.drawable.twitter_logo_white_on_blue
                1 -> R.drawable.mastodon_logo
                else -> 0
            }
            snnIcon.setImageResource(iconImage)


            val userIcon = view.findViewById<View>(R.id.user_icon) as ImageView
            userIcon.setImageURI(Uri.parse(context.filesDir.path + "/" + item.id + "_0" + ".png"))
        }

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