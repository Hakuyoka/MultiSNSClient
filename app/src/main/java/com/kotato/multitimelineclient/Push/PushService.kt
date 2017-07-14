package com.kotato.multitimelineclient.Push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.support.v7.app.NotificationCompat
import com.kotato.multitimelineclient.MainActivity
import com.kotato.multitimelineclient.R


/**
 * Created by kotato on 2017/07/14.
 */
val REQ_CODE = 9999
val INTENT_KEY_LOCAL_PUSH = "LOCAL_PUSH"
val ACTION_LOCAL_PUSH = "com.kotato.localpush"
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("Receiver!")
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.tw__ic_logo_white)
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.putExtra(INTENT_KEY_LOCAL_PUSH, true)
        val builder = NotificationCompat.Builder(context)
        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.drawable.tw__ic_logo_white)//ないと通知が届かない
        builder.setLargeIcon(largeIcon)
        builder.setTicker("ticker")
        builder.setContentTitle("タイトル")
        builder.setContentText("本文")
        val contentIntent = PendingIntent.getActivity(context, REQ_CODE, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        builder.setContentIntent(contentIntent)
        // タップで通知領域から削除する
        builder.setAutoCancel(true)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }

}