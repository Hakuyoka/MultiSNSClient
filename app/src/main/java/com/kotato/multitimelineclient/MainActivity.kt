package com.kotato.multitimelineclient

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import com.kotato.multitimelineclient.AccountManage.AccountsMangeActivity
import com.kotato.multitimelineclient.AccountManage.getAccountList
import com.kotato.multitimelineclient.Push.ACTION_LOCAL_PUSH
import com.kotato.multitimelineclient.Push.NotificationReceiver
import com.kotato.multitimelineclient.Push.PushService
import com.kotato.multitimelineclient.Push.REQ_CODE
import com.kotato.multitimelineclient.SNSService.TwitterService
import com.kotato.multitimelineclient.TimeLine.TimeLineActivity
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import java.util.concurrent.CountDownLatch


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        //init Twitter
        val config = TwitterConfig.Builder(this)
                .logger(DefaultLogger(Log.DEBUG))
                .debug(true)
                .build()
        Twitter.initialize(config)

        //init Orma
        OrmaHolder.initialize(this)


//        Twitter.initialize(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = ""
        toolbar.setNavigationIcon(R.color.tw__composer_white)

        val options = BitmapFactory.Options().apply {
            inMutable = true
        }
        println(TwitterCore.getInstance().sessionManager.activeSession.userId)
        val accountList = getAccountList()
        if(accountList.size > 0){

            val targetAccount = accountList.find { (if(it.id == "") 0L else it.id?.toLong()) ==  TwitterCore.getInstance().sessionManager.activeSession.userId}
            var bitmap = BitmapFactory.decodeFile(filesDir.path + "/" + targetAccount?.id + "_" + targetAccount?.type + ".png",options)
            bitmap.density = 240
            val drawable = BitmapDrawable(resources,bitmap)
            println(drawable.minimumWidth)
            toolbar.navigationIcon = drawable
        }
        setSupportActionBar(toolbar)

        startService()
    }

    fun startService(){
        val intent = Intent(this, PushService::class.java)
        startService(intent)
    }

    fun ormaTest(){
//        val data = OrmaTestData(Random().nextLong(), Random().nextLong(), "test", "insert")
//        println(data)
//        launch(CommonPool){
//            OrmaHolder.ORMA.insertIntoOrmaTestData(data)
//
//            val read = OrmaHolder.ORMA.selectFromOrmaTestData()
//                    .execute()
//            println(read.count)
//            if(read.moveToFirst()){
//                do{
//                  val string = read.getString(read.getColumnIndex("content"))
//                  val id = read.getLong(read.getColumnIndex("id"))
//                  val test = read.getLong(read.getColumnIndex("test"))
//                  println(id.toString() + string)
//                } while (read.moveToNext())
//            }
//
//        }

//        val accounts = readAccountList(filesDir)
//        launch(CommonPool){
//            OrmaHolder.ORMA.deleteAll()
//            println(accounts.size)
//            accounts.forEach {
//                OrmaHolder.ORMA.insertIntoAccounts(it.dto)
//            }
//
//            val read = OrmaHolder.ORMA.selectFromAccounts()
//                    .execute()
//            println(read.count)
//            if(read.moveToFirst()){
//                do{
//                    val string = read.getString(read.getColumnIndex("id"))
//                    val id = read.getLong(read.getColumnIndex("key"))
//                    val name = read.getLong(read.getColumnIndex("name"))
//                    println(id.toString() + string + name)
//                } while (read.moveToNext())
//            }
//
//        }
//
//        println(getTimeList(TwitterCore.getInstance().sessionManager.activeSession.userId, TIME_LINE_TYPE.HOME.id))
//        launch(CommonPool) {
//            OrmaHolder.ORMA.insertIntoTimeLineItem(TimeLineItem(0, TwitterCore.getInstance().sessionManager.activeSession.userId, "name", "test", "icons",
//                    null, TwitterCore.getInstance().sessionManager.activeSession.userId, 0, Medias("image", listOf()).apply {
//                key = OrmaHolder.ORMA.insertIntoMedias(this)
//            }, 9))
//        }

    }

    fun stopService(){
        val intent = Intent(this, PushService::class.java)
        stopService(intent)
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
////        Log.d("AsyncTest","end")
//        val REQUEST_CODE = 1
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
//        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//
//            // Android 6.0 のみ、該当パーミッションが許可されていない場合
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                // パーミッションが必要であることを明示するアプリケーション独自のUIを表示
//            }
//
//        } else {
//            // 許可済みの場合、もしくはAndroid 6.0以前
//            // パーミッションが必要な処理
//        }
//
//
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.type = "image/*"
//        startActivityForResult(intent,1101)

//        TwitterService.getMentions {  }
//
//        val intent =  Intent(this, MediaTabbedActivity::class.java)
//        intent.putExtra("uris", arrayListOf("1","2","3","3"))
//        startActivity(intent)

        stopService()
        ormaTest()
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



    fun pushLocal(view: View){
        println("Push Local")
        val intent = Intent(this, NotificationReceiver::class.java)
        intent.action = ACTION_LOCAL_PUSH
        val sender = PendingIntent.getBroadcast(this, REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(0, 0, sender)

        launch(CommonPool){
            OrmaHolder.ORMA.deleteFromTimeLineItem().ownerEq(TwitterCore.getInstance().sessionManager.activeSession.userId).execute()
        }
    }

}
