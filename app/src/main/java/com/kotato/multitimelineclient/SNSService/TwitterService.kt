package com.kotato.multitimelineclient.SNSService

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.kotato.multitimelineclient.AccountManage.Account
import com.kotato.multitimelineclient.TimeLine.TIME_LINE_TYPE
import com.kotato.multitimelineclient.TimeLine.TimeLineItem
import com.kotato.multitimelineclient.TimeLine.save
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import com.twitter.sdk.android.core.models.Media
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
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

object TwitterService : SNNService {


    val gson = Gson()
    /***
     * ログインユーザのホームタイムラインを取得
     * @param id
     */
    override fun getTimeLine(id: Long?) = async(CommonPool) {
        var timeLineItems: List<TimeLineItem> = listOf()
        val activeSession = TwitterCore.getInstance().sessionManager.activeSession
        if (activeSession != null) {
            val appClient = TwitterApiClient(activeSession)
            val call = appClient.statusesService.homeTimeline(null, id, null, false, null, null, null)
//            Log.d("Call Twitter TImeLine", id?.toString())
            try {
                val result = call?.execute()
                //エラーだとボディがからになる？
                if(result?.body() != null){
                    timeLineItems = result.body().map {
                        TimeLineItem(it.id, it.user.id, it.user.name, it.text, it.user.profileImageUrlHttps,
                                it.extendedEntities?.media?.filter { it.type == "photo" }?.map { it.mediaUrlHttps }, activeSession.userId,
                                TIME_LINE_TYPE.HOME.id)
                    }
                    timeLineItems.save()
                }else{
                    Log.d("Call Twitter TImeLine", result?.errorBody()?.string())
                }
            }catch (e: IOException){
                e.printStackTrace()
            }

        }
        return@async timeLineItems
    }

    /***
     * ログインユーザへのメンションを取得
     * @param callback
     *
     */
    override fun getMentions(callback: (List<TimeLineItem>) -> Unit) = async(CommonPool) {
        var timeLineItems: List<TimeLineItem> = listOf()
        val activeSession = TwitterCore.getInstance().sessionManager.activeSession
        if (activeSession != null) {
            val appClient = TwitterApiClient(activeSession)
            val call = appClient.statusesService.mentionsTimeline(null, null, null, null, null, null)
          try {
                val result = call?.execute()
                //エラーだとボディがからになる？
            if(result?.body() != null){
                timeLineItems = result.body().map {
                        TimeLineItem(it.id, it.user.id, it.user.name, it.text, it.user.profileImageUrlHttps,
                                it.extendedEntities?.media?.filter { it.type == "photo" }?.map { it.mediaUrlHttps }, activeSession.userId, TIME_LINE_TYPE.MENTION.id)
                    }
                    callback.invoke(timeLineItems)
                }else{
                    Log.d("Call Twitter Mention", result?.errorBody()?.string())
                }
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
        return@async timeLineItems
    }

    /**
     * ログインユーザのファボリストを取得
     * @param callback
     */
    fun getFavoriteList(callback: (List<TimeLineItem>) -> Unit) = async(CommonPool) {
        var timeLineItems: List<TimeLineItem> = listOf()
        val activeSession = TwitterCore.getInstance().sessionManager.activeSession
        if (activeSession != null) {
            val appClient = TwitterApiClient(activeSession)
            val call = appClient.favoriteService.list(null, null, null, null, null, null)

            var latch = CountDownLatch(1)
            call?.enqueue(object : Callback<List<Tweet>>() {
                override fun success(result: Result<List<Tweet>>) {
                    Log.d("Get Mentions Success", gson.toJson(result.data))
                    timeLineItems = result.data.map {
                        it ->
                        TimeLineItem(it.id, it.user.id, it.user.name, it.text, it.user.profileImageUrlHttps,
                                it.extendedEntities?.media?.filter { it.type == "photo" }?.map { it.mediaUrlHttps }, activeSession.userId, TIME_LINE_TYPE.FAVORITE.id)
                    }

                    callback.invoke(timeLineItems)
                    latch.countDown()
                }

                override fun failure(exception: TwitterException) {
                    Log.e("Get Timeline Failur", exception.toString())
                    latch.countDown()

                }


            })


            try {
                latch.await()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        return@async timeLineItems
    }

    /**
     * HTTPを通して画像の取得
     */
    override fun getImage(urlStr: String, callback: (Bitmap?) -> Unit) = async(CommonPool) {
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
    override fun getUserInfo(callback: (Account?) -> Unit) = async(CommonPool) {
        var account: Account? = null
        val activeSession = TwitterCore.getInstance().sessionManager.activeSession
        if (activeSession != null) {
            val twitterApiClient = TwitterApiClient(activeSession)
            val statusesService = twitterApiClient.accountService
            val call = statusesService?.verifyCredentials(false, false, false)
            //TODO CountDownLatchでラッピングしてasync/awaitを使うか そのままコールバックにするか
            var latch = CountDownLatch(1)
            call?.enqueue(object : Callback<User>() {
                override fun success(result: Result<User>) {
                    Log.i("Users", result.data.profileImageUrl)
                    val user = result.data
                    account = Account(user.id.toString(), user.name, user.email, 0, null, null, true, user.profileImageUrlHttps, gson.toJson(activeSession))
                    callback(account)
                    latch.countDown()
                }

                override fun failure(exception: TwitterException) {
                    latch.countDown()
                }
            })
            try {
                latch.await()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        return@async account
    }

    override fun authlize(loginButton: View, callback: (Any) -> Unit) = async(CommonPool) {
        if (loginButton is TwitterLoginButton) {
            loginButton.callback = object : Callback<TwitterSession>() {
                override fun success(result: Result<TwitterSession>) {
                    TwitterCore.getInstance().sessionManager.activeSession = result.data
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
            val twitterApiClient = TwitterApiClient(TwitterCore.getInstance().sessionManager.activeSession)
            val mediaService = twitterApiClient.mediaService
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
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        return@async mediaStr
    }


}


