package com.kotato.multitimelineclient.TimeLine

import android.app.ListFragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.kotato.multitimelineclient.AccountManage.AccountListAdapter

import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.Service.TwitterService
import com.mopub.volley.RequestQueue
import com.mopub.volley.toolbox.Volley

class TimeLineItemFragment : ListFragment(){

    var adapter :TimeLineAdapter? = null

    override fun onStart() {
        super.onStart()

        if(adapter == null){
            //アダプターの初期化
            adapter = TimeLineAdapter(activity, Volley.newRequestQueue(activity), fragmentManager)
            listAdapter = adapter
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_time_line_item, container, false)
        val swipeContainer = view?.findViewById<View>(R.id.swipe_container) as SwipeRefreshLayout
        swipeContainer.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener{
            Log.d("Refresh Start", "tarttttt")
            TwitterService.getTimeLine {
                swipeContainer.isRefreshing = false
                adapter?.removeAll()
                adapter?.addAll(it)
            }
        })
        return view
    }

    fun addAll(timelineItems: List<TimeLineItem>){
        Log.d("Add TimeLine", timelineItems.size.toString())
        adapter?.addAll(timelineItems)
    }


    fun removeAll(){
        adapter?.removeAll()
    }


}
