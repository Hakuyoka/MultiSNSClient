package com.kotato.multitimelineclient.AccountManage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.Service.TwitterService
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import com.twitter.sdk.android.core.models.User
import kotlinx.coroutines.experimental.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import java.io.File
import java.io.IOException
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
                TwitterService.getUserInfo { accout ->
                    val intent = Intent(activity, AccountsMangeActivity::class.java)
                    intent.putExtra("Account", accout)

                    val latch = CountDownLatch(1)
                    if (accout != null){

                        TwitterService.getImage(accout.imageUrl) {
                            bitmap ->
                            val filePath = filesDir.path + "/" + accout.id.toString() + "_0" + ".png"
                            val outStream = File(filePath).absoluteFile.outputStream()
                            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                            Log.d("image save", filePath)
                            latch.countDown()
                        }

                        try {
                            latch.await()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
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
    }
}
