package com.kotato.multitimelineclient.SNSService

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.kotato.multitimelineclient.BuildConfig
import com.kotato.multitimelineclient.model.*
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.IOException


/**
 * Created by kotato on 2017/07/11.
 */

object MastodonService : SNNService {
    val redirectUrl = "multiclient://callback"

    override fun authlize(view: View, callback: (Any) -> Unit): Deferred<Any?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var token: MastodonToken? = MastodonToken.get()

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
            .build().create(MastodonHttpService::class.java)

    var userSession = null


    enum class MEDIA_TYPE(val cagotery: String, val typeArr: List<String>) {

        IMAGE_TYPE("image", listOf("photo")),
        MOVIE_TYPE("movie", listOf("animated_gif", "video"));

        fun includes(type: String): Boolean {
            return this.typeArr.contains(type)
        }

        companion object {
            fun get(type: String): MEDIA_TYPE? {
                return if (IMAGE_TYPE.includes(type)) {
                    IMAGE_TYPE
                } else if (MOVIE_TYPE.includes(type)) {
                    MOVIE_TYPE
                } else {
                    null
                }
            }
        }

    }

    /***
     * ログインユーザのホームタイムラインを取得
     * @param id
     */
    override fun getHomeTimeLine(id: Long?) = async(CommonPool) {
        Log.d("Call Service", "Get Twitter HomeTimeLine from $id")
        var timeLineItems: List<TimeLineItem> = listOf()

        return@async timeLineItems
    }

    /***
     * ログインユーザへのメンションを取得
     * @param id
     *
     */
    override fun getMentions(id: Long?) = async(CommonPool) {
        Log.d("Call Service", "Get Twitter Mentions from $id")
        var timeLineItems: List<TimeLineItem> = listOf()
        return@async timeLineItems
    }

    /**
     * ログインユーザのファボリストを取得
     */
    override fun getFavorites() = async(CommonPool) {
        Log.d("Call Service", "Get Twitter Favorites")
        var timeLineItems: List<TimeLineItem> = listOf()
        return@async timeLineItems
    }

    private fun convertToMedia(it: Tweet): Medias? {
        val media = it.extendedEntities?.media?.find { it.type != null }
        var medias: Medias? = null
        media?.apply {
            val mediaType = MEDIA_TYPE.get(media?.type)
            val mediaUrls: List<String>? = when (mediaType) {
                MEDIA_TYPE.IMAGE_TYPE -> it.extendedEntities?.media?.filter { mediaType.includes(it.type) }?.map { it.mediaUrlHttps }
                MEDIA_TYPE.MOVIE_TYPE -> it.extendedEntities?.media?.filter { mediaType.includes(it.type) }
                        //mp4がないことは想定はしてない
                        ?.map { it.videoInfo.variants.find { it.contentType == "video/mp4" }?.url ?: it.videoInfo.variants[0].url }
                else -> listOf()
            }

            if (mediaType != null && mediaUrls != null) {
                medias = Medias(mediaType.cagotery, mediaUrls)
            }
        }
        return medias
    }

    /**
     * HTTPを通して画像の取得
     */
    override fun getImage(urlStr: String, callback: (Bitmap?) -> Unit) = async(CommonPool) {
        println("start getImage")
        var bitmap: Bitmap? = null
        try {
            val request = Request.Builder().url(urlStr).build()
            val response = OkHttpClient().newCall(request).execute()
            bitmap = BitmapFactory.decodeStream(response.body()?.byteStream())
            callback(bitmap)
        } catch(e: IOException) {
            e.printStackTrace()
        } catch(e: JSONException) {
            e.printStackTrace()
        }
        println("end getImage")
        return@async bitmap
    }


    /**
     * 認証されているユーザ情報を取得する
     */
    override fun getUserInfo(callback: (Account?) -> Unit) = async(CommonPool) {
        var account: Account? = null
        return@async account
    }

    fun authlize() = async(CommonPool) {
        if (token == null) {
            registerClient("MultiTimeLineClient", redirectUrl, "read write follow").await()
        }
        return@async token
    }

    fun registerClient(appName: String, registerUri: String, scopes: String) = async(CommonPool) {
        val call = service.registerClient(appName, registerUri, scopes)
        try {
            val result = call.execute()
            //エラーだとボディがからになる？
            token = result?.body()
            token?.save()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    interface MastodonHttpService {
        @FormUrlEncoded
        @POST("api/v1/apps")
        fun registerClient(@Field("client_name") clientName: String, @Field("redirect_uris") redirectUri: String, @Field("scopes") scope: String): Call<MastodonToken>

        @FormUrlEncoded
        @POST("oauth/authorize")
        fun authlize(@Field("client_id") clientId: String, @Field("redirect_uris") redirectUri: String, @Field("scopes") scope: String, @Field("response_type") responseType: String = "code"): Call<MastodonUserToken>


    }

}


