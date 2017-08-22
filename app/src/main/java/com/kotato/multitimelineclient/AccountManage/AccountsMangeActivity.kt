package com.kotato.multitimelineclient.AccountManage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.model.Account
import com.kotato.multitimelineclient.model.getAccountList
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.identity.TwitterAuthClient


class AccountsMangeActivity : AppCompatActivity() {

    val accountList
        get() = getAccountList()

    val listFragment: AccountListFragment by lazy {
        AccountListFragment(accountList)
    }

    private val SUB_ACTIVITY = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts_mange)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)

        fragmentManager.beginTransaction()
                .add(R.id.list_fragment_container, listFragment, "fragment_main")
                .commit()

        val authClient = TwitterAuthClient()
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener({ view ->
            Log.d("Active Session Start", TwitterCore.getInstance().sessionManager.activeSession?.toString() ?: "null")
            authClient.cancelAuthorize()

            val intent = Intent(this, SelectService::class.java)
            startActivityForResult(intent, SUB_ACTIVITY)

            Log.d("Active Session End", TwitterCore.getInstance().sessionManager.activeSession?.toString() ?: "null")
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        Log.d("finish account", " a a a a ")
        if (resultCode != Activity.RESULT_OK || intent == null) {
            return
        }

        val account = intent.getSerializableExtra("Account") as Account?
        val fragment = fragmentManager.findFragmentByTag("fragment_main") as? AccountListFragment

        account?.let {
            if (accountList.contains(account)) {
                fragment?.replaceItem(account, accountList.indexOf(account))
            } else {
                fragment?.addItem(account)
                account.save()
            }
        }

    }

}