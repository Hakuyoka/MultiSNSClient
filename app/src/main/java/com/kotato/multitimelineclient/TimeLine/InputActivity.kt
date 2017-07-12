package com.kotato.multitimelineclient.TimeLine

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.Service.TwitterService
import com.kotato.multitimelineclient.getPathFromUri
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

class InputActivity : AppCompatActivity() {

    var mediaUriList: MutableList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        mediaUriList = mutableListOf()

        val imageView = findViewById(R.id.user_icon) as ImageView
        imageView.setImageResource(R.drawable.tw__composer_logo_blue)


        val submitButton = findViewById(R.id.submit_button) as Button
        val editText = findViewById(R.id.editText) as EditText
        submitButton.setOnClickListener {
            view ->
                Log.d("Submit Text", editText.text.toString())
                submit(editText.text.toString(), {})
        }

        val imageUploadButton = findViewById(R.id.image_upload_button)
        imageUploadButton.setOnClickListener {
            view ->
                Log.d("Upload Image", "")
                selectImage()
        }
    }

    fun submit(text: String ,callback:(Tweet) -> Unit) = async(CommonPool){
        val twitterApiClient = TwitterApiClient(TwitterCore.getInstance().sessionManager.activeSession)
        val statusesService = twitterApiClient?.statusesService

        //イメージファイルのアップロード
        var medisIdStr:String? = null
        if(mediaUriList?.size ?: 0 > 0){
            medisIdStr = mediaUriList?.map {
                TwitterService.uploadMedia(it){}
            }?.map {
                it.await()
            }?.reduce { acc, s -> acc + "," + s }
        }

        //ツイートを行う
        val call = statusesService?.update(text,  null, false, null, null, null, false, null, medisIdStr)
        call?.enqueue(object : Callback<Tweet>() {
            override fun success(result: Result<Tweet>) {
                Log.i("Tweet", result.data.text)
                var intent = Intent()
                setResult(Activity.RESULT_OK,intent)
                finish()
            }

            override fun failure(exception: TwitterException) {
                var intent = Intent()
                setResult(Activity.RESULT_CANCELED,intent)
                finish()
            }
        })
        return@async
    }


    fun selectImage(){
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
                uri = data?.data
                Log.d("Select Media", getPathFromUri(this, uri))
                //クラウド上のファイルなどが渡された場合は変換ができないため一度コンバートする
                val uriText = getPathFromUri(this, uri)
                if(uriText != null) {
                    mediaUriList?.add(uriText)
                }
            }
        }
    }


}
