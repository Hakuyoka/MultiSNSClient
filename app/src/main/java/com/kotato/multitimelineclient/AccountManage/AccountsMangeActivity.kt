package com.kotato.multitimelineclient.AccountManage

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View

import com.kotato.multitimelineclient.R
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.audiofx.AcousticEchoCanceler
import android.os.AsyncTask
import com.kotato.multitimelineclient.client
import com.twitter.sdk.android.core.models.User
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import java.io.File
import java.io.IOException


class AccountsMangeActivity : AppCompatActivity() {

    var listFragment : AccountListFragment? = null
    private val SUB_ACTIVITY = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts_mange)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)

        val accountList = getAccountLisst(filesDir)
        Log.d("accounts", accountList.toString())

        listFragment = AccountListFragment(accountList)
        fragmentManager.beginTransaction()
                .add(R.id.list_fragment_container, listFragment, "fragment_main")
                .commit()

        val authClient = TwitterAuthClient()
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener(View.OnClickListener { view ->
            Log.d("Active Session Start", TwitterCore.getInstance().sessionManager.activeSession?.toString() ?: "null")
            authClient.cancelAuthorize()

            val intent = Intent(this, SelectService::class.java)
            startActivityForResult(intent, SUB_ACTIVITY)

            Log.d("Active Session End", TwitterCore.getInstance().sessionManager.activeSession?.toString() ?: "null")
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK && intent != null) {

            val account = intent.getSerializableExtra("Account") as Account?
            if(account != null){
                val fragment = fragmentManager.findFragmentByTag("fragment_main")
                if(fragment != null && fragment is AccountListFragment){
                    account.save(filesDir)
                    fragment.addItem(account)
                }
            }

        }
    }

}
