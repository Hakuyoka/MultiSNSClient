package com.kotato.multitimelineclient.model

import android.graphics.drawable.Drawable
import android.support.annotation.Nullable
import android.util.Log
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import com.kotato.multitimelineclient.OrmaHolder
import com.kotato.multitimelineclient.SNSService.SNSType
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.io.Serializable


/**
 * Created by kotato on 2017/07/04.
 */
private val gson = com.google.gson.Gson()

val ACCOUNT_OBJ_FILE = "ACCOUNTS.obj"


fun getAccountList():List<Account>{
    var accounts = OrmaHolder.ORMA.selectFromAccounts()
    return accounts.toList().map {
        Account(it.id, it.name, it.email, SNSType.create(it.type ?: 0) ?: SNSType.TWITTER, null, null, false, it.imageUrl, it.twitterSession)
    }
}

data class Account(val id: String, val name: String, val email: String? = "", val type: SNSType = SNSType.TWITTER,
                   val image: Drawable? = null, val serviceIcon : Drawable? = null, var isSelected : Boolean = false, val imageUrl: String = "",
                   var twitterSession: String? = null): Serializable{

    val dto : Accounts
        get() = Accounts(0L, this.id, this.name, this.email ?: "", this.type.id, this.imageUrl, this.twitterSession)

    override fun toString(): String {
        return "id = $id, name = $name, email = $email, type = $type, imageUrl = $imageUrl, iSelected = $isSelected"
    }

    fun save(){
        launch(CommonPool){
            OrmaHolder.ORMA.insertIntoAccounts(dto)
            Log.d("save",dto.toString())
        }
    }


    override fun equals(other: Any?): Boolean {
        return (other as? Account)?.id == this.id
    }

    @Table
    data class Accounts(
            @Setter("key") @PrimaryKey(autoincrement = true) var key: Long,
            @Setter("id") @Column val id: String,
            @Setter("name") @Column val name: String,
            @Setter("email") @Column @Nullable val  email: String?,
            @Setter("type") @Column @Nullable val type: Int?,
            @Setter("imageUrl") @Column val imageUrl: String = "",
            @Setter("twitterSession") @Column @Nullable var twitterSession: String? = null
    )

}