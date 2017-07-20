package com.kotato.multitimelineclient.TimeLine

import android.app.ListFragment
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.SNSService.TwitterService
import com.mopub.volley.toolbox.Volley
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

class TimeLineItemFragment : ListFragment(){

    val adapter :TimeLineAdapter by lazy {
        TimeLineAdapter(activity, Volley.newRequestQueue(activity))
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
            async(UI){
                val timeLine = TwitterService.getTimeLine()
                val insertTimeLine = margeTimeLine(timeLine.await())
                insertTimeLine.forEach {
                    adapter.insert(it,0)
                }
                swipeContainer.isRefreshing = false
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
