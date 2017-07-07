package com.kotato.multitimelineclient

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.JsonObject
import com.kotato.multitimelineclient.AccountManage.AccountsMangeActivity
import com.kotato.multitimelineclient.TimeLine.TimeLineActivity
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.tweetui.UserTimeline
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException


var client = OkHttpClient()

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val config = TwitterConfig.Builder(this)
                .logger(DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(TwitterAuthConfig("RzvmQcIgzVaBszQyqqiIs7vLU", "z5StIYBhxZI6pYvL88dIYlX3U5rswRKJNqWupM5OrwQAbJAUmS"))
                .debug(true)
                .build()
        Twitter.initialize(config)
//        Twitter.initialize(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun goAccountMange(view: View){
        val intent =  Intent(this, AccountsMangeActivity::class.java)
        startActivity(intent)

    }

    fun sendRequest(view: View){
//        val res = object: AsyncTask<Void, Void, String>() {
//            override fun doInBackground(vararg params: Void): String? {
//                var res: String = ""
//                try {
//                    res = run("https://api.twitter.com/1.1/users/show.json?screen_name=boys_surface")
//                    val resJson = JSONObject(res)
//                    Log.i("MainActivity", resJson.toString())
//                } catch(e: IOException) {
//                    e.printStackTrace()
//                } catch(e: JSONException) {
//                    e.printStackTrace()
//                }
//                return res
//            }
//        }.execute()

        Log.d("AsyncTest","start")
        runBlocking {
            Log.d("AsyncTest","start in")
            com.kotato.multitimelineclient.getUserInfo {
                tweet ->
                Log.d("AsyncTest",tweet.text)
            }.await()
            Log.d("AsyncTest","end in")
        }
        Log.d("AsyncTest","end")
        
    }

    fun run(url: String): String {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body()?.string() ?: ""
    }


    fun getUserImage(view: View) : Deferred<Unit> =  async(CommonPool){
        val result = object: AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void): String {
                var res: String = ""
                try {
                    val request = Request.Builder().url("https://images.contentful.com/hspc7zpa5cvq/nsolid-enterprise-node-finally-norsolid/8cdf234194282a75a7c07f9e9d5f7806/nsolid-enterprise-node-finally-norsolid.png").build()
                    val response = client.newCall(request).execute()
                    val filePath = filesDir.path + "/test.png"
                    val outStream = File(filePath).absoluteFile.outputStream()
                    val bitmap = BitmapFactory.decodeStream(response.body()?.byteStream())
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                } catch(e: IOException) {
                    e.printStackTrace()
                } catch(e: JSONException) {
                    e.printStackTrace()
                }

                return res
            }
        }.execute()
        return@async
    }

    fun getUserInfo(callback:(Tweet) -> Unit): Deferred<Unit?> = async(CommonPool){
        Log.d("AsyncTest","start getUserInfo")
        val twitterApiClient = TwitterApiClient(OkHttpClient())
        val statusesService = twitterApiClient?.statusesService
        val call = statusesService?.show(100L, false, false, false)
            call?.enqueue(object : Callback<Tweet>() {
                override fun success(result: Result<Tweet>) {
                    Log.d("AsyncTest",""+result.data.text)
                    callback.invoke(result.data)
                }

                override fun failure(exception: TwitterException) {
                    Log.d("AsyncTest","errrrrrrr")
                }
            })

        Log.d("AsyncTest","end getUserInfo")
        return@async

    }

    fun goTimeLine(view: View){
        val intent =  Intent(this, TimeLineActivity::class.java)
        startActivity(intent)

    }




}
