package com.kotato.multitimelineclient.AccountManage

import android.media.Image
import com.twitter.sdk.android.R
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.TwitterSession
import java.io.*


/**
 * Created by kotato on 2017/07/04.
 */
private val gson = com.google.gson.Gson()

val CONFIG_PATH = "ACCOUNTS.txt"
val ACCOUNT_OBJ_FILE = "ACCOUNTS.obj"


fun getAccountList(filesDir: File):List<Account>{
    val filePath =  filesDir.path +"/"+ CONFIG_PATH
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

fun List<Account>.save(filesDir: File){
    //シリアライズオブジェクトの保存
    val objPath = filesDir.path +"/"+ ACCOUNT_OBJ_FILE
    Log.d("Save Account Object",objPath)
    val baos = ByteArrayOutputStream()
    val oos = ObjectOutputStream(baos)
    oos.writeObject(this)
    oos.close()

    val os = File(objPath).absoluteFile.outputStream()
    os.write(baos.toByteArray())
    os.close()
}

fun readAccountList(filesDir: File): List<Account>{
    val objPath = filesDir.path +"/"+ ACCOUNT_OBJ_FILE
    val objFile = File(objPath).absoluteFile

    if(!objFile.isFile) return mutableListOf()

    var bais = ByteArrayInputStream(objFile.readBytes())
    val ois = ObjectInputStream(bais)

    return ois.readObject() as List<Account>
}

data class Account(val id: String, val name: String, val email: String? = "", val type: Int = 0,
                   val image: Drawable? = null, val serviceIcon : Drawable? = null, var isSelected : Boolean = false, val imageUrl: String = "",
                   var twitterSession: String? = null): Serializable{

    override fun toString(): String {
        return "id = $id, name = $name, email = $email, type = $type, imageUrl = $imageUrl, iSelected = $isSelected"
    }

    fun save(filesDir: File){
        val context = gson.toJson(this)
        val filePath = filesDir.path +"/"+ CONFIG_PATH
        File(filePath).absoluteFile.appendText(context+"\n")
        Log.d("save",context)
    }

    override fun equals(other: Any?): Boolean {
        return (other as? Account)?.id == this.id
    }

}

