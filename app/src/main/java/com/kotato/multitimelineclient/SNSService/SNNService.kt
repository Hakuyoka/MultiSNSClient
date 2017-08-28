package com.kotato.multitimelineclient.SNSService

import android.graphics.Bitmap
import android.view.View
import com.kotato.multitimelineclient.model.Account
import com.kotato.multitimelineclient.model.TimeLineItem
import kotlinx.coroutines.experimental.Deferred

/**
 * Created by kotato on 2017/07/11.
 */

interface SNNService{
    fun getHomeTimeLine(id: Long? = null): Deferred<List<TimeLineItem>>
    fun getMentions(id: Long? = null): Deferred<List<TimeLineItem>>
    fun getFavorites(): Deferred<List<TimeLineItem>>
    fun getImage(urlStr: String, callback:(Bitmap?) -> Unit) : Deferred<Bitmap?>
    fun getUserInfo(callback:(Account?) -> Unit): Deferred<Account?>
    fun authlize(view: View, callback: (Any) -> Unit): Deferred<Any?>
}


enum class SNSType(val id: Id) : Int {
    TWITTER(0),
    MASTODON(1);

    companion object {
        fun create(id: Int): SNSType? {
            return when (id) {
                SNSType.TWITTER.id -> SNSType.TWITTER
                SNSType.MASTODON.id -> SNSType.MASTODON
                else -> null
            }
        }
    }
}
