package com.kotato.multitimelineclient.SNSService

import android.graphics.Bitmap
import android.view.View
import com.kotato.multitimelineclient.AccountManage.Account
import com.kotato.multitimelineclient.TimeLine.TimeLineItem
import kotlinx.coroutines.experimental.Deferred

/**
 * Created by kotato on 2017/07/11.
 */

interface SNNService{
    fun getTimeLine(id: Long? = null) : Deferred<List<TimeLineItem>>
    fun getMentions(callback: (List<TimeLineItem>) -> Unit) : Deferred<List<TimeLineItem>>
    fun getImage(urlStr: String, callback:(Bitmap?) -> Unit) : Deferred<Bitmap?>
    fun getUserInfo(callback:(Account?) -> Unit): Deferred<Account?>
    fun authlize(view: View, callback: (Any) -> Unit): Deferred<Any?>
}

