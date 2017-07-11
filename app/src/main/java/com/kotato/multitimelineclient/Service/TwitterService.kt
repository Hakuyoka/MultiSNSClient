package com.kotato.multitimelineclient.Service

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import com.kotato.multitimelineclient.AccountManage.Account
import com.kotato.multitimelineclient.AccountManage.AccountsMangeActivity
import com.kotato.multitimelineclient.TimeLine.TimeLineItem
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import java.io.File
import java.io.IOException
import java.util.concurrent.CountDownLatch

/**
 * Created by kotato on 2017/07/11.
 */

object TwitterService: SNNService{

    var twitterApiClient: TwitterApiClient? = null

    override fun getTimeLine(callback : (List<TimeLineItem>) -> Unit){
        val activeSession = TwitterCore.getInstance().sessionManager.activeSession
        if(activeSession != null){
            val appClient = TwitterApiClient(activeSession)
            val call = appClient.statusesService.homeTimeline(null,null,null, false, null, null, null)
            call?.enqueue(object : Callback<List<Tweet>>() {
                override fun success(result: Result<List<Tweet>>) {
                    Log.d("Get Timeline Success", result.data.toString())
                    val timeLineItems = result.data.map {
                        it ->
                        TimeLineItem(it.id, it.user.id, it.user.name, it.text, it.user.profileImageUrlHttps,
                                it.entities?.media?.filter { it -> it.type == "photo" }?.map { it -> it.mediaUrlHttps })
                    }
                    callback.invoke(timeLineItems)
                }

                override fun failure(exception: TwitterException) {
                }
            })

        }
    }

    /**
     * HTTPを通して画像の取得
     */
    override fun getImage(urlStr: String, callback:(Bitmap?) -> Unit) {
        async(CommonPool){
            println("start getImage")
            try {
                val request = Request.Builder().url(urlStr).build()
                val response = OkHttpClient().newCall(request).execute()
                val bitmap = BitmapFactory.decodeStream(response.body()?.byteStream())
                callback(bitmap)
            } catch(e: IOException) {
                e.printStackTrace()
            } catch(e: JSONException) {
                e.printStackTrace()
            }

            println("end getImage")
        }
    }


    /**
     * 認証されているユーザ情報を取得する
     */
    override fun getUserInfo(callback:(Account?) -> Unit){
        val activeSession = TwitterCore.getInstance().sessionManager.activeSession
        if(activeSession != null) {
            twitterApiClient = TwitterApiClient(TwitterCore.getInstance().sessionManager.activeSession)
            val statusesService = twitterApiClient?.accountService
            val call = statusesService?.verifyCredentials(false, false, false)
            call?.enqueue(object : Callback<User>() {
                override fun success(result: Result<User>) {
                    Log.i("Users", result.data.profileImageUrl)
                    val user = result.data
                    callback(Account(user.toString(),user.name,user.email,0,null,null,true,user.profileImageUrlHttps))
                }
                override fun failure(exception: TwitterException) {

                }
            })
        }
    }

    override fun authlize(loginButton: View, callback: (Any) -> Unit){
        if(loginButton is TwitterLoginButton){
            loginButton.callback = object : Callback<TwitterSession>() {
                override fun success(result: Result<TwitterSession>){
                    TwitterCore.getInstance().sessionManager.activeSession =  result.data
                }

                override fun failure(exception: TwitterException) {
                }
            }

        }

    }
}