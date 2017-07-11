package com.kotato.multitimelineclient.TimeLine

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

import com.kotato.multitimelineclient.R
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

class InputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        val imageView = findViewById(R.id.user_icon) as ImageView
        imageView.setImageResource(R.drawable.tw__composer_logo_blue)

        val submitButton = findViewById(R.id.submit_button) as Button


        val editText = findViewById(R.id.editText) as EditText
        submitButton.setOnClickListener {
            view ->
                Log.d("Submit Text", editText.text.toString())
                submitText(editText.text.toString(), {})

        }
    }

    fun submitText(text: String ,callback:(User) -> Unit) = async(CommonPool){
        val twitterApiClient = TwitterApiClient(TwitterCore.getInstance().sessionManager.activeSession)
        val statusesService = twitterApiClient?.statusesService
        val call = statusesService?.update(text,  null, false, null, null, null, false, null, null)
        call?.enqueue(object : Callback<Tweet>() {
            override fun success(result: Result<Tweet>) {
                Log.i("Tweet", result.data.text)

                var intent = Intent()
                setResult(Activity.RESULT_OK,intent)
                finish()
            }

            override fun failure(exception: TwitterException) {

            }
        })
        return@async
    }

}
