package com.kotato.multitimelineclient.AccountManage

import android.media.Image
import com.twitter.sdk.android.R
import android.graphics.drawable.Drawable
import android.util.Log
import java.io.File
import java.io.Serializable


/**
 * Created by kotato on 2017/07/04.
 */
private val gson = com.google.gson.Gson()

fun getAccountLisst(filesDir: File):List<Account>{
    val filePath =  filesDir.path +"/"+ Account.CONFIG_PATH
    val resultList = mutableListOf<Account>()
    var file = File(filePath).absoluteFile

    if(!file.isFile) return resultList

    File(filePath).absoluteFile.forEachLine {
        Log.d("conver to Json", it)
        val account = gson.fromJson(it, Account::class.java)
        resultList.add(account)
    }
    return resultList
}


data class Account(val id: String, val name: String, val email: String? = "", val type: Int = 0,
                   val image: Drawable? = null, val serviceIcon : Drawable? = null, var isSelected : Boolean = false, val imageUrl: String = ""): Serializable{
    companion object {
        val CONFIG_PATH = "ACCOUNTS.txt"
    }
    init {

    }

    override fun toString(): String {
        return "id = $id, name = $name, email = $email, type = $type, imageUrl = $imageUrl, iSelected = $isSelected"
    }

    fun save(filesDir: File){
        val context = gson.toJson(this)
        val filePath = filesDir.path +"/"+ CONFIG_PATH
        File(filePath).absoluteFile.appendText(context+"\n")
        Log.d("save",context)
    }

}

