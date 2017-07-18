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

    val adapter :TimeLineAdapter by lazy {
        TimeLineAdapter(activity, Volley.newRequestQueue(activity), fragmentManager)
    }


    override fun onStart() {
        super.onStart()
        listAdapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_time_line_item, container, false)
        val swipeContainer = view?.findViewById<View>(R.id.swipe_container) as SwipeRefreshLayout
        swipeContainer.setOnRefreshListener({
            Log.d("Refresh Start",view.id.toString())
            TwitterService.getTimeLine {
                if(it != null){
                    val insertTimeLine = margeTimeLine(it)
                    swipeContainer.isRefreshing = false
                    insertTimeLine.forEach {
                        adapter.insert(it,0)
                    }
                }
            }
        })
        return view
    }

    fun margeTimeLine(timelineItems: List<TimeLineItem>): List<TimeLineItem>{
        val itemIDs = mutableListOf<Long>()

        if (adapter.count > 0){
            (0 .. adapter.count - 1)
                    .map { adapter.getItem(it) }
                    .mapTo(itemIDs) { it.id }
            return timelineItems.filter { !itemIDs.contains(it.id) }
        }

        return ArrayList(0)
    }


    fun addAll(timelineItems: List<TimeLineItem>){
        Log.d("Add TimeLine", timelineItems.size.toString())
        adapter.addAll(timelineItems)
    }


    fun removeAll(){
        adapter.removeAll()
    }


    fun getAll():List<TimeLineItem>{
        return adapter.getAll()
    }


}
