package com.kotato.multitimelineclient.TimeLine

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import com.google.gson.Gson
import com.kotato.multitimelineclient.AccountManage.readAccountList
import com.kotato.multitimelineclient.Input.InputActivity

import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.SNSService.TwitterService
import com.twitter.sdk.android.core.TwitterCore
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

class TimeLineActivity : AppCompatActivity() {
    val SUBMIT_CODE = 100
    var listFragment : TimeLineItemFragment = TimeLineItemFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line)

        //ツールバー
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        val options = BitmapFactory.Options().apply {
            inMutable = true
        }
        val accountList = readAccountList(filesDir)
        val targetAccount = accountList.find { (if(it.id == "") 0L else it.id?.toLong()) ==  TwitterCore.getInstance().sessionManager.activeSession.userId}
        var bitmap = BitmapFactory.decodeFile(filesDir.path + "/" + targetAccount?.id + "_" + targetAccount?.type + ".png",options)
        bitmap.density = 240
        val drawable = BitmapDrawable(resources,bitmap)
        toolbar.navigationIcon = drawable
        setSupportActionBar(toolbar)

        //タブ
        val tabLayOut = findViewById(R.id.tab) as TabLayout
        tabLayOut.addOnTabSelectedListener(TabListener(listFragment))

        //投稿ボタン
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            val intent =  Intent(this, InputActivity::class.java)
            startActivityForResult(intent, SUBMIT_CODE)
        }

        //タイムライン
        fragmentManager.beginTransaction()
                .add(R.id.time_line_fragment_container, listFragment, "time_line_fragment_main")
                .commit()

        async(UI){
            val timeLine = TwitterService.getTimeLine()
            listFragment.addAll(timeLine.await())
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation,menu)
        return super.onCreateOptionsMenu(menu)
    }



}
