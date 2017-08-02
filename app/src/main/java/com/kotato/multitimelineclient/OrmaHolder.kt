package com.kotato.multitimelineclient

/**
 * Created by kotato on 2017/08/02.
 */
import android.content.Context
import com.kotato.multitimelineclient.AccountManage.OrmaDatabase

object OrmaHolder {
    lateinit var ORMA: OrmaDatabase;

    fun initialize(context: Context) {
        ORMA = OrmaDatabase.builder(context).build();
    }
}
