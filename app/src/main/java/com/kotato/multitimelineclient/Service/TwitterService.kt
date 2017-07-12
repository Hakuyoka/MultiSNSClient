package com.kotato.multitimelineclient.Service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import com.kotato.multitimelineclient.AccountManage.Account
import com.kotato.multitimelineclient.TimeLine.TimeLineItem
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import com.twitter.sdk.android.core.models.Media
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import java.io.File
import java.io.IOException
import java.util.concurrent.CountDownLatch


/**
 * Created by kotato on 2017/07/11.
 */

object TwitterService: SNNService{

    var twitterApiClient: TwitterApiClient? = null
    val sessionList: MutableMap<Long,TwitterSession> = mutableMapOf()

    override fun getTimeLine(callback : (List<TimeLineItem>?) -> Unit) = async(CommonPool){
        var timeLineItems: List<TimeLineItem>? = null
        val activeSession = TwitterCore.getInstance().sessionManager.activeSession
        if(activeSession != null){
            val appClient = TwitterApiClient(activeSession)
            val call = appClient.statusesService.homeTimeline(null,null,null, false, null, null, null)
            call?.enqueue(object : Callback<List<Tweet>>() {
                override fun success(result: Result<List<Tweet>>) {
                    Log.d("Get Timeline Success", result.data.toString())
                    timeLineItems = result.data.map {
                        it ->
                        TimeLineItem(it.id, it.user.id, it.user.name, it.text, it.user.profileImageUrlHttps,
                                it.extendedEntities?.media?.filter { it -> it.type == "photo" }?.map { it -> it.mediaUrlHttps })
                    }
                    callback.invoke(timeLineItems)
                }

                override fun failure(exception: TwitterException) {
                }
            })
        }
        return@async timeLineItems
    }

    /**
     * HTTPを通して画像の取得
     */
    override fun getImage(urlStr: String, callback:(Bitmap?) -> Unit) = async(CommonPool) {
        println("start getImage")
        var bitmap: Bitmap? = null
        try {
            val request = Request.Builder().url(urlStr).build()
            val response = OkHttpClient().newCall(request).execute()
            bitmap = BitmapFactory.decodeStream(response.body()?.byteStream())
            callback(bitmap)
        } catch(e: IOException) {
            e.printStackTrace()
        } catch(e: JSONException) {
            e.printStackTrace()
        }
        println("end getImage")
        return@async bitmap
    }


    /**
     * 認証されているユーザ情報を取得する
     */
    override fun getUserInfo(callback:(Account?) -> Unit) = async(CommonPool){
        var account: Account? = null
        val activeSession = TwitterCore.getInstance().sessionManager.activeSession
        if(activeSession != null) {
            twitterApiClient = TwitterApiClient(TwitterCore.getInstance().sessionManager.activeSession)
            val statusesService = twitterApiClient?.accountService
            val call = statusesService?.verifyCredentials(false, false, false)
            call?.enqueue(object : Callback<User>() {
                override fun success(result: Result<User>) {
                    Log.i("Users", result.data.profileImageUrl)
                    val user = result.data
                    account = Account(user.id.toString(),user.name,user.email,0,null,null,true,user.profileImageUrlHttps)
                    callback(account)
                }
                override fun failure(exception: TwitterException) {

                }
            })
        }

        return@async account
    }

    override fun authlize(loginButton: View, callback: (Any) -> Unit) = async(CommonPool){
        if(loginButton is TwitterLoginButton){
            loginButton.callback = object : Callback<TwitterSession>() {
                override fun success(result: Result<TwitterSession>){
                    TwitterCore.getInstance().sessionManager.activeSession =  result.data
                }

                override fun failure(exception: TwitterException) {
                }
            }

        }
        return@async
    }

    fun uploadMedia(uri: String, callback: (String?) -> Unit) = async(CommonPool) {
        val activeSession = TwitterCore.getInstance().sessionManager.activeSession
        var mediaStr: String? = null
        if (activeSession != null) {

            //TODO CountDownLatchでラッピングしてasync/awaitを使うか そのままコールバックにするか
            var latch = CountDownLatch(1)
            twitterApiClient = TwitterApiClient(TwitterCore.getInstance().sessionManager.activeSession)
            val mediaService = twitterApiClient?.mediaService
            val file = File(uri)
            val media = RequestBody.create(MediaType.parse("image/jpeg"), file.readBytes())
            mediaService?.upload(media, null, null)?.enqueue(object : Callback<Media>() {
                override fun success(result: Result<Media>) {
                    Log.i("Users", result.data.mediaIdString)
                    callback(result.data.mediaIdString)
                    mediaStr = result.data.mediaIdString
                    latch.countDown()
                }
                override fun failure(exception: TwitterException) {
                    latch.countDown()
                }
            })
            try {
                latch.await()
            }catch (e:InterruptedException){
                e.printStackTrace()
            }
        }

        return@async mediaStr
    }

    fun hasSession(id: Long): Boolean{
        return sessionList.containsKey(id)
    }

    fun getSession(id: Long): TwitterSession?{
        return sessionList.get(id)
    }

}
