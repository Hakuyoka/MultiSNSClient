package com.kotato.multitimelineclient.TimeLine

import android.app.ListFragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.kotato.multitimelineclient.AccountManage.AccountListAdapter

import com.kotato.multitimelineclient.R

class TimeLineItemFragment : ListFragment(){

    var adapter :TimeLineAdapter? = null

    override fun onStart() {
        super.onStart()

        if(adapter == null){
            //アダプターの初期化
            adapter = TimeLineAdapter(activity)
            listAdapter = adapter

            for (i in 1..10){
                adapter?.add(i.toLong(),i.toLong(), "aaaaaaaaaaa", "testetseteststtstestes", i.toString())
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_time_line_item, container, false)
    }
}
