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

import com.kotato.multitimelineclient.R

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
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("click item", " $p2 $p3")
                adapter?.remove(p2)
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
