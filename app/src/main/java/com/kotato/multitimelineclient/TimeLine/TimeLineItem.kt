package com.kotato.multitimelineclient.TimeLine

/**
 * Created by kotato on 2017/07/06.
 */

data class TimeLineItem(val id: Long, val userId: Long, val userName: String, val text: String, val userIcon: String, val mediaUrls: List<String>? = null)
