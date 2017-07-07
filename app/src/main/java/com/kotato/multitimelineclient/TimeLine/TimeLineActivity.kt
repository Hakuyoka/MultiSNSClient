package com.kotato.multitimelineclient.TimeLine

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.kotato.multitimelineclient.AccountManage.AccountListFragment
import com.kotato.multitimelineclient.AccountManage.getAccountLisst

import com.kotato.multitimelineclient.R
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.TwitterCollection
import com.twitter.sdk.android.core.models.User
import com.twitter.sdk.android.tweetui.Timeline
import kotlinx.coroutines.experimental.runBlocking

class TimeLineActivity : AppCompatActivity() {
    var listFragment : TimeLineItemFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }



        listFragment = TimeLineItemFragment()
        fragmentManager.beginTransaction()
                .add(R.id.time_line_fragment_container, listFragment, "time_line_fragment_main")
                .commit()

        getTimeLine {
            Log.d("Timeline get", Gson().toJson(it.get(0)).toString())
            val timeLineItem :List<TimeLineItem> = it.map {
                it ->
                    TimeLineItem(it.id, it.user.id, it.user.name, it.text, "")
            }

            listFragment?.addAll(timeLineItem)

        }
    }

    fun getTimeLine(callback : (List<Tweet>) -> Unit){
        val activeSession = TwitterCore.getInstance().sessionManager.activeSession
        if(activeSession != null){
            val appClient = TwitterApiClient(activeSession)
            val call = appClient.statusesService.homeTimeline(null,null,null, true, true, true, true)
            call?.enqueue(object : Callback<List<Tweet>>() {
                override fun success(result: Result<List<Tweet>>) {
                    Log.d("Get Timeline Success", result.data.toString())
                    callback.invoke(result.data)
                }

                override fun failure(exception: TwitterException) {
                }
            })

        }
    }

}
