package com.kotato.multitimelineclient

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import java.io.File
import java.io.IOException

/**
 * Created by kotato on 2017/07/06.
 */

object AsyncModel {
    fun returnTenAsync() = async(CommonPool) {
        delay(1000)
        return@async 10
    }

    fun returnTwentyAsync() = async(CommonPool) {
        delay(2000)
        return@async 20
    }
}


fun getUserInfo(callback:(Tweet) -> Unit) = async(CommonPool){
    var resultstr:String = ""
    println("start getUserInfo")
    val twitterApiClient = TwitterApiClient(OkHttpClient())
    val statusesService = twitterApiClient?.statusesService
    val call = statusesService?.show(100L, false, false, false)
    call?.enqueue(object : Callback<Tweet>() {
        override fun success(result: Result<Tweet>) {
            println(""+result.data.text)
            Log.i("Users", result.data.text)
            resultstr = result.data.toString()
            callback.invoke(result.data)
        }

        override fun failure(exception: TwitterException) {
            println("errrrrrrr")
        }
    })

    println("end getUserInfo")
    return@async resultstr
}

fun getUserImage(urlStr: String, callback:(Any?) -> Unit) : Deferred<Any?> =  async(CommonPool){
    println("start getImage")
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
    println("end getImage")
    return@async response
}

fun main(args: Array<String>) = runBlocking<Unit>  {

//    getUserInfo{
//        tweet ->
//        print(tweet)
//    }
//    runBlocking {
//        println("start closer")
//
//        val respose = getImage("http://qiita.com/k-kagurazaka@github/items/702c92bc3381af36db12"){
//            print("callback")
//        }.await()
//        println(respose)
//        println("end closer")
//
//    }
//    println("start")
//    launch(CommonPool){
//        println(AsyncModel.returnTenAsync().await())
//        val respose = getUserInfo{
//            println("CallBack")
//        }.await()
//        println(respose)
//        println("GoGo")
//        println(AsyncModel.returnTwentyAsync().await())
//    }
//    println("end")
//
//    Thread.sleep(5000)
    val jobs = List(100_000) { // create a lot of coroutines and list their jobs
        launch(CommonPool) {
            delay(1000L)
            print(".")
        }
    }
    jobs.forEach { it.join() }
}