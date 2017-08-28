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
import android.util.Log
import android.view.Menu
import android.view.View
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.kotato.multitimelineclient.AccountManage.AccountsMangeActivity
import com.kotato.multitimelineclient.Push.ACTION_LOCAL_PUSH
import com.kotato.multitimelineclient.Push.NotificationReceiver
import com.kotato.multitimelineclient.Push.PushService
import com.kotato.multitimelineclient.Push.REQ_CODE
import com.kotato.multitimelineclient.SNSService.MastodonService
import com.kotato.multitimelineclient.SNSService.TwitterService
import com.kotato.multitimelineclient.TimeLine.TimeLineActivity
import com.kotato.multitimelineclient.model.getAccountList
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.core.TwitterCore
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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

        val options = BitmapFactory.Options().apply {
            inMutable = true
        }
        val accountList = getAccountList()
        if(accountList.size > 0){

            val targetAccount = accountList.find { (if(it.id == "") 0L else it.id?.toLong()) ==  TwitterCore.getInstance().sessionManager.activeSession.userId}
            var bitmap = BitmapFactory.decodeFile(filesDir.path + "/" + targetAccount?.id + "_" + targetAccount?.type + ".png",options)
            bitmap.density = 240
            val drawable = BitmapDrawable(resources,bitmap)
            println(drawable.minimumWidth)
        }

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
//        MastodonService.authlize(this, {})
//        MastodonService.token
//        val webView = findViewById(R.id.web_view) as WebView
//        webView.webViewClient = WebViewClient()
//        webView.loadUrl("https://mstdn.jp/oauth/authorize?client_id=9fdf8679dd5df6a5779c8c2c57bca000b9e2c45ed988a0ef934e35d885544c2c&response_type=code&redirect_uri=urn:ietf:wg:oauth:2.0:oob&scope=read%20write%20follow")
//        val uri = Uri.parse("https://mstdn.jp/oauth/authorize?client_id=8ca7a07d6c82932a10571298754ab491a34c98f302960eef13c7f18c33b8998c&response_type=code&redirect_uri=multiclient://callback&scope=read%20write%20follow")
////        val intent =  Intent(this, WebViewActivity::class.java)
//        val intent = Intent(Intent.ACTION_VIEW, uri)
//        startActivityForResult(intent, 200)


        val client = OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
        }

        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create() // NamingPoricy そ指定する

        val service = Retrofit.Builder()
                .baseUrl("https://mstdn.jp")
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(MastodonService.MastodonHttpService::class.java)

        val credit = service.verifyCredentials("4d6aa2db28f8ba9ca7894ee974764f97b2e6b8460a62eec18c4619cffdd00937")
        println(gson.toJson(credit))
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
            OrmaHolder.ORMA.deleteFromMastodonToken().keyGe(0L).execute()
        }
    }

}
