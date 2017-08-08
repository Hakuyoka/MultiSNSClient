package com.kotato.multitimelineclient.TimeLine

import android.support.annotation.Nullable
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import com.kotato.multitimelineclient.OrmaHolder
import com.twitter.sdk.android.core.TwitterCore


/**
 * Created by kotato on 2017/07/06.
 */

enum class TIME_LINE_TYPE(val id: Int) {
    HOME(0),
    MENTION(1),
    FAVORITE(2);

    companion object {
        fun create(id: Int): TIME_LINE_TYPE? {
            return when (id) {
                HOME.id -> HOME
                MENTION.id -> MENTION
                FAVORITE.id -> FAVORITE
                else -> null
            }
        }
    }

}

fun getTimeList(userId: Long, type: Int, limit: Long = 40): List<TimeLineItem> {
    return OrmaHolder.ORMA.selectFromTimeLineItem()
            .ownerEq(userId)
            .typeEq(type)
            .limit(limit)
            .toList()
}

fun List<TimeLineItem>.save() {
    OrmaHolder.ORMA.transactionSync {
        this.forEach {
            // Direct Associationができるはずだけどできなかったのでこっちで
            val mediaId = it.media?.apply {
                this.key = OrmaHolder.ORMA.insertIntoMedias(this)
            }
            OrmaHolder.ORMA.insertIntoTimeLineItem(it.apply {
                media = mediaId
            })
        }
    }
}

@Table
data class TimeLineItem(
        @Setter("id") @Column val id: Long,
        @Setter("userId") @Column(indexed = true) val userId: Long,
        @Setter("userName") @Column val userName: String,
        @Setter("text") @Column val text: String,
        @Setter("userIcon") @Column val userIcon: String,
        @Setter("mediaUrls") @Column val mediaUrls: List<String>? = null,
        @Setter("owner") @Column(indexed = true) val owner: Long? = TwitterCore.getInstance().sessionManager.activeSession.userId,
        @Setter("type") @Column(indexed = true) val type: Int,
        @Setter("media") @Nullable @Column(indexed = true) var media: Medias? = null,
        @Setter("key") @PrimaryKey(autoincrement = true) val key: Long = 0L)

@Table
data class Medias(
        @Setter("type") @Column val type: String,
        @Setter("urls") @Column val urls: List<String>,
        @Setter("key") @PrimaryKey var key: Long = 0L)