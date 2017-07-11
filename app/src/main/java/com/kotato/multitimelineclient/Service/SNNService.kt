package com.kotato.multitimelineclient.Service

import android.graphics.Bitmap
import android.view.View
import com.kotato.multitimelineclient.AccountManage.Account
import com.kotato.multitimelineclient.TimeLine.TimeLineItem
import kotlinx.coroutines.experimental.Deferred

/**
 * Created by kotato on 2017/07/11.
 */

interface SNNService{
    fun getTimeLine(callback : (List<TimeLineItem>) -> Unit) {}
    fun getImage(urlStr: String, callback:(Bitmap?) -> Unit) {}
    fun getUserInfo(callback:(Account?) -> Unit) {}
    fun authlize(view: View, callback: (Any) -> Unit){}
}

