package com.kotato.multitimelineclient.AccountManage

import android.app.ListFragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson

import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.Service.TwitterService
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.models.User


class AccountListFragment(accunts: List<Account>) : ListFragment() {
    val accounts = accunts

    var adapter : AccountListAdapter? = null

    init {

    }

    override fun onStart() {
        super.onStart()

        if(adapter == null){
            //アダプターの初期化
            adapter = AccountListAdapter(activity)
            listAdapter = adapter
            adapter?.addAll(accounts)
        }

        listView.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                Log.d("click item", " $index $p3")
                val id = adapter?.getItem(index)?.id
                if(id != null ){
                    val session = TwitterService.getSession(id.toLong())
                    if(session != null){
                        TwitterCore.getInstance().sessionManager.activeSession = session
                        TwitterService.getUserInfo {  }
                        println(Gson().toJson(session))
                    }else{
                        val cliant = TwitterAuthClient()
                        cliant.authorize(activity,session)
                    }

                }

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.custom_list_view, container, false)
    }

    fun addItem(accunt: Account) {
        adapter?.add(accunt)
    }

    fun addAll(accunts: List<Account>) {
        adapter?.addAll(accunts)
    }
}
