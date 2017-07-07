package com.kotato.multitimelineclient.AccountManage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.JsonObject

import com.kotato.multitimelineclient.R
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import com.twitter.sdk.android.core.models.User
import kotlinx.coroutines.experimental.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.CountDownLatch


class SelectService : AppCompatActivity() {

    var loginButton : TwitterLoginButton? = null
    var twitterApiClient : TwitterApiClient? = null
    val client = OkHttpClient()

    init {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_service)
        val authClient = TwitterAuthClient()
        authClient.cancelAuthorize()
        val activity = this
        loginButton = findViewById(R.id.login_button) as TwitterLoginButton
        loginButton?.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>){
                TwitterCore.getInstance().sessionManager.activeSession =  result.data
                getUserInfo{ user: User ->
                    val intent = Intent(activity, AccountsMangeActivity::class.java)
                    intent.putExtra("userId",user.id)
                    intent.putExtra("userName",user.name)
                    intent.putExtra("imageIconUrl",user.profileImageUrlHttps)
                    intent.putExtra("Account",Account(user.id.toString(),user.name,user.email,0,null,null,true,user.id.toString()+"_0.png"))

                    //TODO SyncTackかawait/asyncで書き直す
                    val latch = CountDownLatch(1)
                    getUserImage(user.profileImageUrlHttps, user.id.toString()+"_0"){
                        latch.countDown()
                    }
                    try {
                        latch.await()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
            }

            override fun failure(exception: TwitterException) {
                authClient.cancelAuthorize()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        loginButton?.onActivityResult(requestCode, resultCode, data)
        val account = data.getLongExtra("userId",0L)
        if(account != null){
            val fragment = fragmentManager.findFragmentByTag("fragment_main")
            if(fragment != null){
                val flag = fragment is AccountListFragment
                Log.d("fragment",flag.toString())
            }
            val supportFragment = supportFragmentManager.findFragmentByTag("fragment_main")
            if(supportFragment != null){
                val flag = fragment is AccountListFragment
                Log.d("fragment",flag.toString())
            }
        }
    }

    /**
     * 認証されているユーザ情報を取得する
     */
    fun getUserInfo(callback:(User) -> Unit) = async(CommonPool){
        twitterApiClient = TwitterApiClient(TwitterCore.getInstance().sessionManager.activeSession)
        val statusesService = twitterApiClient?.accountService
        val call = statusesService?.verifyCredentials(false, false, false)
        runBlocking {
            call?.enqueue(object : Callback<User>() {
                override fun success(result: Result<User>) {
                    Log.i("Users", result.data.profileImageUrl)
                    callback.invoke(result.data)
                }

                override fun failure(exception: TwitterException) {

                }
            })
        }
        return@async
    }

    fun getUserImage(urlStr: String, fileName: String, callback:() -> Unit) : Deferred<Unit> =  async(CommonPool){
        object: AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void): String {
                var res: String = ""
                try {
                    val request = Request.Builder().url(urlStr).build()
                    val response = OkHttpClient().newCall(request).execute()
                    val filePath = filesDir.path +"/"+ fileName +".png"
                    val outStream = File(filePath).absoluteFile.outputStream()
                    val bitmap = BitmapFactory.decodeStream(response.body()?.byteStream())
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                    Log.d("image save", filePath)
                    callback()
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


}
