package com.kotato.multitimelineclient.TimeLine

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import com.kotato.multitimelineclient.AccountManage.Account
import com.kotato.multitimelineclient.OrmaHolder
import com.twitter.sdk.android.core.TwitterCore


/**
 * Created by kotato on 2017/07/06.
 */

enum class TIME_LINE_TYPE(val id: Int){
    HOME(0),
    MENTION(1),
    FAVORITE(2)
}

fun getTimeList(userId: Long, type: Int, limit: Long = 40) : List<TimeLineItem>{
    return OrmaHolder.ORMA.selectFromTimeLineItem()
            .ownerEq(userId)
            .typeEq(type)
            .limit(limit)
            .toList()
}
fun List<TimeLineItem>.save(){
    OrmaHolder.ORMA.transactionSync {
        val inserter = OrmaHolder.ORMA.prepareInsertIntoTimeLineItem()
        inserter.executeAll(this)
    }
}

@Table
data class TimeLineItem(
        @Setter("id") @Column val id: Long,
        @Setter("userId") @Column(indexed = true) val userId: Long,
        @Setter("userName") @Column val userName: String,
        @Setter("text") @Column val text: String,
        @Setter("userIcon") @Column  val userIcon: String,
        @Setter("mediaUrls") @Column val mediaUrls: List<String>? = null,
        @Setter("owner") @Column(indexed = true) val owner: Long? = TwitterCore.getInstance().sessionManager.activeSession.userId,
        @Setter("type") @Column(indexed = true) val type: Int ,
        @Setter("key") @PrimaryKey(autoincrement = false) val key: Long = 0L)
