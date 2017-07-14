package com.kotato.multitimelineclient.TimeLine

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import com.google.gson.Gson
import com.kotato.multitimelineclient.Input.InputActivity

import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.Service.TwitterService

class TimeLineActivity : AppCompatActivity() {
    val SUBMIT_CODE = 100
    var listFragment : TimeLineItemFragment = TimeLineItemFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            val intent =  Intent(this, InputActivity::class.java)
            startActivityForResult(intent, SUBMIT_CODE)
        }

        fragmentManager.beginTransaction()
                .add(R.id.time_line_fragment_container, listFragment, "time_line_fragment_main")
                .commit()

        TwitterService.getTimeLine {
            Log.d("Timeline get", Gson().toJson(it?.get(0)).toString())
            if (it != null) {
                listFragment.addAll(it)
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation,menu)
        return super.onCreateOptionsMenu(menu)
    }



}
