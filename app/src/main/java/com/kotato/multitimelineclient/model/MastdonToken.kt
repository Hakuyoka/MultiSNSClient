package com.kotato.multitimelineclient.model

import android.util.Log
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import com.kotato.multitimelineclient.OrmaHolder
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

/**
 * Created by kotato on 2017/08/22.
 */

@Table
data class MastodonToken(@Setter("id") @Column val id: String, @Setter("redirectUri") @Column val redirectUri: String,
                         @Setter("clientId") @Column val clientId: String, @Setter("clientSecret") @Column val clientSecret: String,
                         @Setter("key") @PrimaryKey(autoincrement = true) var key: Long = 0L) {

    companion object {
        fun get(): MastodonToken? {
            val result = OrmaHolder.ORMA.selectFromMastodonToken().toList()
            if (result.isEmpty()) return null
            return result.first()
        }
    }

    fun save() {
        val self = this
        launch(CommonPool) {
            OrmaHolder.ORMA.insertIntoMastodonToken(self)
            Log.d("save", self.toString())
        }
    }

}

@Table
data class MastodonUserToken(
        @Setter("accessToken") @Column val accessToken: String,
        @Setter("tokenType") @Column val tokenType: String,
        @Setter("scope") @Column val scope: String,
        @Setter("createdAt") @Column val createdAt: String,
        @Setter("key") @PrimaryKey(autoincrement = true) var key: Long = 0L) {
    fun save() {
        val self = this
        launch(CommonPool) {
            OrmaHolder.ORMA.insertIntoMastodonUserToken(self)
            Log.d("save", self.toString())
        }
    }
}
