package com.kotato.multitimelineclient.TimeLine
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import com.kotato.multitimelineclient.AccountManage.getAccountList
import com.kotato.multitimelineclient.Input.InputActivity
import com.kotato.multitimelineclient.R
import com.twitter.sdk.android.core.TwitterCore

class TimeLineActivity : AppCompatActivity() {
    val SUBMIT_CODE = 100
    var listFragment = TimeLineItemFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line)

        //ツールバー
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        val options = BitmapFactory.Options().apply {
            inMutable = true
        }
        val accountList = getAccountList()
        val targetAccount = accountList.find { (if(it.id == "") 0L else it.id?.toLong()) ==  TwitterCore.getInstance().sessionManager.activeSession.userId}
        var bitmap = BitmapFactory.decodeFile(filesDir.path + "/" + targetAccount?.id + "_" + targetAccount?.type + ".png",options)
        bitmap.density = 240
        val drawable = BitmapDrawable(resources,bitmap)
        toolbar.navigationIcon = drawable
        setSupportActionBar(toolbar)

        //投稿ボタン
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            val intent =  Intent(this, InputActivity::class.java)
            startActivityForResult(intent, SUBMIT_CODE)
        }

        //タイムライン
        fragmentManager.beginTransaction()
                .add(R.id.time_line_fragment_container, listFragment, "time_line_fragment_main")
                .commit()

        //タブ
        val tabLayOut = findViewById(R.id.tab) as TabLayout
        tabLayOut.addOnTabSelectedListener(TabListener(listFragment).apply { onTabSelected(tabLayOut.getTabAt(0)) })

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation,menu)
        return super.onCreateOptionsMenu(menu)
    }



}
