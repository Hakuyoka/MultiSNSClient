package com.kotato.multitimelineclient

import android.Manifest
import android.app.ActionBar
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import com.google.gson.JsonObject
import com.kotato.multitimelineclient.AccountManage.*
import com.kotato.multitimelineclient.Push.ACTION_LOCAL_PUSH
import com.kotato.multitimelineclient.Push.NotificationReceiver
import com.kotato.multitimelineclient.Push.REQ_CODE
import com.kotato.multitimelineclient.Service.TwitterService
import com.kotato.multitimelineclient.TimeLine.TimeLineActivity
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.tweetui.UserTimeline
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.coroutines.experimental.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.CountDownLatch


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val config = TwitterConfig.Builder(this)
                .logger(DefaultLogger(Log.DEBUG))
                .debug(true)
                .build()
        Twitter.initialize(config)

//        Twitter.initialize(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_home_black_24dp)

        val options = BitmapFactory.Options().apply {
            inMutable = true
        }
        val bitmap = BitmapFactory.decodeFile(filesDir.path + "/" + "103595510_0" + ".png",options)
        val drawable = BitmapDrawable(resources,bitmap)
        toolbar.setNavigationIcon(drawable)
        setSupportActionBar(toolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation,menu)
        return super.onCreateOptionsMenu(menu)
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
//
//        Log.d("AsyncTest","start")
//        runBlocking {
//            Log.d("AsyncTest","start in")
//            com.kotato.multitimelineclient.getUserInfo {
//                tweet ->
//                Log.d("AsyncTest",tweet.text)
//            }.await()
//            Log.d("AsyncTest","end in")
//        }
//        Log.d("AsyncTest","end")
        val REQUEST_CODE = 1
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            // Android 6.0 のみ、該当パーミッションが許可されていない場合

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // パーミッションが必要であることを明示するアプリケーション独自のUIを表示
            }

        } else {
            // 許可済みの場合、もしくはAndroid 6.0以前
            // パーミッションが必要な処理
        }


        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent,1101)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && intent != null) {
            var uri: Uri? = null
            if (data != null) {
                uri = data?.getData()
                Log.d("Select Media", getPathFromUri(this, uri))
                val uriText = getPathFromUri(this, uri)
                if(uriText != null)
                    TwitterService.uploadMedia(uriText){

                    }
            }
        }
    }

    fun getUserInfo(callback:(Tweet) -> Unit) = async(CommonPool){
        var resultstr:String = ""
        println("start getUserInfo")
        val twitterApiClient = TwitterApiClient(OkHttpClient())
        val statusesService = twitterApiClient?.statusesService
        val call = statusesService?.show(100L, false, false, false)
        val latch = CountDownLatch(1)
        call?.enqueue(object : Callback<Tweet>() {
            override fun success(result: Result<Tweet>) {
                println(""+result.data.text)
                Log.i("Users", result.data.text)
                resultstr = result.data.toString()
                callback.invoke(result.data)
                latch.countDown()
            }

            override fun failure(exception: TwitterException) {
                println("errrrrrrr")
            }
        })
        try {
            latch.await()
        }catch (e: Exception){

        }

        println("end getUserInfo")
        return@async resultstr
    }

    fun goTimeLine(view: View){
        val intent =  Intent(this, TimeLineActivity::class.java)
        startActivity(intent)
    }


    fun getUserImage(view: View){
        val list = mutableListOf<Account>()
        list.add(Account("","","",0,null,null,false,"",null))
        list.save(filesDir)
    }

    fun pushLocal(view: View){
        println("Push Local")
        val intent = Intent(this, NotificationReceiver::class.java)
        intent.action = ACTION_LOCAL_PUSH
        val sender = PendingIntent.getBroadcast(this, REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(0, 0, sender)
    }

}
