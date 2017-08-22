package com.kotato.multitimelineclient.AccountManage

import android.app.ListFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.google.gson.Gson
import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.SNSService.TwitterService
import com.kotato.multitimelineclient.TimeLine.TimeLineActivity
import com.kotato.multitimelineclient.model.Account
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient


class AccountListFragment(accounts: List<Account>) : ListFragment() {

    val gson = Gson()

    val adapter : AccountListAdapter by lazy {
        AccountListAdapter(activity).apply {
            addAll(accounts)
        }
    }

    override fun onStart() {
        super.onStart()

        listAdapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { p0, p1, index, p3 ->
            Log.d("click item", " $index $p3")
            //最初オブジェクトそのまま保存する必要があるかと思ったけど、
            //これでいけるっぽい
            //内部のシリアライザーがGson使ってるだけだった。
            val session = gson.fromJson(adapter.getItem(index).twitterSession, TwitterSession::class.java)
            if(session != null){
                TwitterCore.getInstance().sessionManager.activeSession = session
                TwitterService.getUserInfo {  }
                println(Gson().toJson(session))

                val intent = Intent(activity, TimeLineActivity::class.java)
                startActivity(intent)

            }else{
                val cliant = TwitterAuthClient()
                cliant.authorize(activity, object : Callback<TwitterSession>(){
                    override fun success(result: Result<TwitterSession>?) {
                        val intent = Intent(activity, TimeLineActivity::class.java)
                        startActivity(intent)
                    }

                    override fun failure(exception: TwitterException?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.custom_list_view, container, false)
    }

    fun addItem(accunt: Account) {
        adapter.add(accunt)
    }

    fun addAll(accunts: List<Account>) {
        adapter.addAll(accunts)
    }

    fun replaceItem(account: Account, index: Int) {
        adapter.remove(index)
        adapter.insert(account,index)
    }
}
