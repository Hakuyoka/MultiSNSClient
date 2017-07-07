package com.kotato.multitimelineclient

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import kotlinx.coroutines.experimental.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import java.io.File
import java.io.IOException

/**
 * Created by kotato on 2017/07/06.
 */


fun getUserInfo(callback:(Tweet) -> Unit): Deferred<Unit?> = async(CommonPool){
    println("start getUserInfo")
    val twitterApiClient = TwitterApiClient(OkHttpClient())
    val statusesService = twitterApiClient?.statusesService
    val call = statusesService?.show(100L, false, false, false)
    call?.enqueue(object : Callback<Tweet>() {
        override fun success(result: Result<Tweet>) {
            println(""+result.data.text)
            Log.i("Users", result.data.text)
            callback.invoke(result.data)
        }

        override fun failure(exception: TwitterException) {
            println("errrrrrrr")
        }
    })

    println("end getUserInfo")
    return@async

}

fun getUserImage(urlStr: String, callback:(Any?) -> Unit) : Deferred<Any?> =  async(CommonPool){
    println("start getUserImage")
    var response :Any? = null
    var res: String = ""
    try {
        println("connect Start" + urlStr)
        val request = Request.Builder().url(urlStr).build()
        response = OkHttpClient().newCall(request).execute()
        println("connect End:" + response.toString())
        callback(response)
    } catch(e: IOException) {
        e.printStackTrace()
    } catch(e: JSONException) {
        e.printStackTrace()
    }
    println("end getUserImage")
    return@async response
}

fun main(args: Array<String>) {
    println("start")
    getUserInfo{
        tweet ->
        print(tweet)
    }
//    runBlocking {
//        println("start closer")
//
//        val respose = getUserImage("http://qiita.com/k-kagurazaka@github/items/702c92bc3381af36db12"){
//            print("callback")
//        }.await()
//        println(respose)
//        println("end closer")
//
//    }
    println("end")

    Thread.sleep(100000)
}
